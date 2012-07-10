package com.io7m.jpismo.examples;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jlog.Log;
import com.io7m.jpismo.CompiledText;
import com.io7m.jpismo.TextCacheException;

public interface Example
{
  public void initialize(
    final @Nonnull Log log,
    final @Nonnull GLInterface gl,
    final @Nonnull String file)
    throws GLException,
      ConstraintError,
      IOException,
      TextCacheException;

  public @Nonnull CompiledText getCompiledText();

  public void close(
    final @Nonnull Log log,
    final @Nonnull GLInterface gl)
    throws GLException,
      ConstraintError;
}
