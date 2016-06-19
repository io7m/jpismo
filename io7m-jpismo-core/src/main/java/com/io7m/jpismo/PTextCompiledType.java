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

import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.JCGLException;
import com.io7m.jcanephora.core.JCGLResourceUsableType;
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;

/**
 * The type of compiled text.
 */

public interface PTextCompiledType extends JCGLResourceUsableType
{
  /**
   * Delete all resources associated with the compiled text.
   *
   * @param gi The OpenGL implementation
   *
   * @throws JCGLException On OpenGL errors
   */

  void textDelete(
    final JCGLInterfaceGL33Type gi)
    throws JCGLException;

  /**
   * @param index The page index
   *
   * @return The array object for page {@code index}
   */

  JCGLArrayObjectUsableType textGetArrayObject(
    int index);

  /**
   * @return The measured height of the text.
   */

  float textGetHeight();

  /**
   * @return The number of pages used to represent the compiled text
   */

  int textGetPageCount();

  /**
   * @param index The page index
   *
   * @return The texture for page {@code index}
   */

  JCGLTexture2DUsableType textGetTexture(
    int index);

  /**
   * @return The measured width of the text.
   */

  float textGetWidth();

  /**
   * @return The texture type that was used by the renderer; may differ from the
   * requested type depending on the underlying OpenGL version
   */

  PTextureType textGetActualTextureType();
}
