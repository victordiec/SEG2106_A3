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
		try
		{
			lex.start(); //start lexical analyser to get a token
			parseProgram(); //call parseProgram() to process the analysis
			//after "END" token, there should be the EOF token
			if(lex.token == lex.EOF) {System.out.println("\n"+"analysis complete, no syntax error");}
			else {errorMessage("after END - more tokens before EOF");}			
		}
		catch(UndefinedVariableException e)
		{
			System.out.println("Could not finish compilation because:" + e.getMessage());
		}
	}

	public void parseProgram() throws IOException, UndefinedVariableException {

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
	
	public void parseStatement() throws IOException, UndefinedVariableException {
		
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
		}
		//Added for SEG2106 A3
		//Should check for the if statements
		else if(lex.token == lex.IF)
		{
			lex.getNextToken();
			//Find the beginning of the boolean expression
			if(lex.token == lex.LEFT_BRACE)
			{
				lex.getNextToken();
				boolean v = parseBooleanExpression();
				
				//This should be the end of the boolean expression
				if(lex.token == lex.RIGHT_BRACE)
				{
					if(v == true)
					{
						//Execute the statement list inside
						lex.getNextToken();
						if(lex.token == lex.LEFT_CURLY)
						{
							lex.getNextToken();
							parseStatement();
							
							if(lex.token == lex.RIGHT_CURLY)
								lex.getNextToken();
							else
								errorMessage("} expected at the end of a statement");
						}
					}
					else
					{
						//Look for first '}'
						try {
							lex.skipToEndOfIfStatement();
							lex.getNextToken();
						} catch (EndOfFileEncounteredException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else
				{
					errorMessage(") expected at the end of a statement");
				}
				
			}
		}
		else {
			errorMessage("identifier expected at the begining of a statement");
		}
	}
	
	public int parseExpression() throws IOException, UndefinedVariableException{
		if(lex.token == lex.IDENT) {
			
			if(symbolTable.get(lex.idName) == null)
			{
				System.out.println("\nSyntax Error:\t" + lex.idName + " has not been defined");
				throw new UndefinedVariableException(lex.idName + " has not been defined");
			}
			
			int value = ((Integer)symbolTable.get(lex.idName)).intValue();
			String var1 = lex.idName + "";

			lex.getNextToken();
			
			//Since we only removing the MINUS operator
//			if(lex.token == lex.PLUS || lex.token == lex.MINUS) {
//				boolean opIsPlus = (lex.token == lex.PLUS); // just to remember what the operation is
			if(lex.token==lex.PLUS){
				
				lex.getNextToken();
				if(lex.token == lex.IDENT) {
					
					if(symbolTable.get(lex.idName) == null)
					{
						System.out.println("\nSyntax Error:\t" + lex.idName + " has not been defined");
						throw new UndefinedVariableException(lex.idName + " has not been defined");
					}
					
					int tmp = ((Integer)symbolTable.get(lex.idName)).intValue();
					String var2 = lex.idName + "";
					
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
	
	//Added in for SEG2106
	public boolean parseBooleanExpression() throws IOException, UndefinedVariableException
	{
		//Should not execute unless we know the boolean expression is satisfied
		boolean result = false;
		
		if(lex.token == lex.IDENT) {
			
			if(symbolTable.get(lex.idName) == null)
			{
				System.out.println("\nSyntax Error:\t" + lex.idName + " has not been defined");
				throw new UndefinedVariableException(lex.idName + " has not been defined");
			}
			
			int value = ((Integer)symbolTable.get(lex.idName)).intValue();
			lex.getNextToken();

			
			if(lex.token==lex.EQUAL || lex.token==lex.UNEQUAL){
				
				boolean opIsEqual = lex.token==lex.EQUAL;
				
				lex.getNextToken();
				if(lex.token == lex.IDENT) {	
					
					if(symbolTable.get(lex.idName) == null)
					{
						System.out.println("\nSyntax Error:\t" + lex.idName + " has not been defined");
						throw new UndefinedVariableException(lex.idName + " has not been defined");
					}
					
					int tmp = ((Integer)symbolTable.get(lex.idName)).intValue();
					lex.getNextToken();
					
					result = (opIsEqual) ? value==tmp : value != tmp;
					
					return(result); 
					}
				else {errorMessage("identifier expected at the end of the expression");
				return(result);}
			}
			else {errorMessage("Either \'=\' or \'!=\' expected to complete the boolean expression");
				return(result);}
		}
		else {errorMessage("identifier expected at the beginning of a expression");
		return(result);}
	}

	public void errorMessage(String s) throws IOException {
		System.out.println(s);
		// skip to the end of the program
		while (lex.token != lex.EOF) {lex.getNextToken();}
	}
}

