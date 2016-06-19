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

package com.io7m.jpismo;

import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jranges.RangeCheck;

/**
 * The main {@link PTextBuilderType} implementation.
 */

@EqualityReference
public final class PTextBuilder implements PTextBuilderType
{
  private PTextAntialiasing anti;
  private float font_size;
  private JCGLTextureFilterMinification min;
  private JCGLTextureFilterMagnification mag;
  private PTextureType ttype;
  private PTextBuilder()
  {
    this.anti = PTextAntialiasing.TEXT_ANTIALIASING_FAST;
    this.min =
      JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST;
    this.mag = JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST;
    this.ttype = PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED;
  }

  /**
   * @return A new text builder
   */

  public static PTextBuilderType newBuilder()
  {
    return new PTextBuilder();
  }

  @Override
  public void setAntialiasingMode(
    final PTextAntialiasing in_anti)
  {
    this.anti = NullCheck.notNull(in_anti, "Antialiasing");
  }

  @Override
  public void setMinificationFilter(
    final JCGLTextureFilterMinification f)
  {
    this.min = NullCheck.notNull(f, "Filter");
  }

  @Override
  public void setMagnificationFilter(
    final JCGLTextureFilterMagnification f)
  {
    this.mag = NullCheck.notNull(f, "Filter");
  }

  @Override
  public PTextUnmeasuredType buildText(
    final PTypefaceType in_face,
    final float in_font_size,
    final String in_text,
    final PTextWrapping in_wrap_mode,
    final float in_wrap_width)
  {
    NullCheck.notNull(in_face, "Typeface");
    RangeCheck.checkGreaterDouble(
      in_font_size,
      "Font size",
      0.0,
      "Minimum font size");
    NullCheck.notNull(in_text, "Text");
    NullCheck.notNull(in_wrap_mode, "Wrapping mode");
    RangeCheck.checkGreaterDouble(
      in_wrap_width,
      "Wrapping width",
      0.0,
      "Minimum wrapping width");

    return PTextUnmeasured.of(
      this.anti,
      in_face,
      in_font_size,
      this.mag,
      this.min,
      in_wrap_mode,
      in_text,
      this.ttype,
      in_wrap_width);
  }

  @Override
  public void setTextureType(
    final PTextureType t)
  {
    this.ttype = NullCheck.notNull(t, "Type");
  }
}
