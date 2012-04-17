package com.io7m.jpismo;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jcanephora.Texture2DRGBAStatic;

public final class CompiledText
{
  private final @Nonnull ArrayBuffer         vertex_buffer;
  private final @Nonnull IndexBuffer         index_buffer;
  private final @Nonnull Texture2DRGBAStatic texture;

  CompiledText(
    final @Nonnull ArrayBuffer vertex_buffer,
    final @Nonnull IndexBuffer index_buffer,
    final @Nonnull Texture2DRGBAStatic texture)
    throws ConstraintError
  {
    this.vertex_buffer =
      Constraints.constrainNotNull(vertex_buffer, "Vertex buffer");
    this.index_buffer =
      Constraints.constrainNotNull(index_buffer, "Index buffer");
    this.texture = Constraints.constrainNotNull(texture, "Texture");
  }

  public @Nonnull IndexBuffer getIndexBuffer()
  {
    return this.index_buffer;
  }

  public @Nonnull Texture2DRGBAStatic getTexture()
  {
    return this.texture;
  }

  public @Nonnull ArrayBuffer getVertexBuffer()
  {
    return this.vertex_buffer;
  }
}
