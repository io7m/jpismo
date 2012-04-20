package com.io7m.jpismo.examples.lwjgl30;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.ArrayBufferDescriptor;
import com.io7m.jcanephora.BlendFunction;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jcanephora.Primitives;
import com.io7m.jcanephora.Texture2DRGBAStatic;
import com.io7m.jcanephora.TextureUnit;
import com.io7m.jpismo.CompiledText;

final class Show
{
  static ArrayList<String> readLines(
    final String file_name)
    throws IOException
  {
    final ArrayList<String> lines = new ArrayList<String>();
    final BufferedReader reader =
      new BufferedReader(
        new InputStreamReader(new FileInputStream(file_name)));

    for (;;) {
      final String line = reader.readLine();
      if (line == null) {
        break;
      }
      lines.add(line);
    }
    reader.close();

    return lines;
  }

  static void render(
    final GLInterface gl,
    final ArrayList<CompiledText> compiled_pages)

    throws GLException,
      ConstraintError
  {
    /**
     * Set up projection and modelview matrices. Note that the text renderer
     * uses (0,0) as the top left corner of rendered text, so the modelview
     * matrix is translated up the Y axis by 480 units.
     */

    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    GL11.glOrtho(0, 640, 0, 480, 1, 100);

    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();
    GL11.glTranslated(0, 480, -1);

    GL11.glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

    /**
     * Enable texturing and blending. Text is rendered as white text on a
     * transparent background.
     */

    GL11.glEnable(GL11.GL_TEXTURE_2D);

    gl.enableBlending(
      BlendFunction.BLEND_SOURCE_ALPHA,
      BlendFunction.BLEND_ONE_MINUS_SOURCE_ALPHA);

    /**
     * Text renderers render text as a set of overlaid compiled pages. The
     * actual contents of the pages are renderer-specific and the user doesn't
     * need to know how they are constructed.
     * 
     * Each page holds a vertex buffer object, an index buffer, and a texture.
     * Rendering therefore consists of binding the vertex buffer object and
     * texture, and then calling glDrawElements() (jcanephora is used here for
     * extra safety).
     */

    final TextureUnit[] units = gl.getTextureUnits();

    for (final CompiledText ctext : compiled_pages) {
      final Texture2DRGBAStatic texture = ctext.getTexture();
      final ArrayBuffer ab = ctext.getVertexBuffer();
      final ArrayBufferDescriptor d = ab.getDescriptor();
      final IndexBuffer ib = ctext.getIndexBuffer();

      gl.bindTexture2DRGBAStatic(units[0], texture);
      gl.bindArrayBuffer(ab);

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
}
