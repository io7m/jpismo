package com.io7m.jpismo.examples.lwjgl30;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.GLInterfaceLWJGL30;
import com.io7m.jlog.Log;
import com.io7m.jpismo.TextCacheException;
import com.io7m.jpismo.VariableTextRenderer;

public final class ShowText implements Runnable
{
  private static final int SCREEN_WIDTH  = 640;
  private static final int SCREEN_HEIGHT = 480;

  public static void main(
    final String args[])
  {
    if (args.length < 1) {
      System.err.println("usage: file.txt");
      System.exit(1);
    }

    try {
      Display.setDisplayMode(new DisplayMode(
        ShowText.SCREEN_WIDTH,
        ShowText.SCREEN_HEIGHT));
      Display.create();
      final ShowText m = new ShowText(args[0]);
      m.run();
    } catch (final LWJGLException e) {
      ErrorBox.showError("LWJGL exception", e);
    } catch (final GLException e) {
      ErrorBox.showError("OpenGL exception", e);
    } catch (final ConstraintError e) {
      ErrorBox.showError("Constraint error", e);
    } catch (final FileNotFoundException e) {
      ErrorBox.showError("File not found error", e);
    } catch (final IOException e) {
      ErrorBox.showError("I/O error", e);
    } catch (final TextCacheException e) {
      ErrorBox.showError("Text cache error", e);
    } finally {
      Display.destroy();
    }
  }

  private final Log                  log;
  private final GLInterface          gl;
  private final VariableTextRenderer renderer;
  private final Font                 font;
  private final Properties           log_properties;
  private final ArrayList<String>    lines;

  public ShowText(
    final String file_name)
    throws ConstraintError,
      GLException,
      FileNotFoundException,
      IOException,
      TextCacheException
  {
    this.log_properties = new Properties();
    this.log_properties.put("com.io7m.jpismo.level", "LOG_DEBUG");
    this.log_properties.put("com.io7m.jpismo.logs.example", "false");

    this.font = new Font("DejaVu Serif", Font.PLAIN, 12);
    this.log = new Log(this.log_properties, "com.io7m.jpismo", "example");
    this.gl = new GLInterfaceLWJGL30(this.log);
    this.renderer = new VariableTextRenderer(this.gl, this.font, this.log);

    for (final String name : GraphicsEnvironment
      .getLocalGraphicsEnvironment()
      .getAvailableFontFamilyNames()) {
      this.log.debug("font " + name);
    }

    this.lines = new ArrayList<String>();
    {
      final BufferedReader reader =
        new BufferedReader(new InputStreamReader(new FileInputStream(
          file_name)));
      for (;;) {
        final String line = reader.readLine();
        if (line == null) {
          break;
        }
        this.renderer.cacheLine(line);
        this.lines.add(line);
      }
      reader.close();
    }

    this.renderer.debugDumpAtlasImages("dump");
    this.renderer.cacheUpload();
    this.renderer.cacheUpload();
  }

  private void render()
    throws GLException,
      ConstraintError,
      TextCacheException
  {
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    GL11.glOrtho(0, 640, 0, 480, 1, 100);

    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();

    GL11.glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

    GL11.glTranslated(0, 0, -1);

    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    double y = 480.0 - this.renderer.getLineHeight();
    for (final String line : this.lines) {
      y -= this.renderer.getLineHeight();
      this.renderer.renderLine(10, y, line);
    }
  }

  @Override public void run()
  {
    try {
      while (Display.isCloseRequested() == false) {
        this.render();
        Display.update();
        Display.sync(60);
      }
    } catch (final GLException e) {
      ErrorBox.showError("OpenGL exception", e);
    } catch (final ConstraintError e) {
      ErrorBox.showError("Constraint error", e);
    } catch (final TextCacheException e) {
      ErrorBox.showError("Text cache error", e);
    } finally {
      Display.destroy();
    }
  }
}
