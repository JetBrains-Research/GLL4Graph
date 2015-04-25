package org.iguana.parser.datadependent;

import static org.iguana.datadependent.ast.AST.greaterEq;
import static org.iguana.datadependent.ast.AST.integer;
import static org.iguana.datadependent.ast.AST.var;
import static org.iguana.grammar.condition.DataDependentCondition.predicate;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.iguana.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * S ::= E(0,0)
 * 
 * E(l,r) ::=[5 >= r] E(5,r) z // propagate r down
 *         | [4 >= l] x E(l,4) // propagate l down
 *         | [3 >= r] E(3,0) w
 *         | [2 >= l] y E(0,0)
 *         | a
 *
 * tricky inputs: see Test8 and: (x (y a)) w
 */

public class Test9 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal S = Nonterminal.withName("S");
		
		Nonterminal E = Nonterminal.builder("E").addParameters("l", "r").build();
		
		Character z = Character.from('z');
		Character w = Character.from('w');
		
		Rule r0 = Rule.withHead(S).addSymbol(Nonterminal.builder(E).apply(integer(0), integer(0)).build()).build();
		
		Rule r1_1 = Rule.withHead(E)
					.addSymbol(Nonterminal.builder(E).apply(integer(5), var("r"))
						.addPreCondition(predicate(greaterEq(integer(5), var("r")))).build())
					.addSymbol(z).build();
		
		Rule r1_2 = Rule.withHead(E)
					.addSymbol(Character.builder('x')
							.addPreCondition(predicate(greaterEq(integer(4), var("l")))).build())
					.addSymbol(Nonterminal.builder(E).apply(var("l"), integer(4)).build()).build();
		
		Rule r1_3 = Rule.withHead(E)
					.addSymbol(Nonterminal.builder(E).apply(integer(3), integer(0))
						.addPreCondition(predicate(greaterEq(integer(3), var("r")))).build())
					.addSymbol(w).build();
		
		Rule r1_4 = Rule.withHead(E)
					.addSymbol(Character.builder('y')
						.addPreCondition(predicate(greaterEq(integer(2), var("l")))).build())
					.addSymbol(Nonterminal.builder(E).apply(integer(0), integer(0)).build()).build();
		
		Rule r1_5 = Rule.withHead(E).addSymbol(Character.from('a')).build();
		
		grammar = Grammar.builder().addRules(r0, r1_1, r1_2, r1_3, r1_4, r1_5).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
		// Input input = Input.fromString("xawz");
		Input input = Input.fromString("xyaw");
		GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
		
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, graph, Nonterminal.withName("S"));
		
		Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", graph);
		
		if (result.isParseSuccess()) {
			Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", 
					result.asParseSuccess().getRoot(), input);
		}
		
	}

}