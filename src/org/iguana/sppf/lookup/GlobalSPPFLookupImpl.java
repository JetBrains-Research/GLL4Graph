package org.iguana.sppf.lookup;

import java.util.HashMap;
import java.util.Map;

import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.util.collections.IntKey3PlusObject;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Input;
import org.iguana.util.collections.IntKey3;
import org.iguana.util.collections.Key;
import org.iguana.util.hashing.hashfunction.IntHash3;
import org.iguana.util.hashing.hashfunction.IntHash4;

public class GlobalSPPFLookupImpl extends AbstractSPPFLookup {
	
	private Map<Key, TerminalNode> terminalNodes;
	
	private Map<Key, NonterminalNode> nonterminalNodes;

	private Map<Key, IntermediateNode> intermediateNodes;

	private IntHash3 f;
	
	private IntHash4 f4;

	public GlobalSPPFLookupImpl(Input input, Grammar grammar) {
		super(input);
		int inputSize = input.length() + 1;
		int grammarSize = grammar.size() + 1;
		this.nonterminalNodes = new HashMap<>();
		this.intermediateNodes = new HashMap<>();
		this.terminalNodes = new HashMap<>();
		
		this.f = (x, y, z) -> x * inputSize * inputSize + y * inputSize + z;
		this.f4 = (x, y, z, w) -> x * grammarSize * inputSize * inputSize +
				                  y * inputSize * inputSize +
				                  z * inputSize +
				                  w;
	}
	
	@Override
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return terminalNodes.computeIfAbsent(IntKey3.from(slot.getId(), leftExtent, rightExtent, f), k -> {
			TerminalNode val = new TerminalNode(slot, leftExtent, rightExtent);
			terminalNodeAdded(val);
			return val;
		});
	}
	
	@Override
	public NonterminalNode getNonterminalNode(NonterminalGrammarSlot head, int leftExtent, int rightExtent) {
		return nonterminalNodes.computeIfAbsent(IntKey3.from(head.getId(), leftExtent, rightExtent, f), k -> {
			NonterminalNode val = createNonterminalNode(head, leftExtent, rightExtent);
			nonterminalNodeAdded(val);
			return val;
		});
	}
	
	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		return intermediateNodes.computeIfAbsent(IntKey3.from(slot.getId(), leftExtent, rightExtent, f), k -> {
			IntermediateNode val = createIntermediateNode(slot, leftExtent, rightExtent);
			intermediateNodeAdded(val);
			return val;
		});
	}

	@Override
	public NonterminalNode getStartSymbol(NonterminalGrammarSlot startSymbol, int inputSize) {
		return nonterminalNodes.get(IntKey3.from(startSymbol.getId(), 0, inputSize - 1, f));
	}
	
	@Override
	public void reset() {
		super.reset();
		nonterminalNodes = new HashMap<>();
		intermediateNodes = new HashMap<>();
		terminalNodes = new HashMap<>();
	}

	@Override
	public <T> NonterminalNode getNonterminalNode(NonterminalGrammarSlot head, int leftExtent, int rightExtent, GSSNodeData<T> env) {
		return nonterminalNodes.computeIfAbsent(IntKey3PlusObject.from(env, head.getId(), leftExtent, rightExtent, f4), k -> {
			NonterminalNode val = createNonterminalNode(head, leftExtent, rightExtent);
			nonterminalNodeAdded(val);
			return val;
		});
	}

	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent, Environment env) {
		return intermediateNodes.computeIfAbsent(IntKey3PlusObject.from(env, slot.getId(), leftExtent, rightExtent, f4), k -> {
			IntermediateNode val = createIntermediateNode(slot, leftExtent, rightExtent);
			intermediateNodeAdded(val);
			return val;
		});
	}

}