/*
 * Copyright © 2016 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jpismo.awt;

import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayBufferUsableType;
import com.io7m.jcanephora.core.JCGLArrayObjectBuilderType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLBufferUpdateType;
import com.io7m.jcanephora.core.JCGLBufferUpdates;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLIndexBufferUsableType;
import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTexture2DUpdateType;
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureUpdates;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.JCGLUnsignedType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLIndexBuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTextures2DType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.cursors.JCGLR8ByteBuffered;
import com.io7m.jcanephora.cursors.JCGLR8Type;
import com.io7m.jcanephora.cursors.JCGLRG8ByteBuffered;
import com.io7m.jcanephora.cursors.JCGLRG8Type;
import com.io7m.jcanephora.cursors.JCGLRGBA8ByteBuffered;
import com.io7m.jcanephora.cursors.JCGLRGBA8Type;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jpismo.PTextCompiledType;
import com.io7m.jpismo.PTextMeasuredType;
import com.io7m.jpismo.PTextRendererType;
import com.io7m.jpismo.PTextUnmeasuredType;
import com.io7m.jpismo.PTextWrapping;
import com.io7m.jpismo.PTextureType;
import com.io7m.jpismo.PTypefaceDefaultsType;
import com.io7m.jpismo.PTypefaceLoaderType;
import com.io7m.jpismo.PTypefaceType;
import com.io7m.jpismo.awt.cursors.PVertexPU32ByteBuffered;
import com.io7m.jpismo.awt.cursors.PVertexPU32Type;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import com.io7m.jpra.runtime.java.JPRACursor2DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor2DType;
import com.io7m.jtensors.Vector2FType;
import com.io7m.jtensors.Vector3FType;
import com.io7m.jtensors.VectorM2F;
import com.io7m.jtensors.VectorReadable2FType;
import com.io7m.jtensors.VectorWritable2FType;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import net.jcip.annotations.NotThreadSafe;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;

/**
 * A trivial implementation of the {@link PTextRendererType} interface that
 * delegates to AWT for everything.
 */

