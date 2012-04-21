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
 * The {@link CompiledPage} class represents one part of a piece of compiled
 * text. Most text renderers will produce a set of <code>CompiledPage</code>s
 * as output (returned in a {@link CompiledText} value).
 * </p>
 * 
 * @see {@link CompiledText}
 */

public final class CompiledPage implements GLResource
{
  private final @Nonnull ArrayBuffer         vertex_buffer;
  private final @Nonnull IndexBuffer         index_buffer;
  private final @Nonnull Texture2DRGBAStatic texture;
  private final boolean                      texture_owned;

  CompiledPage(
    final @Nonnull ArrayBuffer vertex_buffer,
    final @Nonnull IndexBuffer index_buffer,
    final @Nonnull Texture2DRGBAStatic texture,
    final boolean texture_owned)
    throws ConstraintError
  {
    this.vertex_buffer =
      Constraints.constrainNotNull(vertex_buffer, "Vertex buffer");
    this.index_buffer =
      Constraints.constrainNotNull(index_buffer, "Index buffer");
    this.texture = Constraints.constrainNotNull(texture, "Texture");
    this.texture_owned = texture_owned;
  }

  /**
   * Delete the vertex and index buffers for this particular piece of compiled
   * text (note that the texture may be shared between values of type
   * {@link CompiledPage} and therefore may not be deleted by the user).
   */

  @Override public void delete(
    final @Nonnull GLInterface gl)
    throws ConstraintError,
      GLException
  {
    gl.deleteArrayBuffer(this.getVertexBuffer());
    gl.deleteIndexBuffer(this.getIndexBuffer());

    if (this.texture_owned) {
      gl.deleteTexture2DRGBAStatic(this.texture);
    }
  }

  /**
   * Retrieve the index buffer containing the indices of the triangles stored
   * in the vertex buffer.
   * 
   * @see CompiledPage#getVertexBuffer()
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
   * @see CompiledPage#getVertexBuffer()
   */

  public @Nonnull ArrayBuffer getVertexBuffer()
  {
    return this.vertex_buffer;
  }
}
