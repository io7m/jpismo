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

/**
 * The type of text builders.
 */

public interface PTextBuilderType
{
  /**
   * <p> Build unmeasured text. The text will be wrapped based on the given
   * wrapping mode. </p>
   *
   * @param face       The typeface to use
   * @param font_size  The font size to use
   * @param text       The text
   * @param wrap_mode  The text wrapping mode
   * @param wrap_width The text wrapping width
   *
   * @return Unmeasured text based on all of the parameters given so far.
   */

  PTextUnmeasuredType buildText(
    final PTypefaceType face,
    final float font_size,
    final String text,
    final PTextWrapping wrap_mode,
    final float wrap_width);

  /**
   * <p> Set the antialiasing mode. </p>
   *
   * @param anti The mode
   */

  void setAntialiasingMode(
    final PTextAntialiasing anti);

  /**
   * <p> Set the magnification filter used for the text. The default is {@link
   * JCGLTextureFilterMagnification#TEXTURE_FILTER_NEAREST}. </p>
   *
   * @param f The filter
   */

  void setMagnificationFilter(
    JCGLTextureFilterMagnification f);

  /**
   * <p> Set the minification filter used for the text. The default is {@link
   * JCGLTextureFilterMinification#TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST}. </p>
   *
   * @param f The filter
   */

  void setMinificationFilter(
    JCGLTextureFilterMinification f);

  /**
   * <p> Set the texture type that will be used for textures. The default is
   * {@link PTextureType#TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED}. </p> <p>
   * Note that OpenGL ES2 is not capable of supporting almost all of the texture
   * types, and so renderers will typically fall back to {@link
   * PTextureType#TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED}. </p>
   *
   * @param t The texture type
   */

  void setTextureType(
    final PTextureType t);
}
