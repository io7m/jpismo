package com.io7m.jpismo;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
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

/**
 * <p>
 * The {@link TextRendererTrivial} class implements a renderer for text using
 * arbitrary fonts.
 * </p>
 * <p>
 * Internally, the renderer simply renders incoming text into a large texture
 * and returns a single textured quad. There is no use of atlases and no
 * texture re-use. This renderer is likely to be useful on systems where
 * texture memory is plentiful and texture binds are cheap.
 * </p>
 * 
 */

public final class TextRendererTrivial implements TextRenderer
{
  private final @Nonnull GLInterface           gl;
  private final @Nonnull Font                  font;
  private final @Nonnull Log                   log;
  private final @Nonnull BufferedImage         base_image;
  private final @Nonnull Graphics2D            base_graphics;
  private final @Nonnull FontMetrics           font_metrics;
  private final @Nonnull ArrayBufferDescriptor descriptor;

  private final @Nonnull AtomicInteger         id_pool         =
                                                                 new AtomicInteger(
                                                                   0);

  /**
   * Add PAD_PACK_BORDER to the width of the string returned by the java font
   * metrics, prior to packing. This is either required due to inaccurate font
   * metrics, or platform specific bugs.
   */

  private static final int                     PAD_PACK_BORDER = 1;

  public TextRendererTrivial(
    final @Nonnull GLInterface gl,
    final @Nonnull Font font,
    final @Nonnull Log log)
    throws ConstraintError
  {
    this.gl = Constraints.constrainNotNull(gl, "GL interface");
    this.font = Constraints.constrainNotNull(font, "Font");
    this.log =
      new Log(Constraints.constrainNotNull(log, "Log interface"), "vtext");

    /*
     * Initialize the bare minimum Java2D graphics context needed to measure
     * font metrics.
     */

    this.base_image = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
    this.base_graphics = this.base_image.createGraphics();
    this.font_metrics = this.base_graphics.getFontMetrics(this.font);

    /*
     * Initialize array buffer type descriptor for compiled texts.
     */

    this.descriptor =
      new ArrayBufferDescriptor(new ArrayBufferAttribute[] {
        new ArrayBufferAttribute("position", GLScalarType.TYPE_FLOAT, 2),
        new ArrayBufferAttribute("uv", GLScalarType.TYPE_FLOAT, 2) });
  }

  @Override public void delete(
    final @Nonnull GLInterface gli)
    throws ConstraintError,
      GLException
  {
    // Unused.
  }

  @Override public void textCacheLine(
    final String line)
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    // Unused.
  }

  @Override public void textCacheUpload()
    throws GLException,
      ConstraintError
  {
    // Unused.
  }

  @Override public ArrayList<CompiledText> textCompile(
    final ArrayList<String> text)
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    final String texture_name = "text" + this.id_pool.incrementAndGet();
    int width = 0;
    int height = 0;

    /*
     * Determine how much space is required for the texture.
     */

    for (final String line : text) {
      width = Math.max(this.font_metrics.stringWidth(line), width);
      height += this.font_metrics.getHeight();
    }

    Constraints.constrainRange(
      width,
      1,
      this.gl.getMaximumTextureSize(),
      "Texture width is in range");
    Constraints.constrainRange(
      height,
      1,
      this.gl.getMaximumTextureSize(),
      "Texture height is in range");

    /*
     * Render the given text into the bitmap.
     */

    final BufferedImage bitmap =
      new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    final Graphics2D graphics = bitmap.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.setFont(this.font);

    int y = 0;
    for (final String line : text) {
      graphics.drawString(line, 0, y + this.font_metrics.getAscent());
      y += this.font_metrics.getHeight();
    }
    bitmap.flush();

    /*
     * Upload bitmap data to the GPU texture.
     */

    final Texture2DRGBAStatic texture =
      this.gl.allocateTextureRGBAStatic(
        texture_name,
        width,
        height,
        TextureWrap.TEXTURE_WRAP_REPEAT,
        TextureWrap.TEXTURE_WRAP_REPEAT,
        TextureFilter.TEXTURE_FILTER_NEAREST,
        TextureFilter.TEXTURE_FILTER_NEAREST);

    final PixelUnpackBufferWritableMap map =
      this.gl.mapPixelUnpackBufferWrite(texture.getBuffer());
    try {
      final ByteBuffer target_buffer = map.getByteBuffer();
      final DataBufferByte source_buffer =
        (DataBufferByte) bitmap.getData().getDataBuffer();
      target_buffer.put(source_buffer.getData());
      target_buffer.rewind();
    } finally {
      this.gl.unmapPixelUnpackBuffer(texture.getBuffer());
    }
    this.gl.replaceTexture2DRGBAStatic(texture);

    /*
     * Populate array and index buffers.
     */

    final ArrayBuffer array_buffer =
      this.gl.allocateArrayBuffer(4, this.descriptor);
    final IndexBuffer index_buffer =
      this.gl.allocateIndexBuffer(array_buffer, 6);

    try {
      final ArrayBufferWritableMap array_map =
        this.gl.mapArrayBufferWrite(array_buffer);
      final IndexBufferWritableMap index_map =
        this.gl.mapIndexBufferWrite(index_buffer);

      final ArrayBufferCursorWritable2f cursor_po =
        array_map.getCursor2f("position");
      final ArrayBufferCursorWritable2f cursor_uv =
        array_map.getCursor2f("uv");

      cursor_po.put2f(0, -height);
      cursor_po.put2f(0, 0);
      cursor_po.put2f(width, 0);
      cursor_po.put2f(width, -height);

      cursor_uv.put2f(0, 1);
      cursor_uv.put2f(0, 0);
      cursor_uv.put2f(1, 0);
      cursor_uv.put2f(1, 1);

      if (this.log.enabledByLevel(Level.LOG_DEBUG)) {
        final StringBuilder t0 = new StringBuilder();
        t0.append("tri [");
        t0.append(0);
        t0.append(" ");
        t0.append(-height);
        t0.append("] [");
        t0.append(0);
        t0.append(" ");
        t0.append(0);
        t0.append("] [");
        t0.append(width);
        t0.append(" ");
        t0.append(0);
        t0.append("]");
        this.log.debug(t0.toString());

        final StringBuilder t1 = new StringBuilder();
        t1.append("tri [");
        t1.append(0);
        t1.append(" ");
        t1.append(-height);
        t1.append("] [");
        t1.append(width);
        t1.append(" ");
        t1.append(0);
        t1.append("] [");
        t1.append(width);
        t1.append(" ");
        t1.append(-height);
        t1.append("]");
        this.log.debug(t1.toString());
      }

      index_map.put(0, 0);
      index_map.put(1, 1);
      index_map.put(2, 2);

      index_map.put(3, 0);
      index_map.put(4, 2);
      index_map.put(5, 3);
    } finally {
      this.gl.unmapArrayBuffer(array_buffer);
      this.gl.unmapIndexBuffer(index_buffer);
    }

    final ArrayList<CompiledText> list = new ArrayList<CompiledText>();
    list.add(new CompiledText(array_buffer, index_buffer, texture, true));
    return list;
  }

  @Override public int textGetLineHeight()
  {
    return this.font_metrics.getHeight();
  }

  @Override public int textGetWidth(
    final String line)
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    return this.font_metrics.stringWidth(line)
      + TextRendererTrivial.PAD_PACK_BORDER;
  }
}
