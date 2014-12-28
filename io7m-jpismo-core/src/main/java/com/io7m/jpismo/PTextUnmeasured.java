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

package com.io7m.jpismo;

import com.io7m.jcanephora.TextureFilterMagnification;
import com.io7m.jcanephora.TextureFilterMinification;
import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jranges.RangeCheck;

/**
 * Text that has not been explicitly measured by a renderer.
 */

@EqualityStructural public final class PTextUnmeasured
{
  /**
   * Construct unmeasured text.
   *
   * @param in_face
   *          The typeface
   * @param in_font_size
   *          The font size
   * @param in_mode
   *          The wrapping mode
   * @param in_text
   *          The text
   * @param in_width
   *          The wrapping width
   * @param in_anti
   *          The antialiasing mode
   * @param in_min
   *          The minification filter
   * @param in_mag
   *          The magnification filter
   * @param in_ttype
   *          The texture type
   * @return Unmeasured text
   */

  public static PTextUnmeasured newText(
    final PTypefaceType in_face,
    final float in_font_size,
    final PTextWrapping in_mode,
    final String in_text,
    final float in_width,
    final PTextAntialiasing in_anti,
    final TextureFilterMinification in_min,
    final TextureFilterMagnification in_mag,
    final PTextureType in_ttype)
  {
    return new PTextUnmeasured(
      in_face,
      in_font_size,
      in_mode,
      in_text,
      in_width,
      in_anti,
      in_min,
      in_mag,
      in_ttype);
  }

  private final PTextAntialiasing          anti;
  private final PTypefaceType              face;
  private final float                      font_size;
  private final TextureFilterMagnification mag;
  private final TextureFilterMinification  min;
  private final PTextWrapping              mode;
  private final String                     text;
  private final PTextureType               ttype;
  private final float                      width;

  private PTextUnmeasured(
    final PTypefaceType in_face,
    final float in_font_size,
    final PTextWrapping in_mode,
    final String in_text,
    final float in_width,
    final PTextAntialiasing in_anti,
    final TextureFilterMinification in_min,
    final TextureFilterMagnification in_mag,
    final PTextureType in_ttype)
  {
    this.face = NullCheck.notNull(in_face);
    this.font_size =
      (float) RangeCheck.checkGreaterDouble(
        in_font_size,
        "Size",
        0.0,
        "Minimum size");
    this.mode = NullCheck.notNull(in_mode);
    this.text = NullCheck.notNull(in_text);
    this.width =
      (float) RangeCheck.checkGreaterDouble(
        in_width,
        "Width",
        0.0,
        "Minimum width");
    this.anti = NullCheck.notNull(in_anti, "Antialiasing");
    this.min = NullCheck.notNull(in_min, "Minification filter");
    this.mag = NullCheck.notNull(in_mag, "Magnification filter");
    this.ttype = NullCheck.notNull(in_ttype, "Texture type");
  }

  @Override public boolean equals(
    final @Nullable Object obj)
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
    final PTextUnmeasured other = (PTextUnmeasured) obj;
    return (this.anti == other.anti)
      && (this.face.equals(other.face))
      && (Float.floatToIntBits(this.font_size) == Float
        .floatToIntBits(other.font_size))
      && (this.mag == other.mag)
      && (this.min == other.min)
      && (this.mode == other.mode)
      && (this.text.equals(other.text))
      && (Float.floatToIntBits(this.width) == Float
        .floatToIntBits(other.width))
      && (this.ttype == other.ttype);
  }

  /**
   * @return The type of antialiasing that will be used
   */

  public PTextAntialiasing getAntialisingMode()
  {
    return this.anti;
  }

  /**
   * @return The size of the font
   */

  public float getFontSize()
  {
    return this.font_size;
  }

  /**
   * @return The magnification filter
   */

  public TextureFilterMagnification getMagnificationFilter()
  {
    return this.mag;
  }

  /**
   * @return The minification filter
   */

  public TextureFilterMinification getMinificationFilter()
  {
    return this.min;
  }

  /**
   * @return The source text
   */

  public String getText()
  {
    return this.text;
  }

  /**
   * @return The texture type
   */

  public PTextureType getTextureType()
  {
    return this.ttype;
  }

  /**
   * @return The typeface
   */

  public PTypefaceType getTypeface()
  {
    return this.face;
  }

  /**
   * @return The text wrapping mode
   */

  public PTextWrapping getWrappingMode()
  {
    return this.mode;
  }

  /**
   * @return The wrapping width
   */

  public float getWrappingWidth()
  {
    return this.width;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.anti.hashCode();
    result = (prime * result) + this.face.hashCode();
    result = (prime * result) + Float.floatToIntBits(this.font_size);
    result = (prime * result) + this.mag.hashCode();
    result = (prime * result) + this.min.hashCode();
    result = (prime * result) + this.mode.hashCode();
    result = (prime * result) + this.text.hashCode();
    result = (prime * result) + Float.floatToIntBits(this.width);
    result = (prime * result) + this.ttype.hashCode();
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[PTextUnmeasured anti=");
    builder.append(this.anti);
    builder.append(", face=");
    builder.append(this.face);
    builder.append(", font_size=");
    builder.append(this.font_size);
    builder.append(", mode=");
    builder.append(this.mode);
    builder.append(", text=");
    builder.append(this.text);
    builder.append(", width=");
    builder.append(this.width);
    builder.append(", min=");
    builder.append(this.min);
    builder.append(", mag=");
    builder.append(this.mag);
    builder.append(", ttype=");
    builder.append(this.ttype);
    builder.append("]");
    return NullCheck.notNull(builder.toString());
  }
}
