//Oct.6, 1998   by Gregor v. Bochmann & Yaoping Wang
// revised by G.v. Bochmann, March 7, 2009
import java.io.*;
import java.util.*;

public class Lexer {
	public final int BEGIN = 1;
	public final int END = 2;
	public final int ASSIGN = 3;
	public final int PLUS = 4;
	public final int MINUS = 5;
	public final int IDENT = 6;
	public final int SEMICOLON = 7;
	public final int EOF = 8;
	public String idName;  // identifier name
	public int token;  //holds the next token
	private boolean endOfFile; // whether end of file has been encountered

	private BufferedReader input; // input file buffer
	private char c;  //holds the next character

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
			else if (c == '+') {token = PLUS; nextChar();} 
			else if (c == '-') {token = MINUS; nextChar();} 
			else if (c == ';') {token = SEMICOLON; nextChar();} 
			else if (c == ':') { //check that next char is '=' to find assignment token ":=" 
						nextChar(); 
						if (c == '=') {token = ASSIGN; nextChar();}
						else {System.out.println("lexical error: '=' expected after ':'; skip to end of program");
						       skipToEndOfFile();}
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
		}
	
	 void skipToEndOfFile() throws IOException, EndOfFileEncounteredException {
		while (true) {nextChar();}
		}
	
}
