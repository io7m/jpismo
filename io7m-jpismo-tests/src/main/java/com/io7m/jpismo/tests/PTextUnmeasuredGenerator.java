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

import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jpismo.PTextAntialiasing;
import com.io7m.jpismo.PTextUnmeasured;
import com.io7m.jpismo.PTextUnmeasuredType;
import com.io7m.jpismo.PTextWrapping;
import com.io7m.jpismo.PTextureType;
import com.io7m.jpismo.PTypefaceType;
import net.java.quickcheck.Generator;

/**
 * A text generator.
 */

public final class PTextUnmeasuredGenerator implements
  Generator<PTextUnmeasuredType>
{
  private final Generator<PTypefaceType> face_gen;
  private final Generator<PTextWrapping> wrap_gen;
  private final Generator<String> text_gen;
  private final Generator<PTextAntialiasing> anti_gen;
  private final Generator<JCGLTextureFilterMinification> fmin_gen;
  private final Generator<JCGLTextureFilterMagnification> fmag_gen;
  private final Generator<PTextureType> ttype_gen;

  /**
   * Construct a generator.
   *
   * @param in_face_gen  Typeface generator
   * @param in_wrap_gen  Wrapping mode generator
   * @param in_text_gen  Text generator
   * @param in_anti_gen  Antialiasing mode generator
   * @param in_fmin_gen  Filter generator
   * @param in_fmag_gen  Filter generator
   * @param in_ttype_gen Texture type generator
   */

  public PTextUnmeasuredGenerator(
    final Generator<PTypefaceType> in_face_gen,
    final Generator<PTextWrapping> in_wrap_gen,
    final Generator<String> in_text_gen,
    final Generator<PTextAntialiasing> in_anti_gen,
    final Generator<JCGLTextureFilterMinification> in_fmin_gen,
    final Generator<JCGLTextureFilterMagnification> in_fmag_gen,
    final Generator<PTextureType> in_ttype_gen)
  {
    this.face_gen = in_face_gen;
    this.wrap_gen = in_wrap_gen;
    this.text_gen = in_text_gen;
    this.anti_gen = in_anti_gen;
    this.fmin_gen = in_fmin_gen;
    this.fmag_gen = in_fmag_gen;
    this.ttype_gen = in_ttype_gen;
  }

  @Override
  public PTextUnmeasuredType next()
  {
    final PTypefaceType in_face = this.face_gen.next();
    final float in_font_size = (float) (Math.random() * 100.0f);
    final PTextWrapping in_mode = this.wrap_gen.next();
    final String in_text = this.text_gen.next();

    float in_width = 1.0f;
    switch (in_mode) {
      case TEXT_WRAPPING_COLUMNS: {
        in_width = (float) (Math.random() * 80.0f);
        break;
      }
      case TEXT_WRAPPING_NONE: {
        in_width = 1.0f;
        break;
      }
      case TEXT_WRAPPING_PIXELS: {
        in_width = (float) (Math.random() * 600.0f);
        break;
      }
    }

    final PTextAntialiasing in_anti = this.anti_gen.next();
    final JCGLTextureFilterMinification in_min = this.fmin_gen.next();
    final JCGLTextureFilterMagnification in_mag = this.fmag_gen.next();
    final PTextureType in_ttype = this.ttype_gen.next();

    return PTextUnmeasured.of(
      in_anti,
      in_face,
      in_font_size,
      in_mag,
      in_min,
      in_mode,
      in_text,
      in_ttype,
      in_width);
  }
}
