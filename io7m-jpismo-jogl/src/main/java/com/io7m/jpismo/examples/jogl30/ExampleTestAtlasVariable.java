package com.io7m.jpismo.examples.jogl30;

import java.io.IOException;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jpismo.TextCacheException;
import com.io7m.jpismo.examples.ShowTextAtlasVariable;

public final class ExampleTestAtlasVariable
{
  public static void main(
    final String args[])
    throws GLException,
      IOException,
      ConstraintError,
      TextCacheException
  {
    if (args.length < 1) {
      System.err.println("usage: file.txt");
      System.exit(1);
    }

    final ShowTextAtlasVariable ex = new ShowTextAtlasVariable();
    final ExampleRunnerJOGL30 er = new ExampleRunnerJOGL30(ex, args[0]);
    er.run();
  }
}
