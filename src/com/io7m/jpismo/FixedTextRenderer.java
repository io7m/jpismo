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
 * The <code>FixedTextRenderer</code> class implements a renderer for text
 * using fixed-width fonts.
 * </p>
 * <p>
 * Internally, the renderer breaks incoming text into individual characters
 * and caches these into texture atlases.
 * </p>
 * 
 */

public final class FixedTextRenderer implements TextRenderer
{
  /**
   * A CharAtlas represents a large OpenGL texture containing many packed
   * characters.
   */

  private final class CharAtlas
  {
    private final @Nonnull Texture2DRGBAStatic           texture;
    private final @Nonnull Pack1D                        packer;
    private final @Nonnull HashMap<Character, Rectangle> rectangles;
    private final @Nonnull BufferedImage                 bitmap;
    private final @Nonnull Graphics2D                    graphics;
    private final @Nonnull Log                           atlas_log;
    private boolean                                      dirty;

    @SuppressWarnings("synthetic-access") CharAtlas(
      final @Nonnull Log log)
      throws GLException,
        ConstraintError
    {
      this.atlas_log = log;
      this.texture =
        FixedTextRenderer.this.gl.allocateTextureRGBAStatic(
          "char_atlas" + FixedTextRenderer.this.atlases.size(),
          FixedTextRenderer.this.texture_size,
          FixedTextRenderer.this.texture_size,
          TextureWrap.TEXTURE_WRAP_REPEAT,
          TextureWrap.TEXTURE_WRAP_REPEAT,
          TextureFilter.TEXTURE_FILTER_NEAREST,
          TextureFilter.TEXTURE_FILTER_NEAREST);

      this.packer =
        new Pack1D(
          FixedTextRenderer.this.texture_size,
          FixedTextRenderer.this.texture_size,
          FixedTextRenderer.this.character_height);

      this.rectangles = new HashMap<Character, Rectangle>();
      this.dirty = false;

      this.bitmap =
        new BufferedImage(
          FixedTextRenderer.this.texture_size,
          FixedTextRenderer.this.texture_size,
          BufferedImage.TYPE_4BYTE_ABGR);

      this.graphics = this.bitmap.createGraphics();
      this.graphics.setColor(Color.WHITE);
      this.graphics.setFont(FixedTextRenderer.this.font);

      if (this.atlas_log.enabled(Level.LOG_DEBUG)) {
        final StringBuilder builder = new StringBuilder();
        builder.append("cache ");
        builder.append(this);
        builder.append(" create");
        this.atlas_log.debug(builder.toString());
      }
    }

