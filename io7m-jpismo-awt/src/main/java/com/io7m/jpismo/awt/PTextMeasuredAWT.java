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

package com.io7m.jpismo.awt;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jpismo.PTextMeasuredType;
import com.io7m.jpismo.PTextUnmeasuredType;

import java.text.AttributedString;

@EqualityReference
final class PTextMeasuredAWT implements PTextMeasuredType
{
  private final AttributedString attr_text;
  private final float max_height;
  private final float max_width;
  private final PTextUnmeasuredType text;

  PTextMeasuredAWT(
    final PTextUnmeasuredType in_text,
    final AttributedString in_attr_text,
    final float in_max_width,
    final float in_max_height)
  {
    this.text = NullCheck.notNull(in_text, "Text");
    this.attr_text = NullCheck.notNull(in_attr_text, "Attribute text");
    this.max_width = in_max_width;
    this.max_height = in_max_height;
  }

  @Override
  public float textGetHeight()
  {
    return this.max_height;
  }

  @Override
  public PTextUnmeasuredType textGetUnmeasured()
  {
    return this.text;
  }

  @Override
  public float textGetWidth()
  {
    return this.max_width;
  }

  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[PTextMeasuredAWT attr_text=");
    builder.append(this.attr_text);
    builder.append(", max_height=");
    builder.append(this.max_height);
    builder.append(", max_width=");
    builder.append(this.max_width);
    builder.append(", text=");
    builder.append(this.text);
    builder.append("]");
    return NullCheck.notNull(builder.toString());
  }
}
