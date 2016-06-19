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

import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.JCGLException;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLIndexBuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;
import com.io7m.jpismo.PTextCompiledType;
import com.io7m.jpismo.PTextureType;
import com.io7m.jranges.RangeCheck;
import com.io7m.jranges.RangeInclusiveL;

@EqualityReference
final class PTextCompiledAWT implements PTextCompiledType
{
  private static final RangeInclusiveL VALID_INDICES;

  static {
    VALID_INDICES = new RangeInclusiveL(0L, 0L);
  }

  private final JCGLArrayBufferType array_buffer;
  private final JCGLIndexBufferType index_buffer;
  private final JCGLArrayObjectType array_object;
  private final JCGLTexture2DType texture;
  private final PTextureType type;
  private boolean deleted;
  PTextCompiledAWT(
    final JCGLArrayObjectType in_array_object,
    final JCGLArrayBufferType in_array_buffer,
    final JCGLIndexBufferType in_index_buffer,
    final JCGLTexture2DType in_texture,
    final PTextureType actual_type)
  {
    this.array_object = NullCheck.notNull(in_array_object);
    this.array_buffer = NullCheck.notNull(in_array_buffer);
    this.index_buffer = NullCheck.notNull(in_index_buffer);
    this.texture = NullCheck.notNull(in_texture);
    this.type = NullCheck.notNull(actual_type, "Actual type");
  }

  private static void checkIndex(
    final int in_index)
  {
    RangeCheck.checkIncludedInLong(
      (long) in_index,
      "Index",
      PTextCompiledAWT.VALID_INDICES,
      "Valid indices");
  }

  @Override
  public boolean isDeleted()
  {
    return this.deleted;
  }

  @Override
  public void textDelete(
    final JCGLInterfaceGL33Type gi)
    throws JCGLException
  {
    try {
      final JCGLArrayBuffersType g_ab = gi.getArrayBuffers();
      final JCGLIndexBuffersType g_ib = gi.getIndexBuffers();
      final JCGLArrayObjectsType g_ao = gi.getArrayObjects();
      g_ab.arrayBufferDelete(this.array_buffer);
      g_ib.indexBufferDelete(this.index_buffer);
      g_ao.arrayObjectDelete(this.array_object);
    } finally {
      this.deleted = true;
    }
  }

  @Override
  public JCGLArrayObjectUsableType textGetArrayObject(final int in_index)
  {
    PTextCompiledAWT.checkIndex(in_index);
    return this.array_object;
  }

  @Override
  public PTextureType textGetActualTextureType()
  {
    return this.type;
  }

  @Override
  public float textGetHeight()
  {
    return (float) this.texture.textureGetHeight();
  }

  @Override
  public int textGetPageCount()
  {
    return 1;
  }

  @Override
  public JCGLTexture2DUsableType textGetTexture(
    final int in_index)
  {
    PTextCompiledAWT.checkIndex(in_index);
    return this.texture;
  }

  @Override
  public float textGetWidth()
  {
    return (float) this.texture.textureGetWidth();
  }

  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder(128);
    builder.append("[PTextCompiledAWT array=");
    builder.append(this.array_object);
    builder.append(", deleted=");
    builder.append(this.deleted);
    builder.append(", texture=");
    builder.append(this.texture);
    builder.append(", type=");
    builder.append(this.type);
    builder.append("]");
    final String s = builder.toString();
    return NullCheck.notNull(s);
  }
}
