/*
 * Copyright © 2014 <code@io7m.com> http://io7m.com
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
import java.math.BigInteger;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import com.io7m.jcanephora.ArrayAttributeDescriptor;
import com.io7m.jcanephora.ArrayBufferType;
import com.io7m.jcanephora.ArrayBufferUpdateUnmapped;
import com.io7m.jcanephora.ArrayBufferUpdateUnmappedType;
import com.io7m.jcanephora.ArrayDescriptor;
import com.io7m.jcanephora.ArrayDescriptorBuilderType;
import com.io7m.jcanephora.CursorWritable2fType;
import com.io7m.jcanephora.CursorWritable3fType;
import com.io7m.jcanephora.CursorWritableIndexType;
import com.io7m.jcanephora.IndexBufferType;
import com.io7m.jcanephora.IndexBufferUpdateUnmapped;
import com.io7m.jcanephora.IndexBufferUpdateUnmappedType;
import com.io7m.jcanephora.JCGLException;
import com.io7m.jcanephora.JCGLScalarType;
import com.io7m.jcanephora.SpatialCursorWritable1fType;
import com.io7m.jcanephora.SpatialCursorWritable2fType;
import com.io7m.jcanephora.SpatialCursorWritable4fType;
import com.io7m.jcanephora.Texture2DStaticType;
import com.io7m.jcanephora.Texture2DStaticUpdate;
import com.io7m.jcanephora.Texture2DStaticUpdateType;
import com.io7m.jcanephora.TextureFilterMagnification;
import com.io7m.jcanephora.TextureFilterMinification;
import com.io7m.jcanephora.TextureUpdateType;
import com.io7m.jcanephora.TextureWrapS;
import com.io7m.jcanephora.TextureWrapT;
import com.io7m.jcanephora.UsageHint;
import com.io7m.jcanephora.api.JCGLImplementationType;
import com.io7m.jcanephora.api.JCGLImplementationVisitorType;
import com.io7m.jcanephora.api.JCGLInterfaceCommonType;
import com.io7m.jcanephora.api.JCGLInterfaceGL2Type;
import com.io7m.jcanephora.api.JCGLInterfaceGL3Type;
import com.io7m.jcanephora.api.JCGLInterfaceGLES2Type;
import com.io7m.jcanephora.api.JCGLInterfaceGLES3Type;
import com.io7m.jcanephora.api.JCGLTextures2DStaticGL3ES3Type;
import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jpismo.PTextCompiledType;
import com.io7m.jpismo.PTextMeasuredType;
import com.io7m.jpismo.PTextRendererType;
import com.io7m.jpismo.PTextUnmeasured;
import com.io7m.jpismo.PTextWrapping;
import com.io7m.jpismo.PTextureType;
import com.io7m.jpismo.PTypefaceDefaultsType;
import com.io7m.jpismo.PTypefaceLoaderType;
import com.io7m.jpismo.PTypefaceType;
import com.io7m.jtensors.VectorM2F;
import com.io7m.jtensors.VectorM4F;
import com.io7m.jtensors.VectorReadable2FType;
import com.io7m.junreachable.UnreachableCodeException;

@SuppressWarnings("synthetic-access") @EqualityReference final class PTextRendererAWTTrivialActual implements
  PTextRendererType,
  PTypefaceDefaultsType
{
  private static final PTypefaceType TYPEFACE_DEFAULT;
  private static final PTypefaceType TYPEFACE_MONOSPACED;
  private static final PTypefaceType TYPEFACE_SANS;
  private static final PTypefaceType TYPEFACE_SERIF;

  static {
    TYPEFACE_DEFAULT = PTextRendererAWTTrivialActual.newTypefaceDefault();
    TYPEFACE_MONOSPACED =
      PTextRendererAWTTrivialActual.newTypefaceMonospaced();
    TYPEFACE_SANS = PTextRendererAWTTrivialActual.newTypefaceSansSerif();
    TYPEFACE_SERIF = PTextRendererAWTTrivialActual.newTypefaceSerif();
  }

  private static float calculateWidthLimit(
    final Font font,
    final PTextWrapping mode,
    final float width,
    final FontRenderContext frc)
  {
    switch (mode) {
      case TEXT_WRAPPING_COLUMNS:
      {
        final float reduced = width * 0.35f;
        return (float) (font.getMaxCharBounds(frc).getWidth() * reduced);
      }
      case TEXT_WRAPPING_NONE:
      {
        return Float.MAX_VALUE;
      }
      case TEXT_WRAPPING_PIXELS:
      {
        return width;
      }
    }

    throw new UnreachableCodeException();
  }

  private static void copyRGBANPToRGBANP(
    final BufferedImage image,
    final TextureUpdateType data)
  {
    final SpatialCursorWritable4fType cursor = data.getCursor4f();
    final int height = image.getHeight();
    final int width = image.getWidth();
    final float recip = 1.0f / 256.0f;
    final VectorM4F pixel = new VectorM4F();

    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        final int argb =
          PTextRendererAWTTrivialActual.getImageARGBAsOpenGL(image, x, y);

        final float fw = ((argb >> 24) & 0xFF) * recip;
        final float fx = ((argb >> 16) & 0xFF) * recip;
        final float fy = ((argb >> 8) & 0xFF) * recip;
        final float fz = (argb & 0xFF) * recip;

        pixel.set4F(fx, fy, fz, fw);
        cursor.put4f(pixel);
      }
    }
  }

  private static void copyRGBANPToRGNP(
    final BufferedImage image,
    final Texture2DStaticUpdateType c)
  {
    final SpatialCursorWritable2fType cursor = c.getCursor2f();
    final int height = image.getHeight();
    final int width = image.getWidth();
    final float recip = 1.0f / 256.0f;
    final VectorM2F pixel = new VectorM2F();

    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        final int argb =
          PTextRendererAWTTrivialActual.getImageARGBAsOpenGL(image, x, y);

        final float fw = ((argb >> 24) & 0xFF) * recip;
        final float fz = (argb & 0xFF) * recip;

        pixel.set2F(fz, fw);
        cursor.put2f(pixel);
      }
    }
  }

  private static void copyRToR(
    final BufferedImage image,
    final Texture2DStaticUpdateType c)
  {
    final SpatialCursorWritable1fType cursor = c.getCursor1f();
    final int height = image.getHeight();
    final int width = image.getWidth();
    final float recip = 1.0f / 256.0f;

    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        final int argb =
          PTextRendererAWTTrivialActual.getImageARGBAsOpenGL(image, x, y);
        final float r = (argb & 0xFF) * recip;
        cursor.put1f(r);
      }
    }
  }

  private static PTextureType getActualSupportedTextureType(
    final JCGLImplementationType gi,
    final PTextureType type)
  {
    return gi
      .implementationAccept(new JCGLImplementationVisitorType<PTextureType, JCGLException>() {
        @Override public PTextureType implementationIsGL2(
          final JCGLInterfaceGL2Type gl)
        {
          switch (type) {
            case TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED:
            {
              return PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED;
            }
            case TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED:
            {
              return PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED;
            }
            case TEXTURE_OPAQUE_R:
            {
              return PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED;
            }
          }
          throw new UnreachableCodeException();
        }

        @Override public PTextureType implementationIsGL3(
          final JCGLInterfaceGL3Type gl)
        {
          return type;
        }

        @Override public PTextureType implementationIsGLES2(
          final JCGLInterfaceGLES2Type gl)
        {
          switch (type) {
            case TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED:
            {
              return PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED;
            }
            case TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED:
            {
              return PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED;
            }
            case TEXTURE_OPAQUE_R:
            {
              return PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED;
            }
          }
          throw new UnreachableCodeException();
        }

        @Override public PTextureType implementationIsGLES3(
          final JCGLInterfaceGLES3Type gl)
        {
          return type;
        }
      });
  }

  private static int getImageARGBAsOpenGL(
    final BufferedImage image,
    final int x,
    final int y)
  {
    final int upper = image.getHeight() - 1;
    return image.getRGB(x, upper - y);
  }

  private static ArrayBufferType makeArrayBuffer(
    final JCGLInterfaceCommonType gc,
    final ArrayDescriptor in_type,
    final VectorReadable2FType in_size)
  {
    final ArrayBufferType a =
      gc.arrayBufferAllocate(4, in_type, UsageHint.USAGE_STATIC_DRAW);
    final ArrayBufferUpdateUnmappedType au =
      ArrayBufferUpdateUnmapped.newUpdateReplacingAll(a);
    final CursorWritable3fType pc = au.getCursor3f("v_position");
    final CursorWritable2fType uc = au.getCursor2f("v_uv");

    final float max_x = in_size.getXF();
    final float max_y = in_size.getYF();
    pc.put3f(0.0f, 0.0f, 0.0f);
    pc.put3f(0.0f, -max_y, 0.0f);
    pc.put3f(max_x, -max_y, 0.0f);
    pc.put3f(max_x, 0.0f, 0.0f);

    uc.put2f(0.0f, 1.0f);
    uc.put2f(0.0f, 0.0f);
    uc.put2f(1.0f, 0.0f);
    uc.put2f(1.0f, 1.0f);

    gc.arrayBufferUpdate(au);
    return a;
  }

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

  private static PTextCompiledType makeCompiledText(
    final BufferedImage image,
    final JCGLImplementationType gi,
    final ArrayDescriptor type,
    final VectorReadable2FType size,
    final PTextureType actual_type,
    final TextureFilterMinification filter_min,
    final TextureFilterMagnification filter_mag,
    final BigInteger id)
  {
    final JCGLInterfaceCommonType gc = gi.getGLCommon();
    final ArrayBufferType a =
      PTextRendererAWTTrivialActual.makeArrayBuffer(gc, type, size);
    final IndexBufferType i =
      PTextRendererAWTTrivialActual.makeIndexBuffer(gc, a);

    final Texture2DStaticType t =
      PTextRendererAWTTrivialActual.makeTexture(
        gi,
        filter_min,
        filter_mag,
        actual_type,
        id,
        image);

    return new PTextCompiledAWT(a, i, t, actual_type);
  }

  private static Font makeFont(
    final PTextUnmeasured text)
  {
    final PTypefaceType face_raw = text.getTypeface();
    final PTypefaceAWT face = PTypefaceAWT.checkFace(face_raw);
    final Font font_base = face.getFont();
    final Font font =
      NullCheck.notNull(font_base.deriveFont(text.getFontSize()));
    return font;
  }

  private static BufferedImage makeImage(
    final int width,
    final int height,
    final PTextureType actual_type)
  {
    switch (actual_type) {
      case TEXTURE_OPAQUE_R:
      {
        return new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
      }
      case TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED:
      {
        return new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
      }
      case TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED:
      {
        return new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
      }
    }

    throw new UnreachableCodeException();
  }

  private static IndexBufferType makeIndexBuffer(
    final JCGLInterfaceCommonType gc,
    final ArrayBufferType a)
  {
    final IndexBufferType i =
      gc.indexBufferAllocate(a, 6, UsageHint.USAGE_STATIC_DRAW);
    final IndexBufferUpdateUnmappedType ia =
      IndexBufferUpdateUnmapped.newReplacing(i);
    final CursorWritableIndexType ic = ia.getCursor();
    ic.putIndex(0);
    ic.putIndex(1);
    ic.putIndex(2);
    ic.putIndex(0);
    ic.putIndex(2);
    ic.putIndex(3);
    gc.indexBufferUpdate(ia);
    return i;
  }

  private static Texture2DStaticType makeTexture(
    final JCGLImplementationType gi,
    final TextureFilterMinification min,
    final TextureFilterMagnification mag,
    final PTextureType actual_type,
    final BigInteger id,
    final BufferedImage image)
  {
    final JCGLInterfaceCommonType gc = gi.getGLCommon();

    final Texture2DStaticType t =
      PTextRendererAWTTrivialActual.makeTextureAllocate(
        gi,
        min,
        mag,
        actual_type,
        image,
        id);

    final Texture2DStaticUpdateType c =
      Texture2DStaticUpdate.newReplacingAll(t);

    switch (actual_type) {
      case TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED:
      {
        assert image.getType() == BufferedImage.TYPE_4BYTE_ABGR;
        PTextRendererAWTTrivialActual.copyRGBANPToRGBANP(image, c);
        break;
      }
      case TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED:
      {
        assert image.getType() == BufferedImage.TYPE_4BYTE_ABGR;
        PTextRendererAWTTrivialActual.copyRGBANPToRGNP(image, c);
        break;
      }
      case TEXTURE_OPAQUE_R:
      {
        assert image.getType() == BufferedImage.TYPE_BYTE_GRAY;
        PTextRendererAWTTrivialActual.copyRToR(image, c);
        break;
      }
    }

    gc.texture2DStaticUpdate(c);

    switch (min) {
      case TEXTURE_FILTER_LINEAR:
      case TEXTURE_FILTER_NEAREST:
      {
        break;
      }
      case TEXTURE_FILTER_NEAREST_MIPMAP_LINEAR:
      case TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST:
      case TEXTURE_FILTER_LINEAR_MIPMAP_LINEAR:
      case TEXTURE_FILTER_LINEAR_MIPMAP_NEAREST:
      {
        gc.texture2DStaticRegenerateMipmaps(t);
        break;
      }
    }

    return t;
  }

  private static Texture2DStaticType makeTextureAllocate(
    final JCGLImplementationType gi,
    final TextureFilterMinification min,
    final TextureFilterMagnification mag,
    final PTextureType type,
    final BufferedImage image,
    final BigInteger id)
  {
    return gi
      .implementationAccept(new JCGLImplementationVisitorType<Texture2DStaticType, JCGLException>() {
        @Override public Texture2DStaticType implementationIsGL2(
          final JCGLInterfaceGL2Type gl)
        {
          switch (type) {
            case TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED:
            {
              return gl.texture2DStaticAllocateRGBA8(
                NullCheck.notNull(String.format("text-%s-rgba8", id)),
                image.getWidth(),
                image.getHeight(),
                TextureWrapS.TEXTURE_WRAP_REPEAT,
                TextureWrapT.TEXTURE_WRAP_REPEAT,
                min,
                mag);
            }
            case TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED:
            case TEXTURE_OPAQUE_R:
            {
              throw new UnreachableCodeException();
            }
          }

          throw new UnreachableCodeException();
        }

        @Override public Texture2DStaticType implementationIsGL3(
          final JCGLInterfaceGL3Type gl)
        {
          return PTextRendererAWTTrivialActual.makeTextureAllocateGL3ES3(
            min,
            mag,
            type,
            image,
            id,
            gl);
        }

        @Override public Texture2DStaticType implementationIsGLES2(
          final JCGLInterfaceGLES2Type gl)
        {
          switch (type) {
            case TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED:
            {
              return gl.texture2DStaticAllocateRGBA4444(
                NullCheck.notNull(String.format("text-%s-rgba4444", id)),
                image.getWidth(),
                image.getHeight(),
                TextureWrapS.TEXTURE_WRAP_REPEAT,
                TextureWrapT.TEXTURE_WRAP_REPEAT,
                min,
                mag);
            }
            case TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED:
            case TEXTURE_OPAQUE_R:
            {
              throw new UnreachableCodeException();
            }
          }

          throw new UnreachableCodeException();
        }

        @Override public Texture2DStaticType implementationIsGLES3(
          final JCGLInterfaceGLES3Type gl)
        {
          return PTextRendererAWTTrivialActual.makeTextureAllocateGL3ES3(
            min,
            mag,
            type,
            image,
            id,
            gl);
        }
      });
  }

  private static Texture2DStaticType makeTextureAllocateGL3ES3(
    final TextureFilterMinification min,
    final TextureFilterMagnification mag,
    final PTextureType type,
    final BufferedImage image,
    final BigInteger id,
    final JCGLTextures2DStaticGL3ES3Type gl)
  {
    switch (type) {
      case TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED:
      {
        return gl.texture2DStaticAllocateRGBA8(
          NullCheck.notNull(String.format("text-%s-rgba8", id)),
          image.getWidth(),
          image.getHeight(),
          TextureWrapS.TEXTURE_WRAP_REPEAT,
          TextureWrapT.TEXTURE_WRAP_REPEAT,
          min,
          mag);
      }
      case TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED:
      {
        return gl.texture2DStaticAllocateRG8(
          NullCheck.notNull(String.format("text-%s-rg8", id)),
          image.getWidth(),
          image.getHeight(),
          TextureWrapS.TEXTURE_WRAP_REPEAT,
          TextureWrapT.TEXTURE_WRAP_REPEAT,
          min,
          mag);
      }
      case TEXTURE_OPAQUE_R:
      {
        return gl.texture2DStaticAllocateR8(
          NullCheck.notNull(String.format("text-%s-r8", id)),
          image.getWidth(),
          image.getHeight(),
          TextureWrapS.TEXTURE_WRAP_REPEAT,
          TextureWrapT.TEXTURE_WRAP_REPEAT,
          min,
          mag);
      }
    }
    throw new UnreachableCodeException();
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

  private static void renderAndMeasure(
    final String text_data,
    final AttributedCharacterIterator as_iterator,
    final LineBreakMeasurer measurer,
    final float width_limit,
    final VectorM2F s,
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
      max_width = (float) Math.max(max_width, bounds.getWidth());
    }

    s.set2F(max_width, ty);
  }

  private static void setAntialiasingMode(
    final PTextUnmeasured text,
    final Graphics2D g)
  {
    switch (text.getAntialisingMode()) {
      case TEXT_ANTIALIASING_FAST:
      {
        g.setRenderingHint(
          RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        break;
      }
      case TEXT_ANTIALIASING_NONE:
      {
        g.setRenderingHint(
          RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        break;
      }
    }
  }

  private final JCGLImplementationType gi;
  private BigInteger                   id;
  private final BufferedImage          scratch;
  private final Graphics2D             scratch_g;
  private final VectorM2F              size;
  private final ArrayDescriptor        type;
  private final PTypefaceLoaderType    loader;

  PTextRendererAWTTrivialActual(
    final JCGLImplementationType in_gi,
    final PTypefaceLoaderType in_loader)
  {
    this.gi = NullCheck.notNull(in_gi, "OpenGL implementation");
    this.loader = NullCheck.notNull(in_loader, "Loader");

    this.scratch = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);
    this.scratch_g = NullCheck.notNull(this.scratch.createGraphics());
    this.size = new VectorM2F();
    this.id = NullCheck.notNull(BigInteger.ZERO);

    final ArrayDescriptorBuilderType adb = ArrayDescriptor.newBuilder();
    adb.addAttribute(ArrayAttributeDescriptor.newAttribute(
      "v_position",
      JCGLScalarType.TYPE_FLOAT,
      3));
    adb.addAttribute(ArrayAttributeDescriptor.newAttribute(
      "v_uv",
      JCGLScalarType.TYPE_FLOAT,
      2));
    this.type = adb.build();
  }

  @Override public PTextCompiledType compileMeasuredText(
    final PTextMeasuredType text)
  {
    NullCheck.notNull(text, "Text");

    this.id = this.nextID();

    final PTextUnmeasured unmeasured = text.textGetUnmeasured();
    final PTextureType actual_type =
      PTextRendererAWTTrivialActual.getActualSupportedTextureType(
        this.gi,
        unmeasured.getTextureType());

    final int image_width = (int) Math.ceil(text.textGetWidth());
    final int image_height = (int) Math.ceil(text.textGetHeight());
    final BufferedImage image =
      PTextRendererAWTTrivialActual.makeImage(
        image_width,
        image_height,
        actual_type);
    final Graphics2D g = NullCheck.notNull(image.createGraphics());

    final Font font = PTextRendererAWTTrivialActual.makeFont(unmeasured);
    PTextRendererAWTTrivialActual.setAntialiasingMode(unmeasured, g);

    final FontRenderContext frc = NullCheck.notNull(g.getFontRenderContext());
    final String text_data = unmeasured.getText();
    final AttributedString as =
      PTextRendererAWTTrivialActual.makeAttributedText(font, text_data);
    final AttributedCharacterIterator as_iterator =
      NullCheck.notNull(as.getIterator());
    final LineBreakMeasurer measurer =
      new LineBreakMeasurer(as_iterator, frc);

    final float width_limit =
      PTextRendererAWTTrivialActual.calculateWidthLimit(
        font,
        unmeasured.getWrappingMode(),
        unmeasured.getWrappingWidth(),
        frc);

    PTextRendererAWTTrivialActual.renderAndMeasure(
      text_data,
      as_iterator,
      measurer,
      width_limit,
      this.size,
      g,
      true);

    return PTextRendererAWTTrivialActual.makeCompiledText(
      image,
      this.gi,
      this.type,
      this.size,
      actual_type,
      unmeasured.getMinificationFilter(),
      unmeasured.getMagnificationFilter(),
      this.id);
  }

  @Override public PTextCompiledType compileUnmeasuredText(
    final PTextUnmeasured text)
  {
    NullCheck.notNull(text, "Text");

    this.id = this.nextID();

    final PTextureType actual_type =
      PTextRendererAWTTrivialActual.getActualSupportedTextureType(
        this.gi,
        text.getTextureType());

    final Font font = PTextRendererAWTTrivialActual.makeFont(text);
    PTextRendererAWTTrivialActual.setAntialiasingMode(text, this.scratch_g);

    final FontRenderContext frc =
      NullCheck.notNull(this.scratch_g.getFontRenderContext());
    final float width_limit =
      PTextRendererAWTTrivialActual.calculateWidthLimit(
        font,
        text.getWrappingMode(),
        text.getWrappingWidth(),
        frc);

    final String text_data = text.getText();
    final AttributedString as =
      PTextRendererAWTTrivialActual.makeAttributedText(font, text_data);

    final AttributedCharacterIterator as_iterator =
      NullCheck.notNull(as.getIterator());

    {
      final LineBreakMeasurer measurer =
        new LineBreakMeasurer(as_iterator, frc);

      PTextRendererAWTTrivialActual.renderAndMeasure(
        text_data,
        as_iterator,
        measurer,
        width_limit,
        this.size,
        this.scratch_g,
        false);
    }

    final int image_width = (int) Math.ceil(this.size.getXF());
    final int image_height = (int) Math.ceil(this.size.getYF());
    final BufferedImage image =
      PTextRendererAWTTrivialActual.makeImage(
        image_width,
        image_height,
        actual_type);
    final Graphics2D image_g = NullCheck.notNull(image.createGraphics());
    PTextRendererAWTTrivialActual.setAntialiasingMode(text, image_g);

    {
      as_iterator.first();
      final LineBreakMeasurer measurer =
        new LineBreakMeasurer(as_iterator, frc);

      PTextRendererAWTTrivialActual.renderAndMeasure(
        text_data,
        as_iterator,
        measurer,
        width_limit,
        this.size,
        image_g,
        true);
    }

    return PTextRendererAWTTrivialActual.makeCompiledText(
      image,
      this.gi,
      this.type,
      this.size,
      actual_type,
      text.getMinificationFilter(),
      text.getMagnificationFilter(),
      this.id);
  }

  @Override public PTextCompiledType compileUnmeasuredTextWithHardBounds(
    final PTextUnmeasured text,
    final int width,
    final int height)
  {
    NullCheck.notNull(text, "Text");

    this.id = this.nextID();

    final PTextureType actual_type =
      PTextRendererAWTTrivialActual.getActualSupportedTextureType(
        this.gi,
        text.getTextureType());

    final BufferedImage image =
      PTextRendererAWTTrivialActual.makeImage(width, height, actual_type);
    final Graphics2D g = NullCheck.notNull(image.createGraphics());

    final Font font = PTextRendererAWTTrivialActual.makeFont(text);
    PTextRendererAWTTrivialActual.setAntialiasingMode(text, g);

    final FontRenderContext frc = NullCheck.notNull(g.getFontRenderContext());
    final float width_limit =
      PTextRendererAWTTrivialActual.calculateWidthLimit(
        font,
        text.getWrappingMode(),
        text.getWrappingWidth(),
        frc);

    final String text_data = text.getText();
    final AttributedString as =
      PTextRendererAWTTrivialActual.makeAttributedText(font, text_data);

    final AttributedCharacterIterator as_iterator =
      NullCheck.notNull(as.getIterator());
    final LineBreakMeasurer measurer =
      new LineBreakMeasurer(as_iterator, frc);

    PTextRendererAWTTrivialActual.renderAndMeasure(
      text_data,
      as_iterator,
      measurer,
      width_limit,
      this.size,
      g,
      true);

    this.size.set2F(width, height);
    return PTextRendererAWTTrivialActual.makeCompiledText(
      image,
      this.gi,
      this.type,
      this.size,
      actual_type,
      text.getMinificationFilter(),
      text.getMagnificationFilter(),
      this.id);
  }

  @Override public PTypefaceType getDefault()
  {
    return PTextRendererAWTTrivialActual.TYPEFACE_DEFAULT;
  }

  @Override public PTypefaceType getMonospace()
  {
    return PTextRendererAWTTrivialActual.TYPEFACE_MONOSPACED;
  }

  @Override public PTypefaceType getSansSerif()
  {
    return PTextRendererAWTTrivialActual.TYPEFACE_SANS;
  }

  @Override public PTypefaceType getSerif()
  {
    return PTextRendererAWTTrivialActual.TYPEFACE_SERIF;
  }

  @Override public PTextMeasuredType measureText(
    final PTextUnmeasured text)
  {
    NullCheck.notNull(text, "Text");

    final Font font = PTextRendererAWTTrivialActual.makeFont(text);
    PTextRendererAWTTrivialActual.setAntialiasingMode(text, this.scratch_g);

    final FontRenderContext frc =
      NullCheck.notNull(this.scratch_g.getFontRenderContext());
    final String text_data = text.getText();
    final AttributedString as =
      PTextRendererAWTTrivialActual.makeAttributedText(font, text_data);
    final AttributedCharacterIterator as_iterator =
      NullCheck.notNull(as.getIterator());
    final LineBreakMeasurer measurer =
      new LineBreakMeasurer(as_iterator, frc);

    final float width_limit =
      PTextRendererAWTTrivialActual.calculateWidthLimit(
        font,
        text.getWrappingMode(),
        text.getWrappingWidth(),
        frc);

    PTextRendererAWTTrivialActual.renderAndMeasure(
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

  private BigInteger nextID()
  {
    return NullCheck.notNull(this.id.add(BigInteger.ONE));
  }

  @Override public PTypefaceDefaultsType getTypefaceDefaults()
  {
    return this;
  }

  @Override public PTypefaceLoaderType getTypefaceLoader()
  {
    return this.loader;
  }
}
