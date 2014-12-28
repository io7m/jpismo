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

package com.io7m.jpismo.tests.awt_jogl;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import com.io7m.jcanephora.ArrayBufferUsableType;
import com.io7m.jcanephora.BlendFunction;
import com.io7m.jcanephora.ClearSpecification;
import com.io7m.jcanephora.ClearSpecificationBuilderType;
import com.io7m.jcanephora.DepthFunction;
import com.io7m.jcanephora.FragmentShaderType;
import com.io7m.jcanephora.IndexBufferUsableType;
import com.io7m.jcanephora.JCGLException;
import com.io7m.jcanephora.JCGLExceptionProgramCompileError;
import com.io7m.jcanephora.Primitives;
import com.io7m.jcanephora.ProgramType;
import com.io7m.jcanephora.ProjectionMatrix;
import com.io7m.jcanephora.TextureFilterMagnification;
import com.io7m.jcanephora.TextureFilterMinification;
import com.io7m.jcanephora.TextureUnitType;
import com.io7m.jcanephora.VertexShaderType;
import com.io7m.jcanephora.api.JCGLImplementationType;
import com.io7m.jcanephora.api.JCGLInterfaceCommonType;
import com.io7m.jcanephora.batchexec.JCBExecutor;
import com.io7m.jcanephora.batchexec.JCBExecutorProcedureType;
import com.io7m.jcanephora.batchexec.JCBExecutorType;
import com.io7m.jcanephora.batchexec.JCBProgramProcedureType;
import com.io7m.jcanephora.batchexec.JCBProgramType;
import com.io7m.jcanephora.jogl.JCGLImplementationJOGL;
import com.io7m.jcanephora.jogl.JCGLImplementationJOGLBuilderType;
import com.io7m.jcanephora.utilities.ShaderUtilities;
import com.io7m.jlog.Log;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogPolicyAllOn;
import com.io7m.jlog.LogType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpismo.PException;
import com.io7m.jpismo.PTextAntialiasing;
import com.io7m.jpismo.PTextBuilder;
import com.io7m.jpismo.PTextBuilderType;
import com.io7m.jpismo.PTextCompiledType;
import com.io7m.jpismo.PTextRendererType;
import com.io7m.jpismo.PTextUnmeasured;
import com.io7m.jpismo.PTextWrapping;
import com.io7m.jpismo.PTextureType;
import com.io7m.jpismo.PTypefaceLoaderType;
import com.io7m.jpismo.PTypefaceType;
import com.io7m.jpismo.awt.PTextRendererAWTTrivial;
import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorM4F;
import com.io7m.junreachable.UnimplementedCodeException;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

public final class PShowText
{
  public static void main(
    final String[] args)
    throws IOException
  {
    if (args.length != 1) {
      System.err.println("usage: file.txt");
      System.exit(1);
    }

    final File file = new File(NullCheck.notNull(args[0]));
    final BufferedReader reader = new BufferedReader(new FileReader(file));
    final StringBuilder sb = new StringBuilder(1024);
    while (true) {
      final String line = reader.readLine();
      if (line == null) {
        break;
      }
      sb.append(line);
      sb.append("\n");
    }
    reader.close();
    final String text = NullCheck.notNull(sb.toString());

    final LogType log =
      Log.newLog(LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG), "main");

    final GLProfile profile = GLProfile.get(GLProfile.GL3);
    final GLCapabilities caps = new GLCapabilities(profile);
    final GLWindow w = GLWindow.create(caps);
    w.setTitle(PShowText.class.getCanonicalName());
    w.setSize(640, 480);

    final MatrixM4x4F projection = new MatrixM4x4F();
    final MatrixM4x4F view = new MatrixM4x4F();
    final MatrixM4x4F model = new MatrixM4x4F();
    final MatrixM4x4F modelview = new MatrixM4x4F();
    final VectorM4F color_v4 = new VectorM4F();

