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

import java.awt.Font;

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jpismo.PExceptionTypefaceWrongImplementation;
import com.io7m.jpismo.PTypefaceType;

@EqualityStructural final class PTypefaceAWT implements PTypefaceType
{
  static PTypefaceAWT checkFace(
    final PTypefaceType face_raw)
  {
    if ((face_raw instanceof PTypefaceAWT) == false) {
      throw PExceptionTypefaceWrongImplementation
        .wrongImplementation(face_raw);
    }
    final PTypefaceAWT face = (PTypefaceAWT) face_raw;
    return face;
  }

  private final Font font;

  PTypefaceAWT(
    final Font in_font)
  {
    this.font = NullCheck.notNull(in_font);
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final PTypefaceAWT other = (PTypefaceAWT) obj;
    return this.font.equals(other.font);
  }

  Font getFont()
  {
    return this.font;
  }

  @Override public String getName()
  {
    return NullCheck.notNull(this.font.getFamily());
  }

  @Override public int hashCode()
  {
    return this.font.hashCode();
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[PTypefaceAWT font=");
    builder.append(this.font);
    builder.append("]");
    return NullCheck.notNull(builder.toString());
  }
}
