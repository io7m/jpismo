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

package com.io7m.jpismo.tests.awt_jogl;

import com.io7m.jcanephora.core.JCGLExceptionNonCompliant;
import com.io7m.jcanephora.core.JCGLExceptionUnsupported;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.jogl.JCGLImplementationJOGL;
import com.io7m.jcanephora.jogl.JCGLImplementationJOGLType;
import com.io7m.junreachable.UnreachableCodeException;
import com.jogamp.opengl.DebugGL3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public final class PJOGLTestContexts
{
  private static final JCGLImplementationJOGLType IMPLEMENTATION;
  private static final Map<String, GLOffscreenAutoDrawable> CACHED_CONTEXTS;
  private static final Logger LOG;
  private static final Function<GLContext, GL3>
    GL_CONTEXT_GL3_SUPPLIER;

  static {
    IMPLEMENTATION = JCGLImplementationJOGL.getInstance();
    CACHED_CONTEXTS = new HashMap<>(32);
    LOG = LoggerFactory.getLogger(PJOGLTestContexts.class);
    GL_CONTEXT_GL3_SUPPLIER = c -> {
      final DebugGL3 g = new DebugGL3(c.getGL().getGL3());
      PJOGLTestContexts.LOG.trace("supplying GL3: {}", g);
      return g;
    };
  }

  private PJOGLTestContexts()
  {

  }

  public static JCGLContextType newGL33Context(
    final String name,
    final int depth,
    final int stencil)
  {
    final Function<GLContext, GL3> supplier =
      PJOGLTestContexts.GL_CONTEXT_GL3_SUPPLIER;
    return PJOGLTestContexts.newGL33ContextWithSupplier(
      name,
      supplier,
      depth,
      stencil);
  }

  public static JCGLContextType newGL33ContextWithSupplier(
    final String name,
    final Function<GLContext, GL3> supplier,
    final int depth,
    final int stencil)
  {
    try {
      return PJOGLTestContexts.newGL33ContextWithSupplierAndErrors(
        name, supplier, depth, stencil);
    } catch (final JCGLExceptionUnsupported | JCGLExceptionNonCompliant x) {
      throw new UnreachableCodeException(x);
    }
  }

  public static JCGLContextType newGL33ContextWithSupplierAndErrors(
    final String name,
    final Function<GLContext, GL3> supplier,
    final int depth,
    final int stencil)
    throws JCGLExceptionUnsupported, JCGLExceptionNonCompliant
  {
    final GLContext c = PJOGLTestContexts.newGL33Drawable(name, depth, stencil);
    return PJOGLTestContexts.IMPLEMENTATION.newContextFromWithSupplier(
      c, supplier, name);
  }

  public static GLContext newGL33Drawable(
    final String name,
    final int depth,
    final int stencil)
  {
    PJOGLTestContexts.LOG.debug(
      "creating drawable {} (depth {}) (stencil {})",
      name,
      Integer.valueOf(depth),
      Integer.valueOf(stencil));
    PJOGLTestContexts.destroyCachedDrawableAndRemove(name);

    final GLProfile profile = GLProfile.get(GLProfile.GL3);
    final GLCapabilities cap = new GLCapabilities(profile);
    cap.setFBO(true);
    cap.setDepthBits(depth);
    cap.setStencilBits(stencil);

    final GLDrawableFactory f = GLDrawableFactory.getFactory(profile);
    final GLOffscreenAutoDrawable drawable =
      f.createOffscreenAutoDrawable(null, cap, null, 640, 480);
    drawable.display();

    PJOGLTestContexts.CACHED_CONTEXTS.put(name, drawable);

    final GLContext c = drawable.getContext();
    final int r = c.makeCurrent();
    if (r == GLContext.CONTEXT_NOT_CURRENT) {
      throw new AssertionError("Could not make context current");
    }
    return c;
  }

  private static void destroyCachedDrawableAndRemove(final String name)
  {
    if (PJOGLTestContexts.CACHED_CONTEXTS.containsKey(name)) {
      PJOGLTestContexts.LOG.debug("destroying existing drawable {}", name);
      final GLOffscreenAutoDrawable cached =
        PJOGLTestContexts.CACHED_CONTEXTS.get(name);

      PJOGLTestContexts.destroyDrawable(cached);
      PJOGLTestContexts.CACHED_CONTEXTS.remove(name);
    }
  }

  public static void closeAllContexts()
  {
    final int size = PJOGLTestContexts.CACHED_CONTEXTS.size();
    PJOGLTestContexts.LOG.debug(
      "cleaning up {} contexts", Integer.valueOf(size));

    {
      final Iterator<String> iter =
        PJOGLTestContexts.CACHED_CONTEXTS.keySet().iterator();

      while (iter.hasNext()) {
        final String name = iter.next();
        PJOGLTestContexts.LOG.debug("releasing drawable {}", name);
        Assertive.require(PJOGLTestContexts.CACHED_CONTEXTS.containsKey(name));
        final GLOffscreenAutoDrawable drawable =
          PJOGLTestContexts.CACHED_CONTEXTS.get(name);
        PJOGLTestContexts.releaseDrawable(drawable);
      }
    }

    {
      final Iterator<String> iter =
        PJOGLTestContexts.CACHED_CONTEXTS.keySet().iterator();

      while (iter.hasNext()) {
        final String name = iter.next();
        PJOGLTestContexts.LOG.debug("destroying drawable {}", name);
        Assertive.require(PJOGLTestContexts.CACHED_CONTEXTS.containsKey(name));
        final GLOffscreenAutoDrawable drawable =
          PJOGLTestContexts.CACHED_CONTEXTS.get(name);

        PJOGLTestContexts.destroyDrawable(drawable);
        iter.remove();
      }
    }

    PJOGLTestContexts.LOG.debug(
      "cleaned up {} contexts",
      Integer.valueOf(size));
  }

  private static void destroyDrawable(final GLOffscreenAutoDrawable drawable)
  {
    PJOGLTestContexts.releaseDrawable(drawable);
    drawable.destroy();
  }

  private static void releaseDrawable(final GLOffscreenAutoDrawable drawable)
  {
    final GLContext context = drawable.getContext();
    if (context != null && context.isCurrent()) {
      context.release();
    }
  }
}
