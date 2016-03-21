//Oct.6, 1998   by Gregor v. Bochmann & Yaoping Wang
// revised by G.v. Bochmann, March 7, 2009
import java.io.*;
import java.util.*;

public class Lexer {
	public final int BEGIN = 1;
	public final int END = 2;
	public final int ASSIGN = 3;
	public final int PLUS = 4;
//	public final int MINUS = 5;
	public final int IDENT = 6;
	public final int SEMICOLON = 7;
	public final int EOF = 8;
	
	//Added in for SEG2106 Assignment 3
	
	public final int LEFT_BRACE = 9;
	public final int RIGHT_BRACE = 10;
	public final int LEFT_CURLY = 11;
	public final int RIGHT_CURLY = 12;
	public final int IF = 13;
	public final int EQUAL = 14;
	public final int UNEQUAL = 15;
	
	public String idName;  // identifier name
	public int token;  //holds the next token
	private boolean endOfFile; // whether end of file has been encountered

	private BufferedReader input; // input file buffer
	private char c;  //holds the next character

	private int singleLeftCurly = 0;
	
	public Lexer(String inputFile){
		try{input = new BufferedReader(new FileReader(inputFile));} 
		catch(IOException ee) {ee.printStackTrace();}
	}

	public void start() throws IOException {
		try {nextChar(); getNextToken();}
			catch (EndOfFileEncounteredException e) {token = EOF;}
	}

	public void getNextToken()throws IOException {
		if (endOfFile){token = EOF; return;} 
		String terminalString ="";
		try {
			disposeSpace();
			if(Character.isLetter(c)){ //first character is a letter, get whole alphanumeric string
				terminalString += c;
				nextChar();
				while(Character.isLetterOrDigit(c)){terminalString += c; nextChar();}
				idName = terminalString;
				token = checkKeywords(terminalString);
				} 
			else if (c == '(') {token = LEFT_BRACE; nextChar();}
			else if (c == ')') {token = RIGHT_BRACE; nextChar();}
			else if (c == '{') {token = LEFT_CURLY; nextChar();}
			else if (c == '}') {token = RIGHT_CURLY; nextChar();}
			else if (c == '=') {token = EQUAL; nextChar();}
			
			else if (c == '+') {token = PLUS; nextChar();} 
//			else if (c == '-') {token = MINUS; nextChar();} 
			else if (c == ';') {token = SEMICOLON; nextChar();} 
			else if (c == ':') { 
				//check that next char is '=' to find assignment token ":=" 
				nextChar(); 
				if (c == '=') {token = ASSIGN; nextChar();}
				else 
				{
					System.out.println("lexical error: '=' expected after ':'; skip to end of program");
					skipToEndOfFile();
				}
													} 
			else if (c == '!') { //check that next char is '=' to find the not equal token "!=" 
				nextChar(); 
				if (c == '=') {token = UNEQUAL; nextChar();}
				
				else 
				{
					System.out.println("lexical error: '=' expected after '!'; skip to end of program");
					skipToEndOfFile();
				}
											} 
			else {System.out.println("invalid lexical unit; skip to end of program"); skipToEndOfFile();}
		} catch (EndOfFileEncounteredException e) {
			endOfFile = true;
			token = (terminalString == "")? EOF : checkKeywords(terminalString);
			}
		}
		
	int checkKeywords(String s) {
		if(s.equals("BEGIN")) return(BEGIN);
		else if(s.equals("END")) return(END);
		else if(s.equals("if")) return(IF);
		else return(IDENT);
		}
	
	 void disposeSpace() throws IOException, EndOfFileEncounteredException{
		//get rid of all spaces like \t, \n, and blank space
		while(Character.isWhitespace(c)) {nextChar();}
		}

	 void nextChar() throws IOException, EndOfFileEncounteredException{//get next character
		int i;
		if ((i = input.read()) == -1)throw new EndOfFileEncounteredException();
		c = (char) i;
		System.out.print(c);
		
		if(c == '{')
		{	
			singleLeftCurly++;
		}
		else if(c == '}')
		{
			singleLeftCurly--;
		}
	}
	
	 void skipToEndOfFile() throws IOException, EndOfFileEncounteredException {
		while (true) {nextChar();}
		}
	 
	 void skipToEndOfIfStatement() throws IOException, EndOfFileEncounteredException {
			do {nextChar();} while (c != '}' && singleLeftCurly > 0);
			//get the token after the correct '}'
			nextChar();
			}
}
