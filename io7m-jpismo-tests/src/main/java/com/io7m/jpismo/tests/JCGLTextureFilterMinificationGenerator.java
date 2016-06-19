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

package com.io7m.jpismo.tests;

import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jnull.NullCheck;
import net.java.quickcheck.Generator;

/**
 * Generator for texture filters.
 */

public final class JCGLTextureFilterMinificationGenerator implements
  Generator<JCGLTextureFilterMinification>
{
  /**
   * Construct a new generator.
   */

  public JCGLTextureFilterMinificationGenerator()
  {

  }

  @Override
  public JCGLTextureFilterMinification next()
  {
    final JCGLTextureFilterMinification[] v = JCGLTextureFilterMinification.values();
    final int index = (int) (Math.random() * v.length);
    return NullCheck.notNull(v[index]);
  }
}
