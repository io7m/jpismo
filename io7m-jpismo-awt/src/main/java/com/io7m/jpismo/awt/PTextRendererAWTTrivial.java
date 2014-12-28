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

import com.io7m.jcanephora.api.JCGLImplementationType;
import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jpismo.PTextRendererType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * <p>
 * An AWT implementation of the {@link PTextRendererType} interface.
 * </p>
 */

@EqualityReference public final class PTextRendererAWTTrivial
{
  /**
   * Construct a new text renderer.
   *
   * @param in_gi
   *          An OpenGL implementation
   * @return A new text renderer
   */

  public static PTextRendererType newTextRenderer(
    final JCGLImplementationType in_gi)
  {
    return new PTextRendererAWTTrivialActual(in_gi);
  }

  private PTextRendererAWTTrivial()
  {
    throw new UnreachableCodeException();
  }
}