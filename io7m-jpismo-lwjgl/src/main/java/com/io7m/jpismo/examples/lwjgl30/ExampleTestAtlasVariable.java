package com.io7m.jpismo.examples.lwjgl30;

import java.io.IOException;

import org.lwjgl.LWJGLException;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jpismo.TextCacheException;
import com.io7m.jpismo.examples.ShowTextAtlasVariable;

public final class ExampleTestAtlasVariable
{
  public static void main(
    final String args[])
    throws LWJGLException,
      GLException,
      IOException,
      ConstraintError,
      TextCacheException
  {
    if (args.length < 1) {
      System.err.println("usage: file.txt");
      System.exit(1);
    }

    final ShowTextAtlasVariable ex = new ShowTextAtlasVariable();
    final ExampleRunnerLWJGL30 er = new ExampleRunnerLWJGL30(ex, args[0]);
    er.run();
  }
}
