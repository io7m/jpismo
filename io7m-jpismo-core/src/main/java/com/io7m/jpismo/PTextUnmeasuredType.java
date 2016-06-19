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
import org.immutables.value.Value;

/**
 * Text that has not been explicitly measured by a renderer.
 */

@PImmutableStyleType
@Value.Immutable
public interface PTextUnmeasuredType
{
  /**
   * @return The type of antialiasing that will be used
   */

  @Value.Parameter
  PTextAntialiasing getAntialisingMode();

  /**
   * @return The typeface
   */

  @Value.Parameter
  PTypefaceType getTypeface();

  /**
   * @return The size of the font
   */

  @Value.Parameter
  float getFontSize();

  /**
   * @return The magnification filter
   */

  @Value.Parameter
  JCGLTextureFilterMagnification getMagnificationFilter();

  /**
   * @return The minification filter
   */

  @Value.Parameter
  JCGLTextureFilterMinification getMinificationFilter();

  /**
   * @return The text wrapping mode
   */

  @Value.Parameter
  PTextWrapping getWrappingMode();

  /**
   * @return The source text
   */

  @Value.Parameter
  String getText();

  /**
   * @return The texture type
   */

  @Value.Parameter
  PTextureType getTextureType();

  /**
   * @return The wrapping width
   */

  @Value.Parameter
  float getWrappingWidth();
}
