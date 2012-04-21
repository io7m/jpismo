package com.io7m.jpismo.examples.lwjgl30;

import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.GLInterfaceLWJGL30;
import com.io7m.jlog.Log;
import com.io7m.jpismo.CompiledText;
import com.io7m.jpismo.TextCacheException;
import com.io7m.jpismo.TextRendererTrivial;

final class ShowTextTrivial implements Runnable
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
        ShowTextTrivial.SCREEN_WIDTH,
        ShowTextTrivial.SCREEN_HEIGHT));
      Display.create();
      final ShowTextTrivial m = new ShowTextTrivial(args[0]);
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

  private final Log                 log;
  private final GLInterface         gl;
  private final TextRendererTrivial renderer;
  private final Font                font;
  private final Properties          log_properties;
  private final ArrayList<String>   lines;
  private final CompiledText        compiled_text;

  public ShowTextTrivial(
    final String file_name)
    throws ConstraintError,
      GLException,
      FileNotFoundException,
      IOException,
      TextCacheException
  {
    /**
     * Initialize logging interface.
     */

    this.log_properties = new Properties();
    this.log_properties.put("com.io7m.jpismo.level", "LOG_DEBUG");
    this.log_properties.put("com.io7m.jpismo.logs.example", "true");
    this.log = new Log(this.log_properties, "com.io7m.jpismo", "example");

    /**
     * Initialize text renderer with OpenGL interface and font.
     */

    this.font = new Font("Serif", Font.PLAIN, 12);
    this.gl = new GLInterfaceLWJGL30(this.log);
    this.renderer = new TextRendererTrivial(this.gl, this.font, this.log);

    /**
     * Read lines from file, compile, and upload.
     */

    this.lines = Show.readLines(file_name);
    this.compiled_text = this.renderer.textCompile(this.lines);
    this.renderer.textCacheUpload();
  }

  private void render()
    throws GLException,
      ConstraintError
  {
    Show.render(this.gl, this.compiled_text);
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
    } finally {
      Display.destroy();
    }
  }
}
