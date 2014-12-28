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

package com.io7m.jpismo.tests;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jcanephora.JCGLExceptionDeleted;
import com.io7m.jcanephora.TextureFilterMagnification;
import com.io7m.jcanephora.TextureFilterMinification;
import com.io7m.jcanephora.api.JCGLImplementationType;
import com.io7m.jpismo.PTextAntialiasing;
import com.io7m.jpismo.PTextBuilder;
import com.io7m.jpismo.PTextBuilderType;
import com.io7m.jpismo.PTextCompiledType;
import com.io7m.jpismo.PTextMeasuredType;
import com.io7m.jpismo.PTextRendererType;
import com.io7m.jpismo.PTextUnmeasured;
import com.io7m.jpismo.PTextWrapping;
import com.io7m.jpismo.PTextureType;
import com.io7m.jpismo.PTypefaceDefaultsType;
import com.io7m.jpismo.PTypefaceLoaderType;
import com.io7m.jpismo.PTypefaceType;

// CHECKSTYLE_JAVADOC:OFF

public abstract class PTextRendererContract extends PAbstractTestContract
{
  @Test public final void testFontDefault()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getDefault();
    Assert.assertNotNull(f);
  }

  @Test public final void testFontMonospace()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getMonospace();
    Assert.assertNotNull(f);
  }

  @Test public final void testFontSansSerif()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSansSerif();
    Assert.assertNotNull(f);
  }

  @Test public final void testFontSerif()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();
    Assert.assertNotNull(f);
  }

  @Test public final void testMeasure0()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb
      .setMagnificationFilter(TextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb
      .setMinificationFilter(TextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasured ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextMeasuredType mt = r.measureText(ut);
    System.out.println(mt);

    Assert.assertTrue(mt.textGetHeight() > 13.0f);
    Assert.assertTrue(mt.textGetHeight() < 14.0f);
    Assert.assertTrue(mt.textGetWidth() > 75.0f);
    Assert.assertTrue(mt.textGetWidth() < 76.0f);
    Assert.assertEquals(mt.textGetUnmeasured(), ut);
  }

  @Test public final void testCompileMeasured0()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb
      .setTextureType(PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED);
    tb
      .setMagnificationFilter(TextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb
      .setMinificationFilter(TextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasured ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextMeasuredType mt = r.measureText(ut);
    System.out.println(mt);

    Assert.assertTrue(mt.textGetHeight() > 13.0f);
    Assert.assertTrue(mt.textGetHeight() < 14.0f);
    Assert.assertTrue(mt.textGetWidth() > 75.0f);
    Assert.assertTrue(mt.textGetWidth() < 76.0f);
    Assert.assertEquals(mt.textGetUnmeasured(), ut);

    final PTextCompiledType ct = r.compileMeasuredText(mt);
    System.out.println(ct);

    final int ew = (int) Math.ceil(mt.textGetWidth());
    final int eh = (int) Math.ceil(mt.textGetHeight());

    Assert.assertEquals(ew, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(eh, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.resourceIsDeleted());
    ct.textDelete(g);
    Assert.assertTrue(ct.resourceIsDeleted());
  }

  @Test public final void testCompileMeasured1()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setTextureType(PTextureType.TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED);
    tb
      .setMagnificationFilter(TextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb
      .setMinificationFilter(TextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasured ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextMeasuredType mt = r.measureText(ut);
    System.out.println(mt);

    Assert.assertTrue(mt.textGetHeight() > 13.0f);
    Assert.assertTrue(mt.textGetHeight() < 14.0f);
    Assert.assertTrue(mt.textGetWidth() > 75.0f);
    Assert.assertTrue(mt.textGetWidth() < 76.0f);
    Assert.assertEquals(mt.textGetUnmeasured(), ut);

    final PTextCompiledType ct = r.compileMeasuredText(mt);
    System.out.println(ct);

    final int ew = (int) Math.ceil(mt.textGetWidth());
    final int eh = (int) Math.ceil(mt.textGetHeight());

    Assert.assertEquals(ew, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(eh, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.resourceIsDeleted());
    ct.textDelete(g);
    Assert.assertTrue(ct.resourceIsDeleted());
  }

  @Test public final void testCompileMeasured2()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setTextureType(PTextureType.TEXTURE_OPAQUE_R);
    tb
      .setMagnificationFilter(TextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb
      .setMinificationFilter(TextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasured ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextMeasuredType mt = r.measureText(ut);
    System.out.println(mt);

    Assert.assertTrue(mt.textGetHeight() > 13.0f);
    Assert.assertTrue(mt.textGetHeight() < 14.0f);
    Assert.assertTrue(mt.textGetWidth() > 75.0f);
    Assert.assertTrue(mt.textGetWidth() < 76.0f);
    Assert.assertEquals(mt.textGetUnmeasured(), ut);

    final PTextCompiledType ct = r.compileMeasuredText(mt);
    System.out.println(ct);

    final int ew = (int) Math.ceil(mt.textGetWidth());
    final int eh = (int) Math.ceil(mt.textGetHeight());

    Assert.assertEquals(ew, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(eh, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.resourceIsDeleted());
    ct.textDelete(g);
    Assert.assertTrue(ct.resourceIsDeleted());
  }

  @Test public final void testCompileMeasured3()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb
      .setTextureType(PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED);
    tb
      .setMagnificationFilter(TextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb
      .setMinificationFilter(TextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_NONE);
    final PTextUnmeasured ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextMeasuredType mt = r.measureText(ut);
    System.out.println(mt);

    Assert.assertTrue(mt.textGetHeight() > 13.0f);
    Assert.assertTrue(mt.textGetHeight() < 14.0f);
    Assert.assertTrue(mt.textGetWidth() > 75.0f);
    Assert.assertTrue(mt.textGetWidth() < 76.0f);
    Assert.assertEquals(mt.textGetUnmeasured(), ut);

    final PTextCompiledType ct = r.compileMeasuredText(mt);
    System.out.println(ct);

    final int ew = (int) Math.ceil(mt.textGetWidth());
    final int eh = (int) Math.ceil(mt.textGetHeight());

    Assert.assertEquals(ew, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(eh, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.resourceIsDeleted());
    ct.textDelete(g);
    Assert.assertTrue(ct.resourceIsDeleted());
  }

  @Test public final void testCompileUnmeasured0()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb
      .setTextureType(PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED);
    tb
      .setMagnificationFilter(TextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb
      .setMinificationFilter(TextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasured ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextCompiledType ct = r.compileUnmeasuredText(ut);
    System.out.println(ct);

    Assert.assertEquals(76, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(14, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.resourceIsDeleted());
    ct.textDelete(g);
    Assert.assertTrue(ct.resourceIsDeleted());
  }

  @Test public final void testCompileUnmeasured1()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setTextureType(PTextureType.TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED);
    tb
      .setMagnificationFilter(TextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb
      .setMinificationFilter(TextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasured ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextCompiledType ct = r.compileUnmeasuredText(ut);
    System.out.println(ct);

    Assert.assertEquals(76, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(14, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.resourceIsDeleted());
    ct.textDelete(g);
    Assert.assertTrue(ct.resourceIsDeleted());
  }

  @Test public final void testCompileUnmeasured2()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setTextureType(PTextureType.TEXTURE_OPAQUE_R);
    tb
      .setMagnificationFilter(TextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb
      .setMinificationFilter(TextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasured ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextCompiledType ct = r.compileUnmeasuredText(ut);
    System.out.println(ct);

    Assert.assertEquals(76, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(14, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.resourceIsDeleted());
    ct.textDelete(g);
    Assert.assertTrue(ct.resourceIsDeleted());
  }

  @Test public final void testCompileUnmeasuredHard0()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb
      .setMagnificationFilter(TextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb
      .setMinificationFilter(TextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasured ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextCompiledType ct =
      r.compileUnmeasuredTextWithHardBounds(ut, 8, 8);
    System.out.println(ct);

    Assert.assertEquals(8, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(8, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.resourceIsDeleted());
    ct.textDelete(g);
    Assert.assertTrue(ct.resourceIsDeleted());
  }

  @Test(expected = JCGLExceptionDeleted.class) public final
    void
    testCompileDeleted0()
  {
    final JCGLImplementationType g = this.getGLImplementation();
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb
      .setMagnificationFilter(TextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb
      .setMinificationFilter(TextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasured ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextCompiledType ct = r.compileUnmeasuredText(ut);

    Assert.assertFalse(ct.resourceIsDeleted());
    ct.textDelete(g);
    Assert.assertTrue(ct.resourceIsDeleted());
    ct.textDelete(g);
  }
}
