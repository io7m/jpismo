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

import com.io7m.jnull.NullCheck;

/**
 * The type of exceptions raised by attempting to use a font that was loaded
 * on one implementation on another.
 */

public final class PExceptionTypefaceWrongImplementation extends PException
{
  private PExceptionTypefaceWrongImplementation(
    final String message)
  {
    super(message);
  }

  /**
   * Construct an exception from the given typeface.
   *
   * @param face
   *          The typeface
   * @return An exception
   */

  public static PExceptionTypefaceWrongImplementation wrongImplementation(
    final PTypefaceType face)
  {
    final StringBuilder b = new StringBuilder();
    b
      .append("Attempted to use a loaded typeface on the wrong implementation.\n");
    b.append("  Typeface: ");
    b.append(face);
    b.append("\n");
    final String s = NullCheck.notNull(b.toString());
    return new PExceptionTypefaceWrongImplementation(s);
  }

  private static final long serialVersionUID;

  static {
    serialVersionUID = 5328139766524195605L;
  }

}