    @SuppressWarnings("synthetic-access") Rectangle cacheCharacter(
      final char character)
      throws ConstraintError,
        TextCacheException
    {
      final Character boxed = Character.valueOf(character);

      /*
       * Word is already cached? Return the rectangle.
       */

      if (this.rectangles.containsKey(boxed)) {
        if (this.atlas_log.enabled(Level.LOG_DEBUG)) {
          final StringBuilder builder = new StringBuilder();
          builder.append("cache ");
          builder.append(this);
          builder.append(" exists '");
          builder.append(character);
          builder.append("'");
          this.atlas_log.debug(builder.toString());
        }
        return this.rectangles.get(boxed);
      }

      final PackResult result =
        this.packer.insert(FixedTextRenderer.this.character_width
          + FixedTextRenderer.PAD_PACK_BORDER);

      switch (result.type) {
        case PACK_RESULT_OK:
        {
          if (this.atlas_log.enabled(Level.LOG_DEBUG)) {
            final StringBuilder builder = new StringBuilder();
            builder.append("cache ");
            builder.append(this);
            builder.append(" success '");
            builder.append(character);
            builder.append("'");
            this.atlas_log.debug(builder.toString());
          }

          final PackOK ok = (PackOK) result;
          assert (ok.rectangle.getWidth() == FixedTextRenderer.this.character_width);
          this.writeChar(character, ok.rectangle);
          this.rectangles.put(boxed, ok.rectangle);
          return ok.rectangle;
        }
        case PACK_RESULT_TOO_LARGE:
        {
          if (this.atlas_log.enabled(Level.LOG_DEBUG)) {
            final StringBuilder builder = new StringBuilder();
            builder.append("cache ");
            builder.append(this);
            builder.append(" word too large '");
            builder.append(character);
            builder.append("'");
            this.atlas_log.debug(builder.toString());
          }
          throw new TextCacheException(result.type, character);
        }
        case PACK_RESULT_OUT_OF_SPACE:
        {
          if (this.atlas_log.enabled(Level.LOG_DEBUG)) {
            final StringBuilder builder = new StringBuilder();
            builder.append("cache ");
            builder.append(this);
            builder.append(" out of space '");
            builder.append(character);
            builder.append("'");
            this.atlas_log.debug(builder.toString());
          }
          throw new TextCacheException(result.type, character);
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
        FixedTextRenderer.this.gl.mapPixelUnpackBufferWrite(this.texture
          .getBuffer());

      try {
        final ByteBuffer target_buffer = map.getByteBuffer();
        final DataBufferByte source_buffer =
          (DataBufferByte) this.bitmap.getData().getDataBuffer();
        target_buffer.put(source_buffer.getData());
        target_buffer.rewind();
      } finally {
        FixedTextRenderer.this.gl.unmapPixelUnpackBuffer(this.texture
          .getBuffer());
      }

      FixedTextRenderer.this.gl.replaceTexture2DRGBAStatic(this.texture);
      this.dirty = false;
    }

    @SuppressWarnings("synthetic-access") private void writeChar(
      final char character,
      final @Nonnull Rectangle rectangle)
    {
      final int ascent = FixedTextRenderer.this.font_metrics.getAscent();
      final int x = rectangle.x0;
      final int y = rectangle.y0 + ascent;

      this.graphics.setColor(Color.WHITE);
      this.graphics.drawString("" + character, x, y);

      this.bitmap.flush();
      this.dirty = true;
    }
  }

  /**
   * Add PAD_PACK_BORDER to the width of the string returned by the java font
   * metrics, prior to packing. This is either required due to inaccurate font
   * metrics, or platform specific bugs.
   */

  private static final int                    PAD_PACK_BORDER = 1;

  private final @Nonnull GLInterface          gl;
  private final @Nonnull Font                 font;
  private final @Nonnull Log                  log;
  private final @Nonnull BufferedImage        base_image;
  private final @Nonnull Graphics2D           base_graphics;
  private final @Nonnull FontMetrics          font_metrics;
  private final @Nonnull ArrayList<CharAtlas> atlases;
  private final int                           texture_size;
  private final int                           character_width;
  private final int                           character_height;

  public FixedTextRenderer(
    final @Nonnull GLInterface gl,
    final @Nonnull Font font,
    final @Nonnull Log log)
    throws ConstraintError,
      GLException
  {
    this.gl = Constraints.constrainNotNull(gl, "GL interface");
    this.font = Constraints.constrainNotNull(font, "Font");
    this.log =
      new Log(Constraints.constrainNotNull(log, "Log interface"), "fix-text");

    this.atlases = new ArrayList<CharAtlas>();
    this.texture_size = this.decideTextureSize();

    /*
     * Initialize the bare minimum Java2D graphics context needed to measure
     * font metrics.
     */

    this.base_image = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
    this.base_graphics = this.base_image.createGraphics();
    this.font_metrics = this.base_graphics.getFontMetrics(this.font);

    this.character_height = this.font_metrics.getHeight();
    this.character_width = this.font_metrics.charWidth(' ');
  }

  public void cacheASCII()
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    for (char c = 0; c < 0xff; ++c) {
      this.cacheCharacter(c);
    }
  }

  private @Nonnull Pair<CharAtlas, Rectangle> cacheCharacter(
    final char c)
    throws ConstraintError,
      GLException,
      TextCacheException
  {
    /*
     * Attempt to cache the given word in one of the current atlases.
     */

    for (final CharAtlas atlas : this.atlases) {
      try {
        final Rectangle rectangle = atlas.cacheCharacter(c);
        return new Pair<CharAtlas, Rectangle>(atlas, rectangle);
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

    final CharAtlas atlas = new CharAtlas(this.log);
    this.atlases.add(atlas);
    return new Pair<CharAtlas, Rectangle>(atlas, atlas.cacheCharacter(c));
  }

  private void cacheLineInner(
    final @Nonnull String line)
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    final int max = line.length();
    for (int index = 0; index < max; ++index) {
      this.cacheCharacter(line.charAt(index));
    }
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
    for (final CharAtlas atlas : this.atlases) {
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
   * Decide the texture size to use. Attempt to use 256k textures (2 ^ 8) but
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

  @Override public void textCacheLine(
    final @Nonnull String line)
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    this.cacheLineInner(Constraints.constrainNotNull(line, "Line"));
  }

  @Override public void textCacheUpload()
    throws GLException,
      ConstraintError
  {
    for (final CharAtlas atlas : this.atlases) {
      if (atlas.isDirty()) {
        atlas.upload();
      }
    }
  }

  @Override public int textGetLineHeight()
  {
    return this.font_metrics.getHeight();
  }

  @Override public int textGetWidth(
    final @Nonnull String line)
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    return line.length() * this.character_width;
  }
}
