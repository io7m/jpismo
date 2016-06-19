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

package com.io7m.jpismo.tests.awt_jogl;

import com.io7m.jcanephora.core.api.JCGLContextUsableType;
import com.io7m.jpismo.PTextRendererType;
import com.io7m.jpismo.PTypefaceLoaderType;
import com.io7m.jpismo.awt.PTextRendererAWTTrivial;
import com.io7m.jpismo.tests.PTextRendererContract;

public final class PTextRendererGL3pTest extends PTextRendererContract
{
  @Override
  protected JCGLContextUsableType newContext(
    final String name,
    final int depth,
    final int stencil)
  {
    return PJOGLTestContexts.newGL33Context(name, depth, stencil);
  }

  @Override
  public PTypefaceLoaderType getTypefaceLoader()
  {
    return new PTypefaceLoaderAWT();
  }

  @Override
  protected PTextRendererType getTextRenderer(
    final JCGLContextUsableType gc,
    final PTypefaceLoaderType loader)
  {
    return PTextRendererAWTTrivial.create(loader);
  }

  @Override
  public void onTestCompleted()
  {
    PJOGLTestContexts.closeAllContexts();
  }
}
