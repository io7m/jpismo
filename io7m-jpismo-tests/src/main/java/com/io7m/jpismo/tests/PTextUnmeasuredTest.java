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

package com.io7m.jpismo.tests;

import net.java.quickcheck.Generator;
import net.java.quickcheck.QuickCheck;
import net.java.quickcheck.characteristic.AbstractCharacteristic;
import net.java.quickcheck.generator.support.CharacterGenerator;
import net.java.quickcheck.generator.support.IntegerGenerator;
import net.java.quickcheck.generator.support.StringGenerator;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jcanephora.TextureFilterMagnification;
import com.io7m.jcanephora.TextureFilterMinification;
import com.io7m.jnull.Nullable;
import com.io7m.jpismo.PTextAntialiasing;
import com.io7m.jpismo.PTextUnmeasured;
import com.io7m.jpismo.PTextWrapping;
import com.io7m.jpismo.PTextureType;
import com.io7m.jpismo.PTypefaceType;

/**
 * Unmeasured text.
 */

// CHECKSTYLE_JAVADOC:OFF

@SuppressWarnings("static-method") public final class PTextUnmeasuredTest
{
  public PTextUnmeasuredTest()
  {

  }

  @Test public void testEquals()
  {
    final Generator<PTypefaceType> in_face_gen = new PTypefaceGenerator();
    final Generator<PTextWrapping> in_wrap_gen = new PTextWrappingGenerator();
    final Generator<Integer> length_gen = new IntegerGenerator(10, 1024);
    final Generator<Character> char_gen = new CharacterGenerator();
    final Generator<String> in_text_gen =
      new StringGenerator(length_gen, char_gen);
    final Generator<PTextAntialiasing> in_anti_gen =
      new PTextAntialiasingGenerator();
    final Generator<TextureFilterMinification> in_fmin_gen =
      new TextureFilterMinificationGenerator();
    final Generator<TextureFilterMagnification> in_fmag_gen =
      new TextureFilterMagnificationGenerator();
    final Generator<PTextureType> in_ttype_gen = new PTextureTypeGenerator();
    final PTextUnmeasuredGenerator gen =
      new PTextUnmeasuredGenerator(
        in_face_gen,
        in_wrap_gen,
        in_text_gen,
        in_anti_gen,
        in_fmin_gen,
        in_fmag_gen,
        in_ttype_gen);

    QuickCheck.forAllVerbose(
      gen,
      new AbstractCharacteristic<PTextUnmeasured>() {
        @Override protected void doSpecify(
          final @Nullable PTextUnmeasured any)
        {
          assert any != null;

          final PTextUnmeasured other = gen.next();
          Assert.assertEquals(any, any);
          Assert.assertNotEquals(any, other);
          Assert.assertNotEquals(other, any);
          Assert.assertNotEquals(any, Integer.valueOf(23));
          Assert.assertNotEquals(any, null);

          final PTextUnmeasured same =
            PTextUnmeasured.newText(
              any.getTypeface(),
              any.getFontSize(),
              any.getWrappingMode(),
              any.getText(),
              any.getWrappingWidth(),
              any.getAntialisingMode(),
              any.getMinificationFilter(),
              any.getMagnificationFilter(),
              any.getTextureType());

          Assert.assertEquals(same, any);
          Assert.assertEquals(any, same);

          Assert.assertEquals(any.hashCode(), any.hashCode());
          Assert.assertEquals(same.hashCode(), same.hashCode());
          Assert.assertEquals(any.hashCode(), same.hashCode());

          Assert.assertEquals(any.toString(), any.toString());
          Assert.assertEquals(same.toString(), same.toString());
          Assert.assertEquals(any.toString(), same.toString());
        }
      });
  }
}
