/*
 * Copyright © 2016 <code@io7m.com> http://io7m.com
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

import com.io7m.jcanephora.core.JCGLExceptionDeleted;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.api.JCGLContextUsableType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jpismo.PTextAntialiasing;
import com.io7m.jpismo.PTextBuilder;
import com.io7m.jpismo.PTextBuilderType;
import com.io7m.jpismo.PTextCompiledType;
import com.io7m.jpismo.PTextMeasuredType;
import com.io7m.jpismo.PTextRendererType;
import com.io7m.jpismo.PTextUnmeasuredType;
import com.io7m.jpismo.PTextWrapping;
import com.io7m.jpismo.PTextureType;
import com.io7m.jpismo.PTypefaceDefaultsType;
import com.io7m.jpismo.PTypefaceLoaderType;
import com.io7m.jpismo.PTypefaceType;
import org.junit.Assert;
import org.junit.Test;

// CHECKSTYLE_JAVADOC:OFF

public abstract class PTextRendererContract extends PAbstractTestContract
{
  @Test
  public final void testFontDefault()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getDefault();
    Assert.assertNotNull(f);
  }

  @Test
  public final void testFontMonospace()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getMonospace();
    Assert.assertNotNull(f);
  }

  @Test
  public final void testFontSansSerif()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSansSerif();
    Assert.assertNotNull(f);
  }

  @Test
  public final void testFontSerif()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();
    Assert.assertNotNull(f);
  }

  @Test
  public final void testMeasure0()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setMagnificationFilter(
      JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb.setMinificationFilter(
      JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasuredType ut =
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

  @Test
  public final void testCompileMeasured0()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = g.contextGetGL33();
    final JCGLTextureUnitAllocatorType uc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g33.getTextures().textureGetUnits());

    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setTextureType(
      PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED);
    tb.setMagnificationFilter(
      JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb.setMinificationFilter(
      JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasuredType ut =
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

    final PTextCompiledType ct =
      r.compileMeasuredText(g33, uc.getRootContext(), mt);
    System.out.println(ct);

    final int ew = (int) Math.ceil(mt.textGetWidth());
    final int eh = (int) Math.ceil(mt.textGetHeight());

    Assert.assertEquals(ew, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(eh, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.isDeleted());
    ct.textDelete(g33);
    Assert.assertTrue(ct.isDeleted());
  }

  @Test
  public final void testCompileMeasured1()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = g.contextGetGL33();
    final JCGLTextureUnitAllocatorType uc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g33.getTextures().textureGetUnits());

    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setTextureType(PTextureType.TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED);
    tb.setMagnificationFilter(
      JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb.setMinificationFilter(
      JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasuredType ut =
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

    final PTextCompiledType ct =
      r.compileMeasuredText(g33, uc.getRootContext(), mt);
    System.out.println(ct);

    final int ew = (int) Math.ceil(mt.textGetWidth());
    final int eh = (int) Math.ceil(mt.textGetHeight());

    Assert.assertEquals(ew, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(eh, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.isDeleted());
    ct.textDelete(g33);
    Assert.assertTrue(ct.isDeleted());
  }

  @Test
  public final void testCompileMeasured2()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = g.contextGetGL33();
    final JCGLTextureUnitAllocatorType uc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g33.getTextures().textureGetUnits());

    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setTextureType(PTextureType.TEXTURE_OPAQUE_R);
    tb.setMagnificationFilter(
      JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb.setMinificationFilter(
      JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasuredType ut =
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

    final PTextCompiledType ct =
      r.compileMeasuredText(g33, uc.getRootContext(), mt);
    System.out.println(ct);

    final int ew = (int) Math.ceil(mt.textGetWidth());
    final int eh = (int) Math.ceil(mt.textGetHeight());

    Assert.assertEquals(ew, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(eh, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.isDeleted());
    ct.textDelete(g33);
    Assert.assertTrue(ct.isDeleted());
  }

  @Test
  public final void testCompileMeasured3()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = g.contextGetGL33();
    final JCGLTextureUnitAllocatorType uc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g33.getTextures().textureGetUnits());

    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setTextureType(
      PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED);
    tb.setMagnificationFilter(
      JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb.setMinificationFilter(
      JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_NONE);
    final PTextUnmeasuredType ut =
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

    final PTextCompiledType ct =
      r.compileMeasuredText(g33, uc.getRootContext(), mt);
    System.out.println(ct);

    final int ew = (int) Math.ceil(mt.textGetWidth());
    final int eh = (int) Math.ceil(mt.textGetHeight());

    Assert.assertEquals(ew, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(eh, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.isDeleted());
    ct.textDelete(g33);
    Assert.assertTrue(ct.isDeleted());
  }

  @Test
  public final void testCompileUnmeasured0()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = g.contextGetGL33();
    final JCGLTextureUnitAllocatorType uc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g33.getTextures().textureGetUnits());

    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setTextureType(
      PTextureType.TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED);
    tb.setMagnificationFilter(
      JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb.setMinificationFilter(
      JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasuredType ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextCompiledType ct =
      r.compileUnmeasuredText(g33, uc.getRootContext(), ut);
    System.out.println(ct);

    Assert.assertEquals(76, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(14, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.isDeleted());
    ct.textDelete(g33);
    Assert.assertTrue(ct.isDeleted());
  }

  @Test
  public final void testCompileUnmeasured1()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = g.contextGetGL33();
    final JCGLTextureUnitAllocatorType uc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g33.getTextures().textureGetUnits());

    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setTextureType(PTextureType.TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED);
    tb.setMagnificationFilter(
      JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb.setMinificationFilter(
      JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasuredType ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextCompiledType ct =
      r.compileUnmeasuredText(g33, uc.getRootContext(), ut);
    System.out.println(ct);

    Assert.assertEquals(76, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(14, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.isDeleted());
    ct.textDelete(g33);
    Assert.assertTrue(ct.isDeleted());
  }

  @Test
  public final void testCompileUnmeasured2()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = g.contextGetGL33();
    final JCGLTextureUnitAllocatorType uc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g33.getTextures().textureGetUnits());

    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setTextureType(PTextureType.TEXTURE_OPAQUE_R);
    tb.setMagnificationFilter(
      JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb.setMinificationFilter(
      JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasuredType ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextCompiledType ct =
      r.compileUnmeasuredText(g33, uc.getRootContext(), ut);
    System.out.println(ct);

    Assert.assertEquals(76, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(14, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.isDeleted());
    ct.textDelete(g33);
    Assert.assertTrue(ct.isDeleted());
  }

  @Test
  public final void testCompileUnmeasuredHard0()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = g.contextGetGL33();
    final JCGLTextureUnitAllocatorType uc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g33.getTextures().textureGetUnits());

    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setMagnificationFilter(
      JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb.setMinificationFilter(
      JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasuredType ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextCompiledType ct =
      r.compileUnmeasuredTextWithHardBounds(g33, uc.getRootContext(), ut, 8, 8);
    System.out.println(ct);

    Assert.assertEquals(8, ct.textGetWidth(), 0.0f);
    Assert.assertEquals(8, ct.textGetHeight(), 0.0f);

    Assert.assertFalse(ct.isDeleted());
    ct.textDelete(g33);
    Assert.assertTrue(ct.isDeleted());
  }

  @Test(expected = JCGLExceptionDeleted.class)
  public final void testCompileDeleted0()
  {
    final JCGLContextUsableType g = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = g.contextGetGL33();
    final JCGLTextureUnitAllocatorType uc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g33.getTextures().textureGetUnits());

    final PTypefaceLoaderType tl = this.getTypefaceLoader();
    final PTextRendererType r = this.getTextRenderer(g, tl);
    final PTypefaceDefaultsType fl = r.getTypefaceDefaults();
    final PTypefaceType f = fl.getSerif();

    final PTextBuilderType tb = PTextBuilder.newBuilder();
    tb.setMagnificationFilter(
      JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    tb.setMinificationFilter(
      JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
    tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
    final PTextUnmeasuredType ut =
      tb.buildText(
        f,
        12.0f,
        "Hello World!",
        PTextWrapping.TEXT_WRAPPING_COLUMNS,
        80.0f);

    final PTextCompiledType ct =
      r.compileUnmeasuredText(g33, uc.getRootContext(), ut);

    Assert.assertFalse(ct.isDeleted());
    ct.textDelete(g33);
    Assert.assertTrue(ct.isDeleted());
    ct.textDelete(g33);
  }
}
