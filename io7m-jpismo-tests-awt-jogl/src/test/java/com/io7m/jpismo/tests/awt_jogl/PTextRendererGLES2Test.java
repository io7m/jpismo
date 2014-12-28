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

package com.io7m.jpismo.tests.awt_jogl;

import com.io7m.jcanephora.api.JCGLImplementationType;
import com.io7m.jpismo.PTextRendererType;
import com.io7m.jpismo.PTypefaceLoaderType;
import com.io7m.jpismo.awt.PTextRendererAWTTrivial;
import com.io7m.jpismo.tests.PTextRendererContract;

public final class PTextRendererGLES2Test extends PTextRendererContract
{
  @Override public JCGLImplementationType getGLImplementation()
  {
    return PJOGLTestContext.makeContextWithOpenGL_ES2(true);
  }

  @Override public PTextRendererType getTextRenderer(
    final JCGLImplementationType gi,
    final PTypefaceLoaderType loader)
  {
    return PTextRendererAWTTrivial.newTextRenderer(gi, loader);
  }

  @Override public boolean isGLSupported()
  {
    return PJOGLTestContext.isOpenGLES2Supported();
  }

  @Override public PTypefaceLoaderType getTypefaceLoader()
  {
    return new PTypefaceLoaderAWT();
  }
}
