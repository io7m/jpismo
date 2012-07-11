package com.io7m.jpismo.examples.jogl30;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.fixedfunc.GLPointerFunc;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.ArrayBufferDescriptor;
import com.io7m.jcanephora.BlendFunction;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.GLInterfaceJOGL30;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jcanephora.Primitives;
import com.io7m.jcanephora.Texture2DRGBAStatic;
import com.io7m.jcanephora.TextureUnit;
import com.io7m.jlog.Log;
import com.io7m.jpismo.CompiledPage;
import com.io7m.jpismo.CompiledText;
import com.io7m.jpismo.TextCacheException;
import com.io7m.jpismo.examples.Example;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;

public final class ExampleRunnerJOGL30 implements Runnable
{
  private static final int  SCREEN_WIDTH  = 640;
  private static final int  SCREEN_HEIGHT = 480;

  private final Log         log;
  private final GLInterface gl;
  private final String      file;
  private final Example     example;
  private final Properties  log_properties;
  private final GLWindow    window;
  private final GLContext   context;
  private final GLDrawable  drawable;

  private static GLWindow makeWindow(
    final String name)
  {
    final GLProfile pro = GLProfile.getDefault();
    final GLCapabilities caps = new GLCapabilities(pro);
    final GLWindow window = GLWindow.create(caps);

    window.setSize(
      ExampleRunnerJOGL30.SCREEN_WIDTH,
      ExampleRunnerJOGL30.SCREEN_HEIGHT);
    window.setVisible(true);
    window.setTitle(name);
    window.addWindowListener(new WindowAdapter() {
      @Override public void windowDestroyNotify(
        final WindowEvent e)
      {
        System.exit(0);
      }
    });

    return window;
  }

  ExampleRunnerJOGL30(
    final @Nonnull Example ex,
    final @Nonnull String file)
    throws ConstraintError,
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

    this.window =
      ExampleRunnerJOGL30.makeWindow("Example: "
        + this.example.getName()
        + " - "
        + file);
    this.context = this.window.getContext();
    this.drawable = this.context.getGLDrawable();

    this.gl = new GLInterfaceJOGL30(this.context, this.log);
    this.example.initialize(this.log, this.gl, this.file);
  }

  private static void render(
    final GLContext context,
    final GLInterface gl,
    final CompiledText text)
    throws GLException,
      ConstraintError
  {
    context.makeCurrent();

    final GL g = context.getGL();
    final GL2 g2 = g.getGL2();

    /**
     * Set up projection and modelview matrices. Note that the text renderer
     * uses (0,0) as the top left corner of rendered text, so the projection
     * matrix sets (0,0) as the top left corner. The text renderers also
     * render at Z = 0, so the modelview matrix is translated along negative Z
     * by one unit.
     */

    g2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
    g2.glLoadIdentity();
    g2.glOrtho(0, 640, 480, 0, 1, 100);

    g2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    g2.glLoadIdentity();
    g2.glTranslated(0, 0, -1);

    g2.glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
    g2.glClear(GL.GL_COLOR_BUFFER_BIT);

    /**
     * Enable texturing and blending. Text is rendered as white text on a
     * transparent background.
     */

    g2.glEnable(GL.GL_TEXTURE_2D);

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

      g2.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
      g2.glVertexPointer(2, GL.GL_FLOAT, stride, po_offset);
      g2.glEnableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
      g2.glTexCoordPointer(2, GL.GL_FLOAT, stride, uv_offset);

      gl.drawElements(Primitives.PRIMITIVE_TRIANGLES, ib);
    }
  }

  @Override public void run()
  {
    try {
      while (true) {
        ExampleRunnerJOGL30.render(
          this.context,
          this.gl,
          this.example.getCompiledText());
        this.drawable.swapBuffers();
        Thread.sleep(1000 / 60);
      }
    } catch (final GLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (final ConstraintError e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (final InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
