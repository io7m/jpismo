package com.io7m.jpismo;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.GLResource;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jcanephora.Texture2DRGBAStatic;

/**
 * <p>
 * The {@link CompiledText} class represents, unsurprisingly, text that has
 * been compiled into an efficient format for rendering.
 * </p>
 */

public final class CompiledText implements GLResource
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

  /**
   * Delete the vertex and index buffers for this particular piece of compiled
   * text (note that the texture is shared between values of type
   * {@link CompiledText} and must not be deleted by the user).
   */

  @Override public void delete(
    final @Nonnull GLInterface gl)
    throws ConstraintError,
      GLException
  {
    gl.deleteArrayBuffer(this.getVertexBuffer());
    gl.deleteIndexBuffer(this.getIndexBuffer());
  }

  /**
   * Retrieve the index buffer containing the indices of the triangles stored
   * in the vertex buffer.
   * 
   * @see CompiledText#getVertexBuffer()
   */

  public @Nonnull IndexBuffer getIndexBuffer()
  {
    return this.index_buffer;
  }

  /**
   * Retrieve the texture that contains the compiled text images.
   */

  public @Nonnull Texture2DRGBAStatic getTexture()
  {
    return this.texture;
  }

  /**
   * Retrieve the vertex buffer containing triangle and texture coordinate
   * data for the compiled text.
   * 
   * @see CompiledText#getVertexBuffer()
   */

  public @Nonnull ArrayBuffer getVertexBuffer()
  {
    return this.vertex_buffer;
  }
}
