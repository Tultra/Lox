package craftinginterpreters;
import static craftinginterpreters.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	private int start = 0;
	private int current = 0;
	private int line = 1;
	
	Scanner(String source) {
		this.source = source;
	}
	
	List<Token> scanTokens() {

		// System.out.println("0: " + source.charAt(0));
		while (!isAtEnd()) {
			// We are at the beginning of the next lemme;
			start = current;
			scanToken();
		}

		tokens.add(new Token(EOF, "", null, line));
		return tokens;
	}
	
	private boolean isAtEnd() {
		return current >= source.length();
		
	}
	
	private void scanToken() {
		char c = advance();
		// System.out.println(current + ": " + c);
		switch(c) {
		case '(': addToken(LEFT_PAREN); break;
		case ')': addToken(RIGHT_PAREN); break;
		case '{': addToken(LEFT_BRACE); break;
		case '}': addToken(RIGHT_BRACE); break;
		case ',': addToken(COMMA); break;
		case '.': addToken(DOT); break;
		case '-': addToken(MINUS); break;
		case '+': addToken(PLUS); break;
		case ';': addToken(SEMICOLON); break;
		case '*': addToken(STAR); break;
		case '!': 
			addToken(match('=') ? BANG_EQUAL : BANG); break;
		case '=': 
			addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
		case '<': 
			addToken(match('=') ? LESS_EQUAL : LESS); break;
		case '>': 
			addToken(match('=') ? GREATER_EQUAL : GREATER); break;
		case '/': 
			if (match('/')) {
				// A comment goes until the end of line.
				while (peek() != '\n' && !isAtEnd()) advance();
			} else {
				addToken(SLASH);
			} break;
		case ' ': 
		case '\r':
		case '\t':
		//Ignore whitespace
			break;
		case '\n':
			line++;
			break;
			
		case '"': string(); break;

		default:
			if (isDigit(c)) {
				number();
			} else {
				Lox.error(line,  "Unexpected character.");
			}
			break;
		}
	}
	
	private void string() {
		while (peek() != '"' && !isAtEnd()) {
			if (peek() == '\n') line++;
			advance();
		}
		
		if (isAtEnd()) {
			Lox.error(line, "Unterminated String.");
			return;
		}
		advance();
		
		String value = source.substring(start+1, current-1);
		addToken(STRING,value);
	}
	
	private char advance() {
		// char c = source.charAt(current);
		// current++;
		// return c;
		// Logica abaixo tem o mesmo efeito que o codigo acima
		return source.charAt(current++);
	}

	private void addToken(TokenType type) {
		addToken(type,null);
	}
	
	private void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line));
	}
	
	private boolean match(char expected) {
		if (isAtEnd()) return false;
		if (source.charAt(current) != expected) return false;
		current ++;
		return true;
	}
	
	private char peek() {
		if (isAtEnd()) return '\0';
		return source.charAt(current);
	}
	
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private void number() {
		while(isDigit(peek())) advance();
		// Look for fractional parts		
		if (peek() == '.' && isDigit(peekNext())) {
			// consume the '.'.

			advance();

			while (isDigit(peek())) advance();
			
		addToken(NUMBER,
				Double.parseDouble(source.substring(start, current)));
		}
	}
	
	private char peekNext() {
		if (current + 1 >= source.length()) return '\0';
		return source.charAt(current + 1);
	}

	
}
