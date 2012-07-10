package com.io7m.jpismo.examples.lwjgl30;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.Nonnull;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.ArrayBufferDescriptor;
import com.io7m.jcanephora.BlendFunction;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.GLInterfaceLWJGL30;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jcanephora.Primitives;
import com.io7m.jcanephora.Texture2DRGBAStatic;
import com.io7m.jcanephora.TextureUnit;
import com.io7m.jlog.Log;
import com.io7m.jpismo.CompiledPage;
import com.io7m.jpismo.CompiledText;
import com.io7m.jpismo.TextCacheException;
import com.io7m.jpismo.examples.Example;

public final class ExampleRunnerLWJGL30 implements Runnable
{
  private static final int  SCREEN_WIDTH  = 640;
  private static final int  SCREEN_HEIGHT = 480;

  private final Log         log;
  private final GLInterface gl;
  private final String      file;
  private final Example     example;
  private final Properties  log_properties;

  void initDisplay()
    throws LWJGLException
  {
    Display.setDisplayMode(new DisplayMode(
      ExampleRunnerLWJGL30.SCREEN_WIDTH,
      ExampleRunnerLWJGL30.SCREEN_HEIGHT));
    Display.create();
  }

  ExampleRunnerLWJGL30(
    final @Nonnull Example ex,
    final @Nonnull String file)
    throws ConstraintError,
      LWJGLException,
      GLException,
      IOException,
      TextCacheException
  {
    this.log_properties = new Properties();
    this.log_properties.put("com.io7m.jpismo.level", "LOG_DEBUG");
    this.log_properties.put("com.io7m.jpismo.logs.example", "true");
    this.log = new Log(this.log_properties, "com.io7m.jpismo", "example");

    this.file = Constraints.constrainNotNull(file, "File");
    this.example = Constraints.constrainNotNull(ex, "Example");

    this.initDisplay();

    this.gl = new GLInterfaceLWJGL30(this.log);
    this.example.initialize(this.log, this.gl, this.file);
  }

  private static void render(
    final GLInterface gl,
    final CompiledText text)
    throws GLException,
      ConstraintError
  {
    /**
     * Set up projection and modelview matrices. Note that the text renderer
     * uses (0,0) as the top left corner of rendered text, so the projection
     * matrix sets (0,0) as the top left corner. The text renderers also
     * render at Z = 0, so the modelview matrix is translated along negative Z
     * by one unit.
     */

    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    GL11.glOrtho(0, 640, 480, 0, 1, 100);

    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();
    GL11.glTranslated(0, 0, -1);

    GL11.glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

    /**
     * Enable texturing and blending. Text is rendered as white text on a
     * transparent background.
     */

    GL11.glEnable(GL11.GL_TEXTURE_2D);

    gl.blendingEnable(
      BlendFunction.BLEND_ONE,
      BlendFunction.BLEND_ONE_MINUS_SOURCE_ALPHA);

    /**
     * Text renderers render text as a set of overlaid compiled "pages". The
     * actual contents of the pages are renderer-specific and the user doesn't
     * need to know how they are constructed.
     * 
     * Each page holds a vertex buffer object, an index buffer, and a texture.
     * Rendering therefore consists of binding the vertex buffer object and
     * texture, and then calling glDrawElements() (jcanephora is used here for
     * extra safety).
     */

    final TextureUnit[] units = gl.textureGetUnits();

    final int max = text.maxPages();
    for (int index = 0; index < max; ++index) {
      final CompiledPage ctext = text.getPage(index);
      final Texture2DRGBAStatic texture = ctext.getTexture();
      final ArrayBuffer ab = ctext.getVertexBuffer();
      final ArrayBufferDescriptor d = ab.getDescriptor();
      final IndexBuffer ib = ctext.getIndexBuffer();

      gl.texture2DRGBAStaticBind(units[0], texture);
      gl.arrayBufferBind(ab);

      final int stride = d.getSize();
      final int uv_offset = d.getAttributeOffset("uv");
      final int po_offset = d.getAttributeOffset("position");

      GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
      GL11.glVertexPointer(2, GL11.GL_FLOAT, stride, po_offset);
      GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
      GL11.glTexCoordPointer(2, GL11.GL_FLOAT, stride, uv_offset);

      gl.drawElements(Primitives.PRIMITIVE_TRIANGLES, ib);
    }
  }

  @Override public void run()
  {
    try {
      while (Display.isCloseRequested() == false) {
        ExampleRunnerLWJGL30.render(this.gl, this.example.getCompiledText());
        Display.update();
        Display.sync(60);
      }
    } catch (final GLException e) {
      ErrorBox.showError("OpenGL exception", e);
    } catch (final ConstraintError e) {
      ErrorBox.showError("Constraint error", e);
    } finally {
      try {
        this.example.close(this.log, this.gl);
      } catch (final GLException e) {
        e.printStackTrace();
      } catch (final ConstraintError e) {
        e.printStackTrace();
      }
      Display.destroy();
    }
  }
}
