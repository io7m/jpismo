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

/**
 * The type of text renderers.
 */

public interface PTextRendererType
{
  /**
   * @return The default typefaces.
   */

  PTypefaceDefaultsType getTypefaceDefaults();

  /**
   * @return The implementation's typeface loader.
   */

  PTypefaceLoaderType getTypefaceLoader();

  /**
   * Measure the given text.
   *
   * @return Measured text
   * @param text
   *          The unmeasured text
   */

  PTextMeasuredType measureText(
    final PTextUnmeasured text);

  /**
   * Render the given measured text.
   *
   * @param text
   *          The text
   * @return Compiled text
   */

  PTextCompiledType compileMeasuredText(
    final PTextMeasuredType text);

  /**
   * Render the given unmeasured text, unconditionally creating an image of
   * width <code>width</code> and height <code>height</code>.
   *
   * @param text
   *          The text
   * @param width
   *          The image width
   * @param height
   *          The image height
   * @return Compiled text
   */

  PTextCompiledType compileUnmeasuredTextWithHardBounds(
    final PTextUnmeasured text,
    final int width,
    final int height);

  /**
   * Render the given unmeasured text, creating an image large enough to
   * contain the text given its wrapping mode and width.
   *
   * @param text
   *          The text
   * @return Compiled text
   */

  PTextCompiledType compileUnmeasuredText(
    final PTextUnmeasured text);
}
