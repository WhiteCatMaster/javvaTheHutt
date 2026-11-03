package com.javva;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.javva.TokenType.*;
import static com.javva.Language.*;

class Scanner {
  private static final Map<String, Language> LANG_CODES = new HashMap<>();
  static {
    LANG_CODES.put("us", us);
    LANG_CODES.put("es", es);
    LANG_CODES.put("kr", kr);
  }

  private static Map<String, TokenType> keywords;
  private static Language language;

  static {
    String langProp = System.getProperty("javva.language", "us");
    setLanguage(LANG_CODES.getOrDefault(langProp, us));
  }

  private static void setLanguage(Language lang) {
    language = lang;
    HashMap<String, TokenType> kw = new HashMap<>();
    switch (lang) {
      case us:
        kw.put("and", AND);
        kw.put("class", CLASS);
        kw.put("else", ELSE);
        kw.put("false", FALSE);
        kw.put("for", FOR);
        kw.put("fun", FUN);
        kw.put("if", IF);
        kw.put("nil", NIL);
        kw.put("or", OR);
        kw.put("print", PRINT);
        kw.put("return", RETURN);
        kw.put("super", SUPER);
        kw.put("this", THIS);
        kw.put("true", TRUE);
        kw.put("var", VAR);
        kw.put("while", WHILE);
        kw.put("nand", NAND);
        kw.put("xand", XAND);
        kw.put("xnand", XNAND);
        kw.put("nor", NOR);
        kw.put("xor", XOR);
        kw.put("xnor", XNOR);
        kw.put("switch", SWITCH);
        kw.put("case", CASE);
        kw.put("jumpturn", JUMPTURN);
        break;
      case es:
        kw.put("y", AND);
        kw.put("clase", CLASS);
        kw.put("sino", ELSE);
        kw.put("falso", FALSE);
        kw.put("para", FOR);
        kw.put("funcion", FUN);
        kw.put("si", IF);
        kw.put("nulo", NIL);
        kw.put("o", OR);
        kw.put("imprimir", PRINT);
        kw.put("devolver", RETURN);
        kw.put("super", SUPER);
        kw.put("este", THIS);
        kw.put("verdadero", TRUE);
        kw.put("variable", VAR);
        kw.put("mientras", WHILE);
        kw.put("ny", NAND);
        kw.put("xy", XAND);
        kw.put("xny", XNAND);
        kw.put("no", NOR);
        kw.put("xo", XOR);
        kw.put("xno", XNOR);
        kw.put("interruptor", SWITCH);
        kw.put("caso", CASE);
        kw.put("saltaturnos", JUMPTURN);
        break;
      case kr:
        kw.put("그리고", AND);
        kw.put("클래스", CLASS);
        kw.put("아니면", ELSE);
        kw.put("거짓", FALSE);
        kw.put("반복", FOR);
        kw.put("함수", FUN);
        kw.put("만약", IF);
        kw.put("없음", NIL);
        kw.put("또는", OR);
        kw.put("출력", PRINT);
        kw.put("반환", RETURN);
        kw.put("상위", SUPER);
        kw.put("이것", THIS);
        kw.put("참", TRUE);
        kw.put("변수", VAR);
        kw.put("동안", WHILE);
        kw.put("비그리고", NAND);
        kw.put("배그리고", XAND);
        kw.put("배비그리고", XNAND);
        kw.put("비또는", NOR);
        kw.put("배또는", XOR);
        kw.put("배비또는", XNOR);
        kw.put("스위치", SWITCH);
        kw.put("경우", CASE);
        kw.put("건너뛰기", JUMPTURN);
        break;
    }
    keywords = kw;
  }

  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

  Scanner(String source) {
    this.source = source;
  }

  List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line, null));
    return tokens;
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(':
        addToken(LEFT_PAREN);
        break;
      case ')':
        addToken(RIGHT_PAREN);
        break;
      case '{':
        addToken(LEFT_BRACE);
        break;
      case '}':
        addToken(RIGHT_BRACE);
        break;
      case ',':
        addToken(COMMA);
        break;
      case '.':
        addToken(DOT);
        break;
      case '-':
        addToken(MINUS);
        break;
      case '+':
        addToken(PLUS);
        break;
      case ';':
        addToken(SEMICOLON);
        break;
      case '*':
        addToken(STAR);
        break;
      case '#':
        addToken(HASHTAG);
        break;
      case '$':
        addToken(DOLLAR);
        break;
      case '%':
        addToken(PERCENTAGE);
        break;
      case '\\':
        addToken(BACKSLASH);
        break;
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
      case '/':
        addToken(match('/') ? SLASH_SLASH : SLASH);
        break;
      case '|':
        addToken(match('|') ? VLINE_VLINE : VLINE);
        break;
      case '?':
        addToken(UPPERINTERROGATION);
        break;
      // Spanish inverted punctuation
      case '¡':
        addToken(LOWEREXCLAMATION, Language.es);
        break;
      case '¿':
        addToken(LOWERINTERROGATION, Language.es);
        break;
      case ' ':
      case '\r':
      case '\t':
        break;
      case '\n':
        line++;
        break;
      case '"':
        string();
        break;
      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          Javva.error(line, "Unexpected character.");
        }
        break;
    }
  }

  private void identifier() {
    while (isAlphaNumeric(peek()))
      advance();
    String text = source.substring(start, current);

    // Language switch directive: writing "us", "es", or "kr" changes the active language
    if (LANG_CODES.containsKey(text)) {
      setLanguage(LANG_CODES.get(text));
      return;
    }

    TokenType type = keywords.getOrDefault(text, IDENTIFIER);
    addToken(type);
  }

  private void number() {
    while (isDigit(peek()))
      advance();
    if (peek() == '.' && isDigit(peekNext())) {
      advance();
      while (isDigit(peek()))
        advance();
    }
    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n')
        line++;
      advance();
    }
    if (isAtEnd()) {
      Javva.error(line, "Unterminated string.");
      return;
    }
    advance();
    addToken(STRING, source.substring(start + 1, current - 1));
  }

  private boolean match(char expected) {
    if (isAtEnd())
      return false;
    if (source.charAt(current) != expected)
      return false;
    current++;
    return true;
  }

  private char peek() {
    if (isAtEnd())
      return '\0';
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length())
      return '\0';
    return source.charAt(current + 1);
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_'
        || (c >= '가' && c <= '힣')  // Hangul syllables
        || (c >= 'ᄀ' && c <= 'ᇿ')  // Hangul Jamo
        || (c >= '㄰' && c <= '㆏'); // Hangul Compatibility Jamo
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    return source.charAt(current++);
  }

  private void addToken(TokenType type) {
    addToken(type, null, null);
  }

  private void addToken(TokenType type, Object literal) {
    addToken(type, literal, null);
  }

  private void addToken(TokenType type, Language language) {
    addToken(type, null, language);
  }

  private void addToken(TokenType type, Object literal, Language language) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line, language));
  }
}
