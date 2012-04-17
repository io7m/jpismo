package com.io7m.jpismo;

import javax.annotation.Nonnull;

import com.io7m.jrpack.PackResult.PackResultCode;

public final class TextCacheException extends Throwable
{
  private static final long             serialVersionUID =
                                                           8409721208499794416L;
  private final @Nonnull PackResultCode result_code;
  private final @Nonnull String         word;

  public TextCacheException(
    final @Nonnull PackResultCode result,
    final char c)
  {
    super(result + " - " + c);
    this.result_code = result;
    this.word = "" + c;
  }

  public TextCacheException(
    final @Nonnull PackResultCode result,
    final @Nonnull String word)
  {
    super(result + " - " + word);
    this.result_code = result;
    this.word = word;
  }

  public @Nonnull PackResultCode getResultCode()
  {
    return this.result_code;
  }

  public @Nonnull String getWord()
  {
    return this.word;
  }
}
