package com.io7m.jpismo.examples;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jlog.Log;
import com.io7m.jpismo.CompiledText;
import com.io7m.jpismo.TextCacheException;
import com.io7m.jpismo.TextRendererTrivial;

public final class ShowTextTrivial implements Example
{
  private CompiledText        compiled_text;
  private Font                font;
  private ArrayList<String>   lines;
  private TextRendererTrivial renderer;

  @Override public void close(
    final @Nonnull Log log,
    final @Nonnull GLInterface gl)
    throws GLException,
      ConstraintError
  {
    this.compiled_text.resourceDelete(gl);
    this.lines.clear();
  }

  @Override public CompiledText getCompiledText()
  {
    return this.compiled_text;
  }

  @Override public void initialize(
    final @Nonnull Log log,
    final @Nonnull GLInterface gl,
    final @Nonnull String file)
    throws ConstraintError,
      IOException,
      GLException,
      TextCacheException
  {
    /**
     * Initialize text renderer with OpenGL interface and font.
     */

    this.font = new Font("Serif", Font.PLAIN, 12);
    this.renderer = new TextRendererTrivial(gl, this.font, log);

    /**
     * Read lines from file, compile, and upload.
     */

    this.lines = ExampleUtil.readLines(file);
    this.compiled_text = this.renderer.textCompile(this.lines);
    this.renderer.textCacheUpload();
  }

  @Override public String getName()
  {
    return "ShowTextTrivial";
  }
}
