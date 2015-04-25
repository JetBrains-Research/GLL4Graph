package org.iguana.parser;


import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.DummySlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSEdge;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.parser.gss.OriginalGSSEdgeImpl;
import org.iguana.parser.gss.lookup.GSSLookup;
import org.iguana.parser.lookup.DescriptorLookup;
import org.iguana.sppf.DummyNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.lookup.SPPFLookup;
import org.iguana.util.Configuration;

/**
 *
 * @author Ali Afroozeh
 * 
 */
public class OriginalGLLParserImpl extends AbstractGLLParserImpl {
	
	protected final GSSNode u0 = gssLookup.getGSSNode(DummySlot.getInstance(), 0);
		
	public OriginalGLLParserImpl(Configuration config, GSSLookup gssLookup, SPPFLookup sppfLookup, DescriptorLookup descriptorLookup) {
		super(config, gssLookup, sppfLookup, descriptorLookup);
	}
	
	@Override
	protected void initParserState(NonterminalGrammarSlot startSymbol) {
		u0.clearDescriptors();
		cu = u0;
		cn = DummyNode.getInstance();
		ci = 0;
		errorSlot = null;
		errorIndex = 0;
		errorGSSNode = null;
	}
	
	@Override
	public final void pop(GSSNode gssNode, int inputIndex, NonPackedNode node) {
		
		if (gssNode != u0) {

			if (!gssLookup.addToPoppedElements(gssNode, node))
				return;
			
			log.debug("Pop %s, %d, %s", gssNode, inputIndex, node);

			for (GSSEdge edge : gssNode.getGSSEdges()) {
				Descriptor descriptor = edge.addDescriptor(this, gssNode, inputIndex, node);
				if (descriptor != null) {
					scheduleDescriptor(descriptor);
				}				
			}
		}
	}
	
	@Override
	public final GSSNode createGSSNode(BodyGrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i) {
		return gssLookup.getGSSNode(returnSlot, ci);
	}
	
	@Override
	public final GSSNode hasGSSNode(BodyGrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i) {
		return gssLookup.hasGSSNode(returnSlot, ci);
	}

	@Override
	public void createGSSEdge(BodyGrammarSlot slot, GSSNode destination, NonPackedNode w, GSSNode source) {		
		GSSEdge edge = new OriginalGSSEdgeImpl(w, destination);
		
		if(gssLookup.getGSSEdge(source, edge)) {		
			log.trace("GSS Edge created from %s to %s", source, destination);
			
			for (NonPackedNode z : source.getPoppedElements()) {
				Descriptor descriptor = edge.addDescriptor(this, source, z.getRightExtent(), z);
				if (descriptor != null) {
					scheduleDescriptor(descriptor);
				}
			}
		}
	}

	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public void createGSSEdge(BodyGrammarSlot slot, GSSNode destination, NonPackedNode w, GSSNode source, Environment env) {
		GSSEdge edge = new org.iguana.datadependent.gss.OriginalGSSEdgeImpl(w, destination, env);
		
		if(gssLookup.getGSSEdge(source, edge)) {
			log.trace("GSS Edge created from %s to %s", source, destination);
			
			for (NonPackedNode z : source.getPoppedElements()) {
				Descriptor descriptor = edge.addDescriptor(this, source, z.getRightExtent(), z);
				if (descriptor != null) {
					scheduleDescriptor(descriptor);
				}
			}
		}
	}

	@Override
	public <T> GSSNode createGSSNode(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i, GSSNodeData<T> data) {
		return gssLookup.getGSSNode(returnSlot, ci, data);
	}

	@Override
	public <T> GSSNode hasGSSNode(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i, GSSNodeData<T> data) {
		return gssLookup.hasGSSNode(returnSlot, ci, data);
	}

}