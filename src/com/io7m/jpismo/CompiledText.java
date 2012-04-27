package com.io7m.jpismo;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.GLResource;

/**
 * <p>
 * The {@link CompiledText} class represents, unsurprisingly, text that has
 * been compiled into an efficient format for rendering.
 * </p>
 */

public final class CompiledText implements GLResource
{
  final @Nonnull ArrayList<CompiledPage> pages;
  private float                          height = 0.0f;
  private float                          width  = 0.0f;

  CompiledText()
  {
    this.pages = new ArrayList<CompiledPage>();
  }

  void addPage(
    final @Nonnull CompiledPage page)
  {
    this.pages.add(page);
  }

  public float getHeight()
  {
    return this.height;
  }

  public @Nonnull CompiledPage getPage(
    final int index)
  {
    return this.pages.get(index);
  }

  public float getWidth()
  {
    return this.width;
  }

  public int maxPages()
  {
    return this.pages.size();
  }

  @Override public void resourceDelete(
    final @Nonnull GLInterface gl)
    throws ConstraintError,
      GLException
  {
    for (final CompiledPage page : this.pages) {
      page.resourceDelete(gl);
    }
  }

  void setHeight(
    final float h)
  {
    this.height = h;
  }

  void setWidth(
    final float w)
  {
    this.width = w;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[CompiledText ");
    builder.append(this.width);
    builder.append("x");
    builder.append(this.height);
    builder.append("]");
    return builder.toString();
  }
}
