package org.jgll.benchmark;

import java.io.IOException;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.JavaCharacterLevel;
import org.jgll.grammar.JavaNaturalCharacterLevel;
import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;
import org.jgll.parser.datadependent.java.Java;
import org.jgll.util.Input;

public class BenchmarkJava {
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/Java/jdk1.7.0_60-b19";
//	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(JavaCharacterLevel.grammar));
	private static Nonterminal start = Start.from(Nonterminal.withName("CompilationUnit"));
	
//	private static Grammar grammar2 = new LayoutWeaver().transform(new OperatorPrecedence(JavaNaturalCharacterLevel.precedencePatterns(), JavaNaturalCharacterLevel.exceptPatterns()).transform(new EBNFToBNF().transform(JavaNaturalCharacterLevel.grammar)));
	
	private static Grammar grammar = new DesugarPrecedenceAndAssociativity().transform(new LayoutWeaver().transform(new EBNFToBNF().transform(Java.grammar)));
	
	public static void main(String[] args) throws IOException {
		IguanaBenchmark.builder(grammar, start).addFile("/Users/aliafroozeh/test.java").setRunCount(10).build().run();
//		IguanaBenchmark.builder(grammar, start).addDirectory(sourceDir, "java", true).build().run();
	}
}