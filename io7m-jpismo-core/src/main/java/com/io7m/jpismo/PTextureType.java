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

/**
 * The type of textures that a renderer will produce as output.
 */

public enum PTextureType
{
  /**
   * <p> Produce an RGBA texture as output. The text color (white) will be given
   * in the <code>(r, g, b)</code> channels, and the opacity in <code>a</code> .
   * </p> <p> It is assumed that the texture will be used in a context that
   * demands a "translucent" image. In other words, it will be rendered using
   * some form of blending. </p>
   */

  TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED,

  /**
   * <p> Produce an RG texture as output. The text color (white) is given as a
   * single intensity value in the <code>r</code> channel, and the opacity in
   * <code>g</code>. </p> <p> It is assumed that the texture will be used in a
   * context that demands a "translucent" image. In other words, it will be
   * rendered using some form of blending. </p>
   */

  TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED,

  /**
   * <p> Produce an R texture as output. The text color is given as a single
   * intensity value in the <code>r</code> channel. </p> <p> It is assumed that
   * the texture will be used in a context that demands an "opaque" image. In
   * other words, it will be rendered without any kind of blending (alpha, or
   * otherwise). </p>
   */

  TEXTURE_OPAQUE_R
}
