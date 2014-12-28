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

package com.io7m.jpismo.android;

import android.graphics.Typeface;

import com.io7m.jcanephora.api.JCGLImplementationType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpismo.PTextCompiledType;
import com.io7m.jpismo.PTextMeasuredType;
import com.io7m.jpismo.PTextRendererType;
import com.io7m.jpismo.PTextUnmeasured;
import com.io7m.jpismo.PTypefaceDefaultsType;
import com.io7m.jpismo.PTypefaceLoaderType;
import com.io7m.jpismo.PTypefaceType;
import com.io7m.junreachable.UnimplementedCodeException;

final class PTextRendererAndroidActual implements
  PTextRendererType,
  PTypefaceDefaultsType
{
  private static final PTypefaceType   TYPEFACE_DEFAULT;
  private static final PTypefaceType   TYPEFACE_MONOSPACED;
  private static final PTypefaceType   TYPEFACE_SANS;
  private static final PTypefaceType   TYPEFACE_SERIF;

  static {
    TYPEFACE_DEFAULT = PTextRendererAndroidActual.newTypefaceDefault();
    TYPEFACE_MONOSPACED = PTextRendererAndroidActual.newTypefaceMonospaced();
    TYPEFACE_SANS = PTextRendererAndroidActual.newTypefaceSansSerif();
    TYPEFACE_SERIF = PTextRendererAndroidActual.newTypefaceSerif();
  }

  private static PTypefaceType newTypefaceDefault()
  {
    return new PTypefaceAndroid("Default", Typeface.DEFAULT);
  }
  private static PTypefaceType newTypefaceMonospaced()
  {
    return new PTypefaceAndroid("Monospaced", Typeface.MONOSPACE);
  }

  private static PTypefaceType newTypefaceSansSerif()
  {
    return new PTypefaceAndroid("SansSerif", Typeface.SANS_SERIF);
  }

  private static PTypefaceType newTypefaceSerif()
  {
    return new PTypefaceAndroid("Serif", Typeface.SERIF);
  }

  private final JCGLImplementationType gi;

  private final PTypefaceLoaderType    loader;

  PTextRendererAndroidActual(
    final JCGLImplementationType in_gi,
    final PTypefaceLoaderType in_loader)
  {
    this.gi = NullCheck.notNull(in_gi, "OpenGL implementation");
    this.loader = NullCheck.notNull(in_loader, "Typeface loader");
  }

  @Override public PTextCompiledType compileMeasuredText(
    final PTextMeasuredType text)
  {
    // TODO Auto-generated method stub
    throw new UnimplementedCodeException();
  }

  @Override public PTextCompiledType compileUnmeasuredText(
    final PTextUnmeasured text)
  {
    // TODO Auto-generated method stub
    throw new UnimplementedCodeException();
  }

  @Override public PTextCompiledType compileUnmeasuredTextWithHardBounds(
    final PTextUnmeasured text,
    final int width,
    final int height)
  {
    // TODO Auto-generated method stub
    throw new UnimplementedCodeException();
  }

  @Override public PTypefaceType getDefault()
  {
    return PTextRendererAndroidActual.TYPEFACE_DEFAULT;
  }

  @Override public PTypefaceType getMonospace()
  {
    return PTextRendererAndroidActual.TYPEFACE_MONOSPACED;
  }

  @Override public PTypefaceType getSansSerif()
  {
    return PTextRendererAndroidActual.TYPEFACE_SANS;
  }

  @Override public PTypefaceType getSerif()
  {
    return PTextRendererAndroidActual.TYPEFACE_SERIF;
  }

  @Override public PTypefaceDefaultsType getTypefaceDefaults()
  {
    return this;
  }

  @Override public PTypefaceLoaderType getTypefaceLoader()
  {
    return this.loader;
  }

  @Override public PTextMeasuredType measureText(
    final PTextUnmeasured text)
  {
    // TODO Auto-generated method stub
    throw new UnimplementedCodeException();
  }
}
