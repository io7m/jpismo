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

package com.io7m.jpismo;

/**
 * The type of exceptions raised by the package.
 */

public abstract class PException extends RuntimeException
{
  private static final long serialVersionUID;

  static {
    serialVersionUID = 3066957660374566801L;
  }

  /**
   * Construct an exception.
   */

  public PException()
  {
    super();
  }

  /**
   * Construct an exception.
   *
   * @param message
   *          The message
   */

  public PException(
    final String message)
  {
    super(message);
  }

  /**
   * Construct an exception.
   *
   * @param message
   *          The message
   * @param cause
   *          The cause
   */

  public PException(
    final String message,
    final Throwable cause)
  {
    super(message, cause);
  }

  /**
   * Construct an exception.
   *
   * @param cause
   *          The cause
   */

  public PException(
    final Throwable cause)
  {
    super(cause);
  }

  // CHECKSTYLE:OFF
  @Override protected final void finalize()
    // CHECKSTYLE:ON
    throws Throwable
  {
    // Empty finalizer to prevent attacks on the implementation.
  }
}
