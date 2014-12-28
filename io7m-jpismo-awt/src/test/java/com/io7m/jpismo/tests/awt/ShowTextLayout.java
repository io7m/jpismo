package com.io7m.jpismo.tests.awt;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.io7m.jnull.NullCheck;

final class ShowTextLayout
{
  static final class Canvas extends JPanel
  {
    private static final long serialVersionUID;

    static {
      serialVersionUID = -4409410715626686503L;
    }

    private final String      text;
    private Font              text_font;
    private boolean           written;
    private final String[]    names;
    private int               name_index;

    Canvas()
      throws IOException
    {
      final InputStream is =
        NullCheck.notNull(this.getClass().getResourceAsStream(
          "lorem-long.txt"));
      final BufferedReader r = new BufferedReader(new InputStreamReader(is));
      final StringBuilder sb = new StringBuilder(1024);

      while (true) {
        final String line = r.readLine();
        if (line == null) {
          break;
        }
        sb.append(line + "\n");
      }

      r.close();

      final GraphicsEnvironment ge =
        GraphicsEnvironment.getLocalGraphicsEnvironment();
      this.names = ge.getAvailableFontFamilyNames();
      this.name_index = -1;
      this.text = sb.toString();

      final TimerTask tt = new TimerTask() {
        @Override public void run()
        {
          Canvas.this.repaint();
        }
      };
      final Timer timer = new Timer();
      timer.scheduleAtFixedRate(tt, 0, 1000L);
    }

    @Override public void paint(
      final Graphics g)
    {
      final String name;
      if (this.name_index == -1) {
        name = "Dialog";
        this.name_index = 0;
      } else {
        this.name_index = (this.name_index + 1) % this.names.length;
        name = this.names[this.name_index];
      }

      final String typeface = name + " 11";
      System.out.println("type: " + typeface);
      this.text_font = Font.decode(typeface);

      final BufferedImage image =
        new BufferedImage(1024, 1024, BufferedImage.TYPE_4BYTE_ABGR_PRE);

      {
        final Graphics2D ig = (Graphics2D) image.getGraphics();

        ig.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
        ig.setRenderingHint(
          RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        final FontRenderContext frc = ig.getFontRenderContext();
        final AttributedString as = new AttributedString(this.text);
        as.addAttribute(TextAttribute.FONT, this.text_font);
        as.addAttribute(
          TextAttribute.FOREGROUND,
          Color.WHITE,
          0,
          this.text.length() - 1);
        final AttributedCharacterIterator as_iterator = as.getIterator();
        final LineBreakMeasurer measurer =
          new LineBreakMeasurer(as_iterator, frc);

        final int width_limit = 300;
        final float tx = 0;
        float ty = 0;

        while (measurer.getPosition() < as_iterator.getEndIndex()) {
          final int current = measurer.getPosition();
          final int next = measurer.nextOffset(width_limit);
          int limit = next;
          final int index = this.text.indexOf("\n", current + 1);
          if ((next > (index - current)) && (index != -1)) {
            limit = index - current;
          }
          final TextLayout layout =
            measurer.nextLayout(
              width_limit,
              measurer.getPosition() + limit,
              false);

          ty += layout.getAscent();
          layout.draw(ig, tx, ty);
          ty += layout.getDescent() + layout.getLeading();
        }
      }

      if (this.written == false) {
        try {
          ImageIO.write(image, "PNG", new File("/tmp/image.png"));
        } catch (final IOException e) {
          e.printStackTrace();
        }
        this.written = true;
      }

      g.setColor(Color.DARK_GRAY);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      g.drawImage(image, 0, 0, null);
    }
  }

  static final class ShowTextLayoutWindow extends JFrame
  {
    private static final long serialVersionUID;

    static {
      serialVersionUID = -6209812719368914270L;
    }

    private final Canvas      canvas;

    ShowTextLayoutWindow()
      throws IOException
    {
      final Container content = this.getContentPane();
      this.canvas = new Canvas();
      content.add(this.canvas);
    }
  }

  public static void main(
    final String[] args)
  {
    {
      final GraphicsEnvironment ge =
        GraphicsEnvironment.getLocalGraphicsEnvironment();
      final String[] names = ge.getAvailableFontFamilyNames();
      for (int i = 0; i < names.length; i++) {
        System.out.println(names[i]);
      }
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override public void run()
      {
        try {
          final ShowTextLayoutWindow w = new ShowTextLayoutWindow();
          w.setMinimumSize(new Dimension(640, 480));
          w.pack();
          w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          w.setVisible(true);
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
