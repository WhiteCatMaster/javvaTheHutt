package com.javva;

class Token {
  final TokenType type;
  final String lexeme;
  final Object literal;
  final int line;
  final Language language;

  Token(TokenType type, String lexeme, Object literal, int line, Language language) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
    this.language = language;
  }

  public String toString() {
    return type + " " + lexeme + " " + literal + ", " + language;
  }
}