    w.addGLEventListener(new GLEventListener() {
      private JCGLImplementationType gi;
      private PTextRendererType      tr;
      private PTextCompiledType      tc;
      private ClearSpecification     cs;
      private boolean                done;
      private float                  hue;
      private JCBExecutorType        program_rgba_np;
      private JCBExecutorType        program_rg_np;
      private JCBExecutorType        program_r;
      private PTypefaceLoaderType    tl;

      @Override public void reshape(
        final GLAutoDrawable drawable,
        final int x,
        final int y,
        final int width,
        final int height)
      {
        ProjectionMatrix.makeOrthographicProjection(
          projection,
          0.0f,
          width,
          0.0f,
          height,
          1.0f,
          100.0f);
      }

      @Override public void init(
        final GLAutoDrawable drawable)
      {
        if (this.done) {
          return;
        }

        try {
          final GLContext context = NullCheck.notNull(drawable.getContext());

          final JCGLImplementationJOGLBuilderType gib =
            JCGLImplementationJOGL.newBuilder();
          gib.setStateCaching(true);
          this.gi = gib.build(context, log);
          this.tl = new PTypefaceLoaderType() {
            @Override public PTypefaceType loadTrueType(
              final String name)
              throws IOException,
                PException
            {
              // TODO Auto-generated method stub
              throw new UnimplementedCodeException();
            }
          };
          this.tr = PTextRendererAWTTrivial.newTextRenderer(this.gi, this.tl);

          final PTypefaceType face =
            this.tr.getTypefaceDefaults().getSansSerif();

          final PTextBuilderType tb = PTextBuilder.newBuilder();
          tb.setTextureType(PTextureType.TEXTURE_OPAQUE_R);
          tb.setAntialiasingMode(PTextAntialiasing.TEXT_ANTIALIASING_FAST);
          tb
            .setMinificationFilter(TextureFilterMinification.TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST);
          tb
            .setMagnificationFilter(TextureFilterMagnification.TEXTURE_FILTER_NEAREST);
          final PTextUnmeasured t =
            tb.buildText(
              face,
              11.0f,
              text,
              PTextWrapping.TEXT_WRAPPING_COLUMNS,
              80.0f);

          this.tc = this.tr.compileUnmeasuredText(t);

          System.out.println("Width: " + this.tc.textGetWidth());
          System.out.println("Height: " + this.tc.textGetHeight());

          final ClearSpecificationBuilderType csb =
            ClearSpecification.newBuilder();
          csb.enableColorBufferClear4f(0.2f, 0.2f, 0.2f, 1.0f);
          csb.enableDepthBufferClear(1.0f);
          this.cs = csb.build();

          final JCGLInterfaceCommonType gc = this.gi.getGLCommon();
          this.program_rgba_np =
            PShowText.makeProgram(
              log,
              gc,
              "uv.v",
              "uv_rgba_non_premult.f",
              "uv_rgba_non_premult");
          this.program_rg_np =
            PShowText.makeProgram(
              log,
              gc,
              "uv.v",
              "uv_rg_non_premult.f",
              "uv_rg_non_premult");
          this.program_r =
            PShowText.makeProgram(log, gc, "uv.v", "uv_r.f", "uv_r");

        } catch (final JCGLExceptionProgramCompileError e) {
          e.printStackTrace();
          this.done = true;
        } catch (final JCGLException e) {
          e.printStackTrace();
          this.done = true;
        } catch (final IOException e) {
          e.printStackTrace();
          this.done = true;
        }
      }

      @Override public void dispose(
        final GLAutoDrawable drawable)
      {
        // Nothing
      }

      @Override public void display(
        final GLAutoDrawable drawable)
      {
        if (this.done) {
          return;
        }

        final JCGLInterfaceCommonType gc = this.gi.getGLCommon();
        gc.clear(this.cs);
        gc.cullingDisable();
        gc.depthBufferTestEnable(DepthFunction.DEPTH_LESS_THAN_OR_EQUAL);
        gc.depthBufferWriteEnable();

        gc.blendingEnable(
          BlendFunction.BLEND_SOURCE_ALPHA,
          BlendFunction.BLEND_ONE_MINUS_SOURCE_ALPHA);

        MatrixM4x4F.setIdentity(view);
        MatrixM4x4F.setIdentity(model);
        MatrixM4x4F.makeTranslation3FInto(
          new VectorI3F(16.0f, 464.0f, -1.0f),
          model);

        MatrixM4x4F.multiply(view, model, modelview);

        final IndexBufferUsableType i = this.tc.textGetIndexBuffer(0);
        final ArrayBufferUsableType a = this.tc.textGetArrayBuffer(0);
        gc.arrayBufferBind(a);

        JCBExecutorType exec = null;
        switch (this.tc.textGetActualTextureType()) {
          case TEXTURE_TRANSLUCENT_RGBA_NON_PREMULTIPLIED:
          {
            exec = this.program_rgba_np;
            break;
          }
          case TEXTURE_TRANSLUCENT_RG_NON_PREMULTIPLIED:
          {
            exec = this.program_rg_np;
            break;
          }
          case TEXTURE_OPAQUE_R:
          {
            exec = this.program_r;
            break;
          }
        }

        this.hue += 0.01f;
        final float sat = (float) Math.sin(this.hue * 2.0f);
        final Color rgb = new Color(Color.HSBtoRGB(this.hue, sat, 1.0f));
        color_v4.set4F(
          rgb.getRed() / 256.0f,
          rgb.getGreen() / 256.0f,
          rgb.getBlue() / 256.0f,
          1.0f);

        final List<TextureUnitType> units = gc.textureGetUnits();
        gc.texture2DStaticBind(units.get(0), this.tc.textGetTexture(0));

        NullCheck.notNull(exec).execRun(
          new JCBExecutorProcedureType<JCGLException>() {
            @Override public void call(
              final JCBProgramType program)
            {
              assert modelview != null;
              assert projection != null;
              program.programUniformPutMatrix4x4f("m_modelview", modelview);
              program.programUniformPutMatrix4x4f("m_projection", projection);
              program.programUniformPutVector4f("f_color", color_v4);
              program.programUniformPutTextureUnit("f_texture", units.get(0));
              program.programAttributeBind(
                "v_position",
                a.arrayGetAttribute("v_position"));
              program.programAttributeBind(
                "v_uv",
                a.arrayGetAttribute("v_uv"));
              program
                .programExecute(new JCBProgramProcedureType<JCGLException>() {
                  @Override public void call()
                  {
                    gc.drawElements(Primitives.PRIMITIVE_TRIANGLES, i);
                  }
                });
            }
          });

        gc.arrayBufferUnbind();
      }
    });
    w.setVisible(true);
    w.setDefaultCloseOperation(WindowClosingMode.DISPOSE_ON_CLOSE);
    w.addWindowListener(new WindowAdapter() {
      @Override public void windowDestroyed(
        final WindowEvent e)
      {
        System.exit(0);
      }
    });

    final Animator anim = new Animator(w);
    anim.start();
  }

  private static JCBExecutorType makeProgram(
    final LogType log,
    final JCGLInterfaceCommonType gc,
    final String vertex,
    final String fragment,
    final String program)
    throws IOException
  {
    InputStream vs = null;
    InputStream fs = null;
    try {
      vs = PShowText.class.getResourceAsStream(vertex);
      final List<String> v_lines = ShaderUtilities.readLines(vs);
      final VertexShaderType pv = gc.vertexShaderCompile(vertex, v_lines);
      fs = PShowText.class.getResourceAsStream(fragment);
      final List<String> f_lines = ShaderUtilities.readLines(fs);
      final FragmentShaderType pf =
        gc.fragmentShaderCompile(fragment, f_lines);
      final ProgramType pp = gc.programCreateCommon(program, pv, pf);
      return JCBExecutor.newExecutorWithoutDeclarations(gc, pp, log);
    } finally {
      if (vs != null) {
        vs.close();
      }
      if (fs != null) {
        fs.close();
      }
    }
  }
}