@NotThreadSafe
public final class PTextRendererAWTTrivial implements
  PTextRendererType,
  PTypefaceDefaultsType
{
  private static final PTypefaceType TYPEFACE_DEFAULT;
  private static final PTypefaceType TYPEFACE_MONOSPACED;
  private static final PTypefaceType TYPEFACE_SANS;
  private static final PTypefaceType TYPEFACE_SERIF;

  private static final int ATTRIB_POSITION_INDEX;
  private static final long ATTRIB_POSITION_SIZE;
  private static final int ATTRIB_UV_INDEX;
  private static final long ATTRIB_UV_SIZE;
  private static final long VERTEX_SIZE;
  private static final long ARRAY_SIZE;

  static {
    TYPEFACE_DEFAULT = PTextRendererAWTTrivial.newTypefaceDefault();
    TYPEFACE_MONOSPACED =
      PTextRendererAWTTrivial.newTypefaceMonospaced();
    TYPEFACE_SANS = PTextRendererAWTTrivial.newTypefaceSansSerif();
    TYPEFACE_SERIF = PTextRendererAWTTrivial.newTypefaceSerif();

    ATTRIB_POSITION_INDEX = 0;
    ATTRIB_POSITION_SIZE = 3L * 4L;

    ATTRIB_UV_INDEX = 1;
    ATTRIB_UV_SIZE = 2L * 4L;

    long vs = 0L;
    vs += PTextRendererAWTTrivial.ATTRIB_POSITION_SIZE;
    vs += PTextRendererAWTTrivial.ATTRIB_UV_SIZE;

    VERTEX_SIZE = vs;

    ARRAY_SIZE =
      4L * PTextRendererAWTTrivial.VERTEX_SIZE;
  }

  private final BufferedImage scratch;
  private final Graphics2D scratch_g;
  private final VectorM2F size;
  private final PTypefaceLoaderType loader;

  private PTextRendererAWTTrivial(final PTypefaceLoaderType in_loader)
  {
    this.loader = NullCheck.notNull(in_loader);
    this.scratch = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);
    this.scratch_g = NullCheck.notNull(this.scratch.createGraphics());
    this.size = new VectorM2F();
  }

  private static PTypefaceType newTypefaceDefault()
  {
    return new PTypefaceAWT(NullCheck.notNull(Font.decode(null)));
  }

  private static PTypefaceType newTypefaceMonospaced()
  {
    return new PTypefaceAWT(NullCheck.notNull(Font.decode("Monospaced")));
  }

  private static PTypefaceType newTypefaceSansSerif()
  {
    return new PTypefaceAWT(NullCheck.notNull(Font.decode("SansSerif")));
  }

  private static PTypefaceType newTypefaceSerif()
  {
    return new PTypefaceAWT(NullCheck.notNull(Font.decode("Serif")));
  }

  private static Font makeFont(
    final PTextUnmeasuredType text)
  {
    final PTypefaceType face_raw = text.getTypeface();
    final PTypefaceAWT face = PTypefaceAWT.checkFace(face_raw);
    final Font font_base = face.getFont();
    return NullCheck.notNull(font_base.deriveFont(text.getFontSize()));
  }

  private static void setAntialiasingMode(
    final PTextUnmeasuredType text,
    final Graphics2D g)
  {
    switch (text.getAntialisingMode()) {
      case TEXT_ANTIALIASING_FAST: {
        g.setRenderingHint(
          RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        break;
      }
      case TEXT_ANTIALIASING_NONE: {
        g.setRenderingHint(
          RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        break;
      }
    }
  }

  /**
   * Construct an attributed string that uses the given font and renders
   * strictly white text.
   */

  private static AttributedString makeAttributedText(
    final Font font,
    final String text_data)
  {
    final AttributedString as = new AttributedString(text_data);
    as.addAttribute(TextAttribute.FONT, font);
    as.addAttribute(
      TextAttribute.FOREGROUND,
      Color.WHITE,
      0,
      text_data.length() - 1);
    return as;
  }

  /**
   * Calculate the maximum possible width based on the parameters.
   */

  private static float calculateWidthLimit(
    final Font font,
    final PTextWrapping mode,
    final float wrap_width,
    final FontRenderContext frc)
  {
    switch (mode) {

      /**
       * Estimate the width based on the number of columns.
       */

      case TEXT_WRAPPING_COLUMNS: {
        final double reduced = (double) wrap_width * 0.35;
        return (float) (font.getMaxCharBounds(frc).getWidth() * reduced);
      }

      /**
       * Without text wrapping, the maximum width is effectively unbounded.
       */

      case TEXT_WRAPPING_NONE: {
        return Float.MAX_VALUE;
      }

      /**
       * With text wrapping set to a specific pixel value, the maximum width
       * is this value.
       */

      case TEXT_WRAPPING_PIXELS: {
        return wrap_width;
      }
    }

    throw new UnreachableCodeException();
  }

  /**
   * Render all of the given text. This function may be used to measure text by
   * disabling rendering.
   *
   * @param text_data   The text data
   * @param as_iterator A character iterator
   * @param measurer    A line measurer
   * @param width_limit The current width limit
   * @param out_bounds  The resulting bounds of the text
   * @param dg          A graphics instance
   * @param draw        {@code true} iff the text should actually be rendered
   *                    into an image
   */

  private static void renderAndMeasure(
    final String text_data,
    final CharacterIterator as_iterator,
    final LineBreakMeasurer measurer,
    final float width_limit,
    final VectorWritable2FType out_bounds,
    final Graphics2D dg,
    final boolean draw)
  {
    float max_width = 0.0f;
    float ty = 0.0f;

    while (measurer.getPosition() < as_iterator.getEndIndex()) {
      final int current = measurer.getPosition();
      final int next = measurer.nextOffset(width_limit);
      final int index = text_data.indexOf("\n", current + 1);

      final int limit;
      if ((next > (index - current)) && (index != -1)) {
        limit = index - current;
      } else {
        limit = next;
      }

      final TextLayout layout =
        measurer.nextLayout(width_limit, current + limit, false);

      ty += layout.getAscent();
      if (draw) {
        layout.draw(dg, 0.0f, ty);
      }
      ty += layout.getDescent() + layout.getLeading();

      final Rectangle2D bounds = layout.getBounds();
      max_width = (float) Math.max((double) max_width, bounds.getWidth());
    }

    out_bounds.set2F(max_width, ty);
  }

  private static BufferedImage makeImage(
    final int width,
    final int height,
    final PTextureType actual_type)
  {
    switch (actual_type) {
      case TEXTURE_OPAQUE_R: {
        return new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
      }
      case TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED: {
        return new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
      }
      case TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED: {
        return new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
      }
    }

    throw new UnreachableCodeException();
  }

  private static Font setupFontAndAntialiasing(
    final PTextUnmeasuredType unmeasured,
    final Graphics2D g)
  {
    final Font font = PTextRendererAWTTrivial.makeFont(unmeasured);
    PTextRendererAWTTrivial.setAntialiasingMode(unmeasured, g);
    return font;
  }

  private static void makeTexturePopulate(
    final JCGLTextures2DType g_t,
    final JCGLTextureUnitType texture_unit,
    final JCGLTexture2DUsableType texture,
    final BufferedImage image,
    final PTextureType texture_type)
  {
    final JCGLTexture2DUpdateType update =
      JCGLTextureUpdates.newUpdateReplacingAll2D(texture);

    switch (texture_type) {
      case TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED: {
        assert image.getType() == BufferedImage.TYPE_4BYTE_ABGR;
        PTextRendererAWTTrivial.copyRGBANPToRGBANP(image, update);
        break;
      }
      case TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED: {
        assert image.getType() == BufferedImage.TYPE_4BYTE_ABGR;
        PTextRendererAWTTrivial.copyRGBANPToRGNP(image, update);
        break;
      }
      case TEXTURE_OPAQUE_R: {
        assert image.getType() == BufferedImage.TYPE_BYTE_GRAY;
        PTextRendererAWTTrivial.copyRToR(image, update);
        break;
      }
    }

    g_t.texture2DUpdate(texture_unit, update);
  }

  private static void copyRGBANPToRGBANP(
    final BufferedImage image,
    final JCGLTexture2DUpdateType update)
  {
    final int width = image.getWidth();
    final int height = image.getHeight();
    final float recip = 1.0f / 256.0f;

    final JPRACursor2DType<JCGLRGBA8Type> c =
      JPRACursor2DByteBufferedChecked.newCursor(
        update.getData(),
        width,
        height,
        JCGLRGBA8ByteBuffered::newValueWithOffset);

    final JCGLRGBA8Type view = c.getElementView();
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        c.setElementPosition(x, y);

        final int argb =
          PTextRendererAWTTrivial.getImageARGBAsOpenGL(image, x, y);

        final float fw = (float) ((argb >> 24) & 0xFF) * recip;
        final float fx = (float) ((argb >> 16) & 0xFF) * recip;
        final float fy = (float) ((argb >> 8) & 0xFF) * recip;
        final float fz = (float) (argb & 0xFF) * recip;
        view.set((double) fx, (double) fy, (double) fz, (double) fw);
      }
    }
  }

  private static void copyRGBANPToRGNP(
    final BufferedImage image,
    final JCGLTexture2DUpdateType update)
  {
    final int width = image.getWidth();
    final int height = image.getHeight();
    final float recip = 1.0f / 256.0f;

    final JPRACursor2DType<JCGLRG8Type> c =
      JPRACursor2DByteBufferedChecked.newCursor(
        update.getData(),
        width,
        height,
        JCGLRG8ByteBuffered::newValueWithOffset);

    final JCGLRG8Type view = c.getElementView();
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        c.setElementPosition(x, y);

        final int argb =
          PTextRendererAWTTrivial.getImageARGBAsOpenGL(image, x, y);

        final float fw = (float) ((argb >> 24) & 0xFF) * recip;
        final float fz = (float) (argb & 0xFF) * recip;
        view.set((double) fz, (double) fw);
      }
    }
  }

  private static int getImageARGBAsOpenGL(
    final BufferedImage image,
    final int x,
    final int y)
  {
    final int upper = image.getHeight() - 1;
    return image.getRGB(x, upper - y);
  }

  private static void copyRToR(
    final BufferedImage image,
    final JCGLTexture2DUpdateType update)
  {
    final int width = image.getWidth();
    final int height = image.getHeight();
    final float recip = 1.0f / 256.0f;

    final JPRACursor2DType<JCGLR8Type> c =
      JPRACursor2DByteBufferedChecked.newCursor(
        update.getData(),
        width,
        height,
        JCGLR8ByteBuffered::newValueWithOffset);

    final JCGLR8Type view = c.getElementView();
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        c.setElementPosition(x, y);

        final int argb =
          PTextRendererAWTTrivial.getImageARGBAsOpenGL(image, x, y);

        final float r = (float) (argb & 0xFF) * recip;
        view.set((double) r);
      }
    }
  }

  private static JCGLTextureFormat makeTextureFormat(
    final PTextureType texture_type)
  {
    switch (texture_type) {
      case TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED:
        return JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP;
      case TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED:
        return JCGLTextureFormat.TEXTURE_FORMAT_RG_8_2BPP;
      case TEXTURE_OPAQUE_R:
        return JCGLTextureFormat.TEXTURE_FORMAT_R_8_1BPP;
    }

    throw new UnreachableCodeException();
  }

  private static JCGLArrayObjectType makeArrayObject(
    final JCGLArrayObjectsType g_ao,
    final JCGLArrayBufferUsableType ab,
    final JCGLIndexBufferUsableType ib)
  {
    final JCGLArrayObjectBuilderType aob = g_ao.arrayObjectNewBuilder();

    aob.setIndexBuffer(ib);
    aob.setAttributeFloatingPoint(
      PTextRendererAWTTrivial.ATTRIB_POSITION_INDEX,
      ab,
      3,
      JCGLScalarType.TYPE_FLOAT,
      (int) PTextRendererAWTTrivial.VERTEX_SIZE,
      0L,
      false);
    aob.setAttributeFloatingPoint(
      PTextRendererAWTTrivial.ATTRIB_UV_INDEX,
      ab,
      2,
      JCGLScalarType.TYPE_FLOAT,
      (int) PTextRendererAWTTrivial.VERTEX_SIZE,
      PTextRendererAWTTrivial.ATTRIB_UV_SIZE,
      false);

    final JCGLArrayObjectType ao = g_ao.arrayObjectAllocate(aob);
    g_ao.arrayObjectUnbind();
    return ao;
  }

  private static JCGLIndexBufferType makeIndexBuffer(
    final JCGLIndexBuffersType g_ib)
  {
    final JCGLIndexBufferType index_buffer =
      g_ib.indexBufferAllocate(
        6L,
        JCGLUnsignedType.TYPE_UNSIGNED_INT,
        JCGLUsageHint.USAGE_STATIC_DRAW);
    final JCGLBufferUpdateType<JCGLIndexBufferType> update =
      JCGLBufferUpdates.newUpdateReplacingAll(index_buffer);

    final ShortBuffer d = update.getData().asShortBuffer();
    d.put(0, (short) 0);
    d.put(1, (short) 1);
    d.put(2, (short) 2);
    d.put(3, (short) 0);
    d.put(4, (short) 2);
    d.put(5, (short) 3);

    g_ib.indexBufferUpdate(update);
    g_ib.indexBufferUnbind();
    return index_buffer;
  }

  private static JCGLArrayBufferType makeArrayBuffer(
    final JCGLArrayBuffersType g_ab,
    final VectorReadable2FType bounds)
  {
    final JCGLArrayBufferType ab =
      g_ab.arrayBufferAllocate(
        PTextRendererAWTTrivial.ARRAY_SIZE,
        JCGLUsageHint.USAGE_STATIC_DRAW);

    final JCGLBufferUpdateType<JCGLArrayBufferType> up =
      JCGLBufferUpdates.newUpdateReplacingAll(ab);

    final ByteBuffer data = up.getData();

    final JPRACursor1DType<PVertexPU32Type> cursor =
      JPRACursor1DByteBufferedChecked.newCursor(
        data, PVertexPU32ByteBuffered::newValueWithOffset);

    final PVertexPU32Type view = cursor.getElementView();
    final Vector3FType pos_view = view.getPositionWritable();
    final Vector2FType uv_view = view.getUvWritable();

    cursor.setElementIndex(0);
    pos_view.set3F(0.0f, bounds.getYF(), 0.0f);
    uv_view.set2F(0.0f, 1.0f);

    cursor.setElementIndex(1);
    pos_view.set3F(0.0f, 0.0f, 0.0f);
    uv_view.set2F(0.0f, 0.0f);

    cursor.setElementIndex(2);
    pos_view.set3F(bounds.getXF(), 0.0f, 0.0f);
    uv_view.set2F(1.0f, 0.0f);

    cursor.setElementIndex(3);
    pos_view.set3F(bounds.getXF(), bounds.getYF(), 0.0f);
    uv_view.set2F(1.0f, 1.0f);

    g_ab.arrayBufferUpdate(up);
    g_ab.arrayBufferUnbind();
    return ab;
  }

  private static Pair<JCGLTextureUnitType, JCGLTexture2DType> makeTextureAllocate(
    final JCGLTexturesType g_t,
    final JCGLTextureUnitContextMutableType uc,
    final BufferedImage image,
    final PTextureType texture_type,
    final JCGLTextureFilterMinification filter_min,
    final JCGLTextureFilterMagnification filter_mag)
  {
    final JCGLTextureFormat format =
      PTextRendererAWTTrivial.makeTextureFormat(texture_type);
    return uc.unitContextAllocateTexture2D(
      g_t,
      (long) image.getWidth(),
      (long) image.getHeight(),
      format,
      JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
      JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
      filter_min,
      filter_mag);
  }

  private static PTextCompiledType makeCompiledText(
    final JCGLInterfaceGL33Type g33,
    final JCGLTextureUnitContextParentType uc,
    final BufferedImage image,
    final VectorReadable2FType bounds,
    final PTextureType texture_type,
    final JCGLTextureFilterMinification filter_min,
    final JCGLTextureFilterMagnification filter_mag)
  {
    final JCGLArrayBufferType ab =
      PTextRendererAWTTrivial.makeArrayBuffer(g33.getArrayBuffers(), bounds);
    final JCGLIndexBufferType ib =
      PTextRendererAWTTrivial.makeIndexBuffer(g33.getIndexBuffers());
    final JCGLArrayObjectType ao =
      PTextRendererAWTTrivial.makeArrayObject(g33.getArrayObjects(), ab, ib);

    final JCGLTexturesType g_t =
      g33.getTextures();
    final JCGLTextureUnitContextType uc_alloc =
      uc.unitContextNewWithReserved(1);

    try {
      final Pair<JCGLTextureUnitType, JCGLTexture2DType> pair =
        PTextRendererAWTTrivial.makeTextureAllocate(
          g_t, uc_alloc, image, texture_type, filter_min, filter_mag);
      PTextRendererAWTTrivial.makeTexturePopulate(
        g_t, pair.getLeft(), pair.getRight(), image, texture_type);
      return new PTextCompiledAWT(ao, ab, ib, pair.getRight(), texture_type);
    } finally {
      uc_alloc.unitContextFinish(g_t);
    }
  }

  /**
   * Create a new text renderer.
   *
   * @param loader A typeface loader
   *
   * @return A new text renderer
   */

  public static PTextRendererType create(
    final PTypefaceLoaderType loader)
  {
    return new PTextRendererAWTTrivial(loader);
  }

  @Override
  public PTypefaceDefaultsType getTypefaceDefaults()
  {
    return this;
  }

  @Override
  public PTypefaceLoaderType getTypefaceLoader()
  {
    return this.loader;
  }

  @Override
  public PTextMeasuredType measureText(final PTextUnmeasuredType text)
  {
    NullCheck.notNull(text);

    /**
     * Set up the font and antialiasing mode for the given text.
     */

    final Font font =
      PTextRendererAWTTrivial.setupFontAndAntialiasing(text, this.scratch_g);

    /**
     * Set up the context required to measure the text.
     */

    final FontRenderContext frc =
      NullCheck.notNull(this.scratch_g.getFontRenderContext());
    final String text_data = text.getText();
    final AttributedString as =
      PTextRendererAWTTrivial.makeAttributedText(font, text_data);
    final AttributedCharacterIterator as_iterator =
      NullCheck.notNull(as.getIterator());
    final LineBreakMeasurer measurer =
      new LineBreakMeasurer(as_iterator, frc);

    /**
     * Calculate the maximum width based on the wrapping mode.
     */

    final float width_limit =
      PTextRendererAWTTrivial.calculateWidthLimit(
        font,
        text.getWrappingMode(),
        text.getWrappingWidth(),
        frc);

    /**
     * Render the text without actually drawing it, resulting in a
     * complete set of measurements for the text.
     */

    PTextRendererAWTTrivial.renderAndMeasure(
      text_data,
      as_iterator,
      measurer,
      width_limit,
      this.size,
      this.scratch_g,
      false);

    return new PTextMeasuredAWT(
      text,
      as,
      this.size.getXF(),
      this.size.getYF());
  }

  @Override
  public PTextCompiledType compileMeasuredText(
    final JCGLInterfaceGL33Type g33,
    final JCGLTextureUnitContextParentType uc,
    final PTextMeasuredType text)
  {
    NullCheck.notNull(text, "Text");
    NullCheck.notNull(g33, "GL33");
    NullCheck.notNull(uc, "Unit allocator");

    final PTextUnmeasuredType unmeasured = text.textGetUnmeasured();

    /**
     * Create a new image that will contain the results of rendering.
     */

    final int image_width = (int) Math.ceil((double) text.textGetWidth());
    final int image_height = (int) Math.ceil((double) text.textGetHeight());
    final BufferedImage image =
      PTextRendererAWTTrivial.makeImage(
        image_width,
        image_height,
        unmeasured.getTextureType());
    final Graphics2D g = NullCheck.notNull(image.createGraphics());

    /**
     * Set up the font and antialiasing mode for the given text.
     */

    final Font font =
      PTextRendererAWTTrivial.setupFontAndAntialiasing(unmeasured, g);

    /**
     * Set up the context required to measure the text.
     */

    final FontRenderContext frc =
      NullCheck.notNull(g.getFontRenderContext());
    final String text_data = unmeasured.getText();
    final AttributedString as =
      PTextRendererAWTTrivial.makeAttributedText(font, text_data);
    final AttributedCharacterIterator as_iterator =
      NullCheck.notNull(as.getIterator());
    final LineBreakMeasurer measurer =
      new LineBreakMeasurer(as_iterator, frc);

    final float width_limit =
      PTextRendererAWTTrivial.calculateWidthLimit(
        font,
        unmeasured.getWrappingMode(),
        unmeasured.getWrappingWidth(),
        frc);

    /**
     * Render the actual image.
     */

    PTextRendererAWTTrivial.renderAndMeasure(
      text_data,
      as_iterator,
      measurer,
      width_limit,
      this.size,
      g,
      true);

    return PTextRendererAWTTrivial.makeCompiledText(
      g33,
      uc,
      image,
      this.size,
      unmeasured.getTextureType(),
      unmeasured.getMinificationFilter(),
      unmeasured.getMagnificationFilter());
  }

  @Override
  public PTextCompiledType compileUnmeasuredTextWithHardBounds(
    final JCGLInterfaceGL33Type g33,
    final JCGLTextureUnitContextParentType uc,
    final PTextUnmeasuredType text,
    final int width,
    final int height)
  {
    throw new UnimplementedCodeException();
  }

  @Override
  public PTextCompiledType compileUnmeasuredText(
    final JCGLInterfaceGL33Type g33,
    final JCGLTextureUnitContextParentType uc,
    final PTextUnmeasuredType text)
  {
    throw new UnimplementedCodeException();
  }

  @Override
  public PTypefaceType getDefault()
  {
    return PTextRendererAWTTrivial.TYPEFACE_DEFAULT;
  }

  @Override
  public PTypefaceType getMonospace()
  {
    return PTextRendererAWTTrivial.TYPEFACE_MONOSPACED;
  }

  @Override
  public PTypefaceType getSansSerif()
  {
    return PTextRendererAWTTrivial.TYPEFACE_SANS;
  }

  @Override
  public PTypefaceType getSerif()
  {
    return PTextRendererAWTTrivial.TYPEFACE_SERIF;
  }
}
