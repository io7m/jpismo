package com.io7m.jpismo;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.lwjgl.opengl.GL11;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.PixelUnpackBufferWritableMap;
import com.io7m.jcanephora.Texture2DRGBAStatic;
import com.io7m.jcanephora.TextureFilter;
import com.io7m.jcanephora.TextureWrap;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
import com.io7m.jrpack.Pack1D;
import com.io7m.jrpack.PackResult;
import com.io7m.jrpack.PackResult.PackOK;
import com.io7m.jrpack.Rectangle;

/**
 * <p>
 * The <code>VariableTextRenderer</code> class implements a renderer for text
 * using variable-width fonts.
 * </p>
 * <p>
 * Internally, the renderer breaks incoming text into words and caches these
 * into texture atlases.
 * </p>
 * 
 */

public final class VariableTextRenderer implements TextRenderer
{
  /**
   * A WordAtlas represents a large OpenGL texture containing many packed
   * words.
   */

  private final class WordAtlas
  {
    private final @Nonnull Texture2DRGBAStatic        texture;
    private final @Nonnull Pack1D                     packer;
    private final @Nonnull HashMap<String, Rectangle> rectangles;
    private final @Nonnull BufferedImage              bitmap;
    private final @Nonnull Graphics2D                 graphics;
    private final @Nonnull Log                        atlas_log;
    private boolean                                   dirty;

    @SuppressWarnings("synthetic-access") WordAtlas(
      final @Nonnull Log log)
      throws GLException,
        ConstraintError
    {
      this.atlas_log = log;
      this.texture =
        VariableTextRenderer.this.gl.allocateTextureRGBAStatic(
          "word_atlas" + VariableTextRenderer.this.atlases.size(),
          VariableTextRenderer.this.texture_size,
          VariableTextRenderer.this.texture_size,
          TextureWrap.TEXTURE_WRAP_REPEAT,
          TextureWrap.TEXTURE_WRAP_REPEAT,
          TextureFilter.TEXTURE_FILTER_NEAREST,
          TextureFilter.TEXTURE_FILTER_NEAREST);

      this.packer =
        new Pack1D(
          VariableTextRenderer.this.texture_size,
          VariableTextRenderer.this.texture_size,
          VariableTextRenderer.this.getLineHeight());

      this.rectangles = new HashMap<String, Rectangle>();
      this.dirty = false;

      this.bitmap =
        new BufferedImage(
          VariableTextRenderer.this.texture_size,
          VariableTextRenderer.this.texture_size,
          BufferedImage.TYPE_4BYTE_ABGR);

      this.graphics = this.bitmap.createGraphics();
      this.graphics.setColor(Color.WHITE);
      this.graphics.setFont(VariableTextRenderer.this.font);

      if (this.atlas_log.enabled(Level.LOG_DEBUG)) {
        final StringBuilder builder = new StringBuilder();
        builder.append("cache ");
        builder.append(this);
        builder.append(" create");
        this.atlas_log.debug(builder.toString());
      }
    }

    @SuppressWarnings("synthetic-access") Rectangle cacheWord(
      final @Nonnull String word)
      throws ConstraintError,
        TextCacheException
    {
      /*
       * Word is already cached? Return the rectangle.
       */

      if (this.rectangles.containsKey(word)) {
        if (this.atlas_log.enabled(Level.LOG_DEBUG)) {
          final StringBuilder builder = new StringBuilder();
          builder.append("cache ");
          builder.append(this);
          builder.append(" exists '");
          builder.append(word);
          builder.append("'");
          this.atlas_log.debug(builder.toString());
        }
        return this.rectangles.get(word);
      }

      /*
       * Otherwise, pack the rectangle.
       */

      final int width =
        VariableTextRenderer.this.font_metrics.stringWidth(word)
          + VariableTextRenderer.PAD_PACK_BORDER;

      final PackResult result = this.packer.insert(width);
      switch (result.type) {
        case PACK_RESULT_OK:
        {
          if (this.atlas_log.enabled(Level.LOG_DEBUG)) {
            final StringBuilder builder = new StringBuilder();
            builder.append("cache ");
            builder.append(this);
            builder.append(" success '");
            builder.append(word);
            builder.append("'");
            this.atlas_log.debug(builder.toString());
          }

          final PackOK ok = (PackOK) result;
          assert (ok.rectangle.getWidth() == width);
          this.writeWord(word, ok.rectangle);
          this.rectangles.put(word, ok.rectangle);
          return ok.rectangle;
        }
        case PACK_RESULT_TOO_LARGE:
        {
          if (this.atlas_log.enabled(Level.LOG_DEBUG)) {
            final StringBuilder builder = new StringBuilder();
            builder.append("cache ");
            builder.append(this);
            builder.append(" word too large '");
            builder.append(word);
            builder.append("'");
            this.atlas_log.debug(builder.toString());
          }
          throw new TextCacheException(result.type, word);
        }
        case PACK_RESULT_OUT_OF_SPACE:
        {
          if (this.atlas_log.enabled(Level.LOG_DEBUG)) {
            final StringBuilder builder = new StringBuilder();
            builder.append("cache ");
            builder.append(this);
            builder.append(" out of space '");
            builder.append(word);
            builder.append("'");
            this.atlas_log.debug(builder.toString());
          }
          throw new TextCacheException(result.type, word);
        }
      }

      /* UNREACHABLE */
      throw new AssertionError("unreachable code!");
    }

    Texture2DRGBAStatic getTexture()
    {
      return this.texture;
    }

    boolean isDirty()
    {
      return this.dirty;
    }

    @Override public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("[WordAtlas '");
      builder.append(this.texture.getName());
      builder.append("']");
      return builder.toString();
    }

    @SuppressWarnings("synthetic-access") void upload()
      throws GLException,
        ConstraintError
    {
      final PixelUnpackBufferWritableMap map =
        VariableTextRenderer.this.gl.mapPixelUnpackBufferWrite(this.texture
          .getBuffer());

      try {
        final ByteBuffer target_buffer = map.getByteBuffer();
        final DataBufferByte source_buffer =
          (DataBufferByte) this.bitmap.getData().getDataBuffer();
        target_buffer.put(source_buffer.getData());
        target_buffer.rewind();
      } finally {
        VariableTextRenderer.this.gl.unmapPixelUnpackBuffer(this.texture
          .getBuffer());
      }

      VariableTextRenderer.this.gl.replaceTexture2DRGBAStatic(this.texture);
      this.dirty = false;
    }

    @SuppressWarnings("synthetic-access") private void writeWord(
      final @Nonnull String word,
      final @Nonnull Rectangle rectangle)
    {
      final int ascent = VariableTextRenderer.this.font_metrics.getAscent();
      final int x = rectangle.x0;
      final int y = rectangle.y0 + ascent;

      this.graphics.setColor(Color.WHITE);
      this.graphics.drawString(word, x, y);

      this.bitmap.flush();
      this.dirty = true;
    }
  }

  /**
   * Add PAD_PACK_BORDER to the width of the string returned by the java font
   * metrics, prior to packing. This is either required due to inaccurate font
   * metrics, or platform specific bugs.
   */

  private static final int PAD_PACK_BORDER = 1;

  private static String[] splitWords(
    final @Nonnull String line)
  {
    return line.split("\\s+");
  }

  private final @Nonnull GLInterface          gl;
  private final @Nonnull Font                 font;
  private final @Nonnull Log                  log;
  private final @Nonnull BufferedImage        base_image;
  private final @Nonnull Graphics2D           base_graphics;
  private final @Nonnull FontMetrics          font_metrics;
  private final @Nonnull ArrayList<WordAtlas> atlases;
  private final int                           font_space_width;

  private final int                           texture_size;

  public VariableTextRenderer(
    final @Nonnull GLInterface gl,
    final @Nonnull Font font,
    final @Nonnull Log log)
    throws ConstraintError,
      GLException
  {
    this.gl = Constraints.constrainNotNull(gl, "GL interface");
    this.font = Constraints.constrainNotNull(font, "Font");
    this.log =
      new Log(Constraints.constrainNotNull(log, "Log interface"), "vtext");

    this.atlases = new ArrayList<VariableTextRenderer.WordAtlas>();
    this.texture_size = this.decideTextureSize();

    /*
     * Initialize the bare minimum Java2D graphics context needed to measure
     * font metrics.
     */

    this.base_image = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
    this.base_graphics = this.base_image.createGraphics();
    this.font_metrics = this.base_graphics.getFontMetrics(this.font);
    this.font_space_width = this.font_metrics.stringWidth(" ");
  }

  @Override public void cacheLine(
    final @Nonnull String line)
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    this.cacheLineInner(Constraints.constrainNotNull(line, "Line"));
  }

  private void cacheLineInner(
    final @Nonnull String line)
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    final String[] words = VariableTextRenderer.splitWords(line);

    for (final String word : words) {
      if (word.equals("") == false) {
        this.cacheWord(word);
      }
    }
  }

  @Override public void cacheUpload()
    throws GLException,
      ConstraintError
  {
    for (final WordAtlas atlas : this.atlases) {
      if (atlas.isDirty()) {
        atlas.upload();
      }
    }
  }

  private @Nonnull Pair<WordAtlas, Rectangle> cacheWord(
    final @Nonnull String word)
    throws ConstraintError,
      GLException,
      TextCacheException
  {
    /*
     * Attempt to cache the given word one of the current atlases.
     */

    for (final WordAtlas atlas : this.atlases) {
      try {
        final Rectangle rectangle = atlas.cacheWord(word);
        return new Pair<WordAtlas, Rectangle>(atlas, rectangle);
      } catch (final TextCacheException e) {
        switch (e.getResultCode()) {
          case PACK_RESULT_OK:
            throw new AssertionError("unreachable code");
          case PACK_RESULT_OUT_OF_SPACE:
            continue;
          case PACK_RESULT_TOO_LARGE:
            throw e;
        }
      }
    }

    /*
     * None of the atlases could cache the word. Create a new atlas and cache.
     */

    final WordAtlas atlas = new WordAtlas(this.log);
    this.atlases.add(atlas);
    return new Pair<WordAtlas, Rectangle>(atlas, atlas.cacheWord(word));
  }

  /**
   * Dump the internal texture atlases to PNG files in the directory specified
   * by <code>directory</code>.
   * 
   * @param directory
   *          The output directory.
   * @throws FileNotFoundException
   *           Iff <code>directory</code> does not exist.
   * @throws IOException
   *           Iff an I/O error occurs whilst writing files.
   */

  @SuppressWarnings("synthetic-access") public void debugDumpAtlasImages(
    final @Nonnull String directory)
    throws FileNotFoundException,
      IOException
  {
    for (final WordAtlas atlas : this.atlases) {
      final String name = atlas.getTexture().getName();
      final String path = directory + "/" + name + ".png";
      this.log.debug("dumping " + path);

      final ImageOutputStream stream =
        ImageIO.createImageOutputStream(new FileOutputStream(path));
      ImageIO.write(atlas.bitmap, "png", stream);
      stream.flush();
      stream.close();
    }
  }

  /**
   * Decide the texture size to use. Attempt to use 1mb textures (2 ^ 10) but
   * use less if the implementation cannot support them.
   * 
   * @return The texture size.
   * @throws GLException
   *           Iff an OpenGL error occurs.
   */

  private int decideTextureSize()
    throws GLException
  {
    final int size = this.gl.getMaximumTextureSize();
    return Math.min(size, (int) Math.pow(2, 8));
  }

  @Override public int getLineHeight()
  {
    return this.font_metrics.getHeight();
  }

  @Override public int getTextWidth(
    final @Nonnull String line)
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    final String[] words = VariableTextRenderer.splitWords(line);
    int width = 0;

    for (int index = 0; index < words.length; ++index) {
      final Pair<WordAtlas, Rectangle> pair = this.cacheWord(words[index]);
      final Rectangle rect = pair.second;

      if ((index + 1) != words.length) {
        width += rect.getWidth() + this.font_space_width;
      } else {
        width += rect.getWidth();
      }
    }

    return width;
  }

  public void renderLine(
    final double x,
    final double y,
    final String line)
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    final String[] words = VariableTextRenderer.splitWords(line);
    double offset = 0.0;
    final double m = 1.0 / this.texture_size;

    for (final String word : words) {
      final Pair<WordAtlas, Rectangle> pair = this.cacheWord(word);
      final Rectangle rect = pair.second;

      final double u0 = rect.x0 * m;
      final double v0 = rect.y0 * m;
      final double u1 = (rect.x1) * m;
      final double v1 = (rect.y1 + 1) * m;

      final double x0 = x + offset;
      final double x1 = (x0 + rect.getWidth()) - 1;
      final double y0 = y;
      final double y1 = y + rect.getHeight();

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, pair.first
        .getTexture()
        .getLocation());

      GL11.glBegin(GL11.GL_QUADS);
      {
        GL11.glTexCoord2d(u0, v0);
        GL11.glVertex2d(x0, y1);

        GL11.glTexCoord2d(u0, v1);
        GL11.glVertex2d(x0, y0);

        GL11.glTexCoord2d(u1, v1);
        GL11.glVertex2d(x1, y0);

        GL11.glTexCoord2d(u1, v0);
        GL11.glVertex2d(x1, y1);
      }
      GL11.glEnd();

      offset = offset + rect.getWidth() + this.font_space_width;
    }
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[VariableTextRenderer [Font ");
    builder.append(this.font.getName());
    builder.append(" ");
    builder.append(this.font.getSize());
    builder.append("]]");
    return builder.toString();
  }
}
