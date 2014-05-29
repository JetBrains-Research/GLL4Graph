package org.jgll.util.visualization;

import static org.jgll.util.visualization.GraphVizUtil.*;

import org.jgll.grammar.GrammarGraph;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.traversal.SPPFVisitorUtil;
import org.jgll.util.Input;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 */
public class ToDotWithoutIntermediateNodes extends SPPFToDot {
	
	public ToDotWithoutIntermediateNodes(GrammarGraph grammarGraph, Input input) {
		super(grammarGraph, input);
	}
	
	@Override
	public void visit(NonterminalSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			
			if(node.isAmbiguous()) {
				
				int i = 0;
				while(i < node.childrenCount()) {
					SPPFVisitorUtil.removeIntermediateNode((PackedNode) node.getChildAt(i));
					i++;
				}
			} else {
				SPPFVisitorUtil.removeIntermediateNode(node);
			}
	
			String label = grammarGraph.getNonterminalById(node.getId()).getName();
			sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
			addEdgesToChildren(node);
			
			SPPFVisitorUtil.visitChildren(node, this);
		}		
	}
}