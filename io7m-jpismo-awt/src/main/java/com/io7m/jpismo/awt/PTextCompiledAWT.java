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

import com.io7m.jcanephora.ArrayBufferType;
import com.io7m.jcanephora.ArrayBufferUsableType;
import com.io7m.jcanephora.IndexBufferType;
import com.io7m.jcanephora.IndexBufferUsableType;
import com.io7m.jcanephora.JCGLException;
import com.io7m.jcanephora.Texture2DStaticType;
import com.io7m.jcanephora.Texture2DStaticUsableType;
import com.io7m.jcanephora.api.JCGLImplementationType;
import com.io7m.jcanephora.api.JCGLInterfaceCommonType;
import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jpismo.PTextCompiledType;
import com.io7m.jpismo.PTextureType;
import com.io7m.jranges.RangeCheck;
import com.io7m.jranges.RangeInclusiveL;

@EqualityReference final class PTextCompiledAWT implements PTextCompiledType
{
  private static final RangeInclusiveL VALID_INDICES;

  static {
    VALID_INDICES = new RangeInclusiveL(0, 0);
  }

  private static void checkIndex(
    final int in_index)
  {
    RangeCheck.checkIncludedIn(
      in_index,
      "Index",
      PTextCompiledAWT.VALID_INDICES,
      "Valid indices");
  }

  private final ArrayBufferType     array;
  private boolean                   deleted;
  private final IndexBufferType     index;
  private final Texture2DStaticType texture;
  private final PTextureType        type;

  PTextCompiledAWT(
    final ArrayBufferType in_array,
    final IndexBufferType in_index,
    final Texture2DStaticType in_texture,
    final PTextureType actual_type)
  {
    this.array = NullCheck.notNull(in_array);
    this.index = NullCheck.notNull(in_index);
    this.texture = NullCheck.notNull(in_texture);
    this.type = NullCheck.notNull(actual_type, "Actual type");
  }

  @Override public boolean resourceIsDeleted()
  {
    return this.deleted;
  }

  @Override public void textDelete(
    final JCGLImplementationType gi)
    throws JCGLException
  {
    try {
      final JCGLInterfaceCommonType gc = gi.getGLCommon();
      gc.arrayBufferDelete(this.array);
      gc.indexBufferDelete(this.index);
      gc.texture2DStaticDelete(this.texture);
    } finally {
      this.deleted = true;
    }
  }

  @Override public PTextureType textGetActualTextureType()
  {
    return this.type;
  }

  @Override public ArrayBufferUsableType textGetArrayBuffer(
    final int in_index)
  {
    PTextCompiledAWT.checkIndex(in_index);
    return this.array;
  }

  @Override public float textGetHeight()
  {
    return this.texture.textureGetHeight();
  }

  @Override public IndexBufferUsableType textGetIndexBuffer(
    final int in_index)
  {
    PTextCompiledAWT.checkIndex(in_index);
    return this.index;
  }

  @Override public int textGetPageCount()
  {
    return 1;
  }

  @Override public Texture2DStaticUsableType textGetTexture(
    final int in_index)
  {
    PTextCompiledAWT.checkIndex(in_index);
    return this.texture;
  }

  @Override public float textGetWidth()
  {
    return this.texture.textureGetWidth();
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[PTextCompiledAWT array=");
    builder.append(this.array);
    builder.append(", deleted=");
    builder.append(this.deleted);
    builder.append(", index=");
    builder.append(this.index);
    builder.append(", texture=");
    builder.append(this.texture);
    builder.append(", type=");
    builder.append(this.type);
    builder.append("]");
    final String s = builder.toString();
    return NullCheck.notNull(s);
  }
}
