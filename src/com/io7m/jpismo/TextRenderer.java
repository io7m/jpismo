package com.io7m.jpismo;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;

public interface TextRenderer
{
  /**
   * Cache the given text in a renderer-specific manner.
   * 
   * @param line
   *          The text to cache.
   * @throws TextCacheException
   *           Iff the given text could not be cached.
   * @throws ConstraintError
   *           Iff <code>line == null</code>.
   * @throws GLException
   *           Iff an OpenGL error occurs.
   */

  void textCacheLine(
    final @Nonnull String line)
    throws GLException,
      ConstraintError,
      TextCacheException;

  /**
   * Upload any cached data to the OpenGL implementation.
   * 
   * @throws ConstraintError
   *           Iff an internal constraint error occurs.
   * @throws GLException
   *           Iff an OpenGL error occurs.
   */

  void textCacheUpload()
    throws GLException,
      ConstraintError;

  /**
   * Compile the given text into a set of textures and vertex buffer objects.
   * 
   * @See {@link CompiledText}.
   * 
   * @param text
   *          An array of lines.
   * @throws GLException
   *           Iff an OpenGL error occurs.
   * @throws ConstraintError
   *           Iff any of the following conditions hold:
   *           <ul>
   *           <li><code>text == null</code></li>
   *           <li><code>∃n. text.get(n) == null</code></li>
   *           </ul>
   * @throws TextCacheException
   *           Iff an error occurs whilst caching the input text.
   */

  @Nonnull ArrayList<CompiledText> textCompile(
    final @Nonnull ArrayList<String> text)
    throws GLException,
      ConstraintError,
      TextCacheException;

  /**
   * Return the maximum height in pixels of text rendered with the current
   * renderer.
   */

  int textGetLineHeight();

  /**
   * Return the number of pixels the given text would take when rendered with
   * the current renderer.
   * 
   * @param line
   *          The text.
   * @return The length in pixels.
   * @throws TextCacheException
   *           Iff an error occurs whilst caching the input text.
   * @throws ConstraintError
   *           Iff <code>line == null</code>.
   * @throws GLException
   *           Iff an OpenGL error occurs.
   */

  int textGetWidth(
    final @Nonnull String line)
    throws GLException,
      ConstraintError,
      TextCacheException;
}
