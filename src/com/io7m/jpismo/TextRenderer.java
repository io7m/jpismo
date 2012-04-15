package com.io7m.jpismo;

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
   *           Iff <code>line</code> is null.
   * @throws GLException
   *           Iff an OpenGL error occurs.
   */

  void cacheLine(
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

  void cacheUpload()
    throws GLException,
      ConstraintError;

  /**
   * Return the maximum height in pixels of text rendered with the current
   * renderer.
   */

  int getLineHeight();
}
