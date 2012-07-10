package com.io7m.jpismo.examples;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

final class ExampleUtil
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
}
