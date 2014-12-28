package com.io7m.jpismo.tests.awt_jogl;

import java.io.IOException;

import com.io7m.jpismo.PException;
import com.io7m.jpismo.PTypefaceLoaderType;
import com.io7m.jpismo.PTypefaceType;
import com.io7m.junreachable.UnimplementedCodeException;

public final class PTypefaceLoaderAWT implements PTypefaceLoaderType
{
  @Override public PTypefaceType loadTrueType(
    final String name)
    throws IOException,
      PException
  {
    // TODO Auto-generated method stub
    throw new UnimplementedCodeException();
  }
}
