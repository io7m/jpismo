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
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.ArrayBufferAttribute;
import com.io7m.jcanephora.ArrayBufferCursorWritable2f;
import com.io7m.jcanephora.ArrayBufferDescriptor;
import com.io7m.jcanephora.ArrayBufferWritableMap;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.GLScalarType;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jcanephora.IndexBufferWritableMap;
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
 * into texture atlases. Note that spaces are not preserved: multiple
 * successive spaces are collapsed into single spaces in the rendered text.
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
    private final int                                 id;
    private boolean                                   dirty;

    @SuppressWarnings("synthetic-access") WordAtlas(
      final @Nonnull Log log)
      throws GLException,
        ConstraintError
    {
      this.atlas_log = log;

      this.id = VariableTextRenderer.this.atlases.size();

      this.texture =
        VariableTextRenderer.this.gl.allocateTextureRGBAStatic(
          "word_atlas" + this.id,
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
          VariableTextRenderer.this.textGetLineHeight());

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

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final WordAtlas other = (WordAtlas) obj;
      if (!this.getOuterType().equals(other.getOuterType())) {
        return false;
      }
      if (this.id != other.id) {
        return false;
      }
      return true;
    }

    private VariableTextRenderer getOuterType()
    {
      return VariableTextRenderer.this;
    }

    Texture2DRGBAStatic getTexture()
    {
      return this.texture;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.getOuterType().hashCode();
      result = (prime * result) + this.id;
      return result;
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

  private static void addQuad(
    final @Nonnull WordAtlas atlas,
    final @Nonnull HashMap<WordAtlas, Integer> counts)
  {
    final Integer count = counts.get(atlas);
    if (count != null) {
      counts.put(atlas, Integer.valueOf(count.intValue() + 1));
    } else {
      counts.put(atlas, Integer.valueOf(1));
    }
  }

  private static String[] splitWords(
    final @Nonnull String line)
  {
    return line.split("\\s+");
  }

  private final @Nonnull GLInterface           gl;
  private final @Nonnull Font                  font;
  private final @Nonnull Log                   log;
  private final @Nonnull BufferedImage         base_image;
  private final @Nonnull Graphics2D            base_graphics;
  private final @Nonnull FontMetrics           font_metrics;
  private final @Nonnull ArrayList<WordAtlas>  atlases;
  private final int                            font_space_width;
  private final int                            texture_size;

  private final @Nonnull ArrayBufferDescriptor descriptor;

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

    /*
     * Initialize array buffer type descriptor for compiled texts.
     */

    this.descriptor =
      new ArrayBufferDescriptor(new ArrayBufferAttribute[] {
        new ArrayBufferAttribute("position", GLScalarType.TYPE_FLOAT, 2),
        new ArrayBufferAttribute("uv", GLScalarType.TYPE_FLOAT, 2) });
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

  private @Nonnull Pair<WordAtlas, Rectangle> cacheWord(
    final @Nonnull String word)
    throws ConstraintError,
      GLException,
      TextCacheException
  {
    /*
     * Attempt to cache the given word in one of the current atlases.
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
   * by <code>directory</code>. This is only of use for debugging the
   * implementation of the text renderer.
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
    for (final WordAtlas atlas : this.atlases) {
      if (atlas.isDirty()) {
        atlas.upload();
      }
    }
  }

  @Override public @Nonnull ArrayList<CompiledText> textCompile(
    final @Nonnull ArrayList<String> text)
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    final ArrayList<CompiledText> ctexts = new ArrayList<CompiledText>();
    final HashMap<WordAtlas, Integer> quad_counts =
      new HashMap<WordAtlas, Integer>();
    final HashMap<String, String[]> word_cache =
      new HashMap<String, String[]>();
    final HashMap<WordAtlas, CompiledText> texts =
      new HashMap<WordAtlas, CompiledText>();

    /*
     * For each line, determine the number of quads required for words using
     * each atlas.
     */

    for (final String line : text) {
      final String[] words = VariableTextRenderer.splitWords(line);
      for (final String word : words) {
        final Pair<WordAtlas, Rectangle> pair = this.cacheWord(word);
        VariableTextRenderer.addQuad(pair.first, quad_counts);
      }
      word_cache.put(line, words);
    }

    /*
     * Allocate vertex and index buffers.
     */

    for (final Entry<WordAtlas, Integer> entry : quad_counts.entrySet()) {
      final WordAtlas atlas = entry.getKey();
      final Integer quad_count = entry.getValue();
      final int vertx_count = quad_count.intValue() * 4;
      final int index_count = quad_count.intValue() * 6;
      final ArrayBuffer array_buffer =
        this.gl.allocateArrayBuffer(vertx_count, this.descriptor);
      final IndexBuffer index_buffer =
        this.gl.allocateIndexBuffer(array_buffer, index_count);
      texts.put(
        atlas,
        new CompiledText(array_buffer, index_buffer, atlas.getTexture()));
    }

    final float size_divisor = 1.0f / this.texture_size;

    /*
     * For each atlas α, populate a vertex buffer with all the quads that use
     * α.
     */

    for (final Entry<WordAtlas, CompiledText> entry : texts.entrySet()) {
      final WordAtlas wanted_atlas = entry.getKey();
      final CompiledText comp = entry.getValue();
      int index = 0;
      int quad = 0;
      int quad_base = 0;
      float y_offset = -this.font_metrics.getHeight();

      try {
        final ArrayBufferWritableMap map_array =
          this.gl.mapArrayBufferWrite(comp.getVertexBuffer());
        final IndexBufferWritableMap map_index =
          this.gl.mapIndexBufferWrite(comp.getIndexBuffer());
        final ArrayBufferCursorWritable2f cursor_pos =
          map_array.getCursor2f("position");
        final ArrayBufferCursorWritable2f cursor_uv =
          map_array.getCursor2f("uv");

        for (final String line : text) {
          float x_offset = 0.0f;
          final String[] line_words = word_cache.get(line);

          for (final String word : line_words) {
            final Pair<WordAtlas, Rectangle> pair = this.cacheWord(word);
            final WordAtlas word_atlas = pair.first;
            final Rectangle rect = pair.second;

            if (word_atlas == wanted_atlas) {
              final float u0 = rect.x0 * size_divisor;
              final float v0 = rect.y0 * size_divisor;
              final float u1 = (rect.x1) * size_divisor;
              final float v1 = (rect.y1 + 1) * size_divisor;

              final float x0 = x_offset;
              final float x1 = (x0 + rect.getWidth()) - 1;
              final float y0 = y_offset;
              final float y1 = y_offset + rect.getHeight();

              if (this.log.enabledByLevel(Level.LOG_DEBUG)) {
                final StringBuilder t0 = new StringBuilder();
                t0.append("tri ");
                t0.append(quad);
                t0.append(" [");
                t0.append(x0);
                t0.append(" ");
                t0.append(y1);
                t0.append("] [");
                t0.append(x0);
                t0.append(" ");
                t0.append(y0);
                t0.append("] [");
                t0.append(x1);
                t0.append(" ");
                t0.append(y0);
                t0.append("]");
                this.log.debug(t0.toString());

                final StringBuilder t1 = new StringBuilder();
                t1.append("tri ");
                t1.append(quad);
                t1.append(" [");
                t1.append(x0);
                t1.append(" ");
                t1.append(y1);
                t1.append("] [");
                t1.append(x1);
                t1.append(" ");
                t1.append(y0);
                t1.append("] [");
                t1.append(x1);
                t1.append(" ");
                t1.append(y1);
                t1.append("]");
                this.log.debug(t1.toString());
              }

              cursor_pos.put2f(x0, y0);
              cursor_pos.put2f(x0, y1);
              cursor_pos.put2f(x1, y0);
              cursor_pos.put2f(x1, y1);

              cursor_uv.put2f(u0, v1);
              cursor_uv.put2f(u0, v0);
              cursor_uv.put2f(u1, v1);
              cursor_uv.put2f(u1, v0);

              map_index.put(index + 0, quad_base + 1);
              map_index.put(index + 1, quad_base + 0);
              map_index.put(index + 2, quad_base + 2);

              map_index.put(index + 3, quad_base + 1);
              map_index.put(index + 4, quad_base + 2);
              map_index.put(index + 5, quad_base + 3);

              index += 6;
              quad += 1;
              quad_base = quad * 4;
            }

            x_offset += rect.getWidth() + this.font_space_width;
          }
          y_offset -= this.font_metrics.getHeight();
        }
      } finally {
        this.gl.unmapArrayBuffer(comp.getVertexBuffer());
        this.gl.unmapIndexBuffer(comp.getIndexBuffer());
      }

      ctexts.add(comp);
    }

    return ctexts;
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
