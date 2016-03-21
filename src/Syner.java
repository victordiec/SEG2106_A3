// Syner.java Oct.6, 1998   By Gregor v. Bochmann & Yaoping Wang
// revised by G.v. Bochmann, March 7, 2009, and March 4, 2013
import java.io.*;
import java.util.*;

public class Syner {
	private Lexer lex;
	private Hashtable symbolTable;
	
	public Syner(String inputFile) {
		lex = new Lexer(inputFile);
		symbolTable = new Hashtable();
		symbolTable.put("zero", new Integer(0) );
		symbolTable.put("one", new Integer(1) );
		symbolTable.put("ten", new Integer(10) );
	}

	public void startAnalysis() throws IOException {
		lex.start(); //start lexical analyser to get a token
		parseProgram(); //call parseProgram() to process the analysis
		//after "END" token, there should be the EOF token
		if(lex.token == lex.EOF) {System.out.println("\n"+"analysis complete, no syntax error");}
		else {errorMessage("after END - more tokens before EOF");}			
	}

	public void parseProgram() throws IOException {
		if(lex.token == lex.BEGIN){
			while (true){
				lex.getNextToken();
				parseStatement();
				if(lex.token != lex.SEMICOLON){
					break;
				}
			}
			if (lex.token == lex.END) {lex.getNextToken(); }
			else {errorMessage("END token expected!"); }
		}
		else {
			errorMessage("BEGIN token expected!");
		}
	}
	
	public void parseStatement() throws IOException {
		if (lex.token == lex.IDENT) {
			String var = lex.idName;
			lex.getNextToken(); 
			if (lex.token == lex.ASSIGN) {
				lex.getNextToken(); 
				int v = parseExpression();
				
				//Save the variable and its associated value into the table
				symbolTable.put(var, new Integer(v) );
				System.out.println("\n"+var+" assign "+v);
			} else {
				errorMessage("assignment symbol expected");
			}
		} else {
			errorMessage("identifier expected at the begining of a statement");
		}
	}
	
	public int parseExpression() throws IOException {
		if(lex.token == lex.IDENT) {
			int value = ((Integer)symbolTable.get(lex.idName)).intValue();
			lex.getNextToken();
			
			//Since we only removing the MINUS operator
//			if(lex.token == lex.PLUS || lex.token == lex.MINUS) {
//				boolean opIsPlus = (lex.token == lex.PLUS); // just to remember what the operation is
			if(lex.token==lex.PLUS){
				
				lex.getNextToken();
				if(lex.token == lex.IDENT) {					
					int tmp = ((Integer)symbolTable.get(lex.idName)).intValue();
					
					value = value + tmp;
//					value = (opIsPlus)? (value + tmp) : (value - tmp) ; 
					
					lex.getNextToken();
					return(value); }
				else {errorMessage("identifier expected at the end of the expression");
				return(0);}
			}
			else {return(value);}
		}
		else {errorMessage("identifier expected at the beginning of a expression");
		return(0);}
	}

	public void errorMessage(String s) throws IOException {
		System.out.println(s);
		// skip to the end of the program
		while (lex.token != lex.EOF) {lex.getNextToken();}
	}
}

