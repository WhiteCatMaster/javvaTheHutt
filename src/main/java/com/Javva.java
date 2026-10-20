package com.javva;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Javva {
  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: javva [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);
    int rythm = 0;

    for (;;) {

      if (rythm < 3) {
        System.out.print("taaaan > ");
        rythm++;
      } else {
        System.out.print("tatatataaaan > ");
        rythm = 0;
      }

      System.out.print("taaaan > ");
      String line = reader.readLine();
      if (line == null)
        break;
      run(line);
    }
  }

  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    // for now just print tokens.
    for (Token token : tokens) {
      System.out.println(token);
    }
  }
}
