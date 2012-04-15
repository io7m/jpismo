package com.io7m.jpismo.examples.lwjgl30;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.annotation.Nonnull;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class ErrorBox
{
  public static void showError(
    final @Nonnull String title,
    final @Nonnull Throwable e)
  {
    final StringWriter writer = new StringWriter();
    e.printStackTrace(new PrintWriter(writer));

    final JTextArea text = new JTextArea();
    text.setEditable(false);
    text.setText(writer.toString());

    final JScrollPane pane = new JScrollPane(text);
    pane.setPreferredSize(new Dimension(600, 320));

    final JLabel label = new JLabel(title);
    label.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

    final BorderLayout layout = new BorderLayout();
    final JPanel panel = new JPanel(layout);
    panel.add(label, BorderLayout.NORTH);
    panel.add(pane, BorderLayout.SOUTH);

    e.printStackTrace();

    JOptionPane.showMessageDialog(
      null,
      panel,
      title,
      JOptionPane.ERROR_MESSAGE);
  }
}
