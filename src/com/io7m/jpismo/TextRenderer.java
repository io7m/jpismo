package com.io7m.jpismo;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;

/**
 * <p>
 * The common interface supported by all text renderers.
 * </p>
 * <p>
 * Renderers will typically take lines of text as input and will then
 * compile/cache the text into an efficient internal format (usually as some
 * sort of GPU pixel buffer). In order to give developer control over GPU
 * traffic, the interface requires the developer to:
 * <ol>
 * <li>Perform any number of "cache" operations (
 * {@link TextRenderer#textCacheLine(String)},
 * {@link TextRenderer#textCompile(ArrayList)}, etc).</li>
 * <li>Perform an "upload" operation ({@link TextRenderer#textCacheUpload()}).
 * </li>
 * </ol>
 * </p>
 * <p>
 * It is then possible to render text using vertex buffer objects and textures
 * retrieved from the compiled data ({@link CompiledText}).
 * </p>
 * <p>
 * As is most probably obvious, the "cache" operations modify internal caches,
 * whilst "upload" operations bulk upload GPU data. Note that "cache"
 * operations may perform operations on the GPU, but renderers are expected to
 * keep these operations to a minimum.
 * </p>
 * 
 * @see CompiledText
 */

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
   * @see CompiledText
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
