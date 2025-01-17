/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */
package org.iguana.iggy;

import iguana.regex.Char;
import iguana.regex.Epsilon;
import org.iguana.datadependent.ast.Expression;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Anastasia Izmaylova
 */
public abstract class Builder {

    private final Map<String, Rule> regexs = new HashMap<>();
	
	public Grammar grammar(List<Object> rules) {
		org.iguana.grammar.Grammar.Builder builder = Grammar.builder();
		rules.forEach(rule -> builder.addRule((Rule) rule));
		return builder.build();
	}
	
	public List<Rule> rule(List<Object> tag, Object name, List<Object> params, List<Object> body) { // TODO: Add the logic of handling precedence
		List<Rule> rules = new ArrayList<>();
		body.forEach(elem -> {
			((PrecGroup)elem).alts.forEach(alt -> {
				final Nonterminal head = params.isEmpty() ? Nonterminal.withName((String)name)
                        : Nonterminal.builder((String)name)
                              .addParameters(params.stream().map(p -> (String)p).toArray(String[]::new))
                              .build();
				if (alt instanceof Sequence) {
					org.iguana.grammar.symbol.Rule.Builder builder = Rule.withHead(head);
					builder.addSymbols(((Sequence)alt).syms);
					rules.add(builder.build());
				} else if (alt instanceof AssocGroup) {
					((AssocGroup)alt).seqs.forEach(seq -> {
						org.iguana.grammar.symbol.Rule.Builder builder = Rule.withHead(head);
						builder.addSymbols(seq.syms);
						rules.add(builder.build());
					});
				}
			});
		});
		return rules;
	}
	
	public Object rule(Object name, Object body) {
        Rule.Builder builder = Rule.withHead(Nonterminal.withName((String)name));
        builder.addSymbol((Symbol)body);
        Rule rule = builder.build();
        regexs.put((String)name, rule);
        return rule;
	}
	
	public Object precGroup(List<Object> alts) {
		return new PrecGroup(alts);
	}
	
	public Object assocGroup(List<Object> elems) {
		List<Sequence> seqs = new ArrayList<>();
		List<String> assoc = new ArrayList<>();
		elems.forEach(elem -> {
			if (elem instanceof Sequence)
				seqs.add((Sequence) elem);
			else if (elem instanceof String)
				assoc.add((String) elem);
		});
		return new AssocGroup(seqs, assoc.get(0));
	}
	
	public Sequence body(List<Object> elems) {
		List<Symbol> syms = new ArrayList<>();
		List<String> attrs = new ArrayList<>();
		elems.forEach(elem -> {
			if (elem instanceof Symbol)
				syms.add((Symbol) elem);
			else if (elem instanceof Expression)
				syms.add(Return.ret((Expression) elem));
			else if (elem instanceof String)
				attrs.add((String) elem);
		});
		return new Sequence(syms, attrs);
	}
	
	public Object star(Object sym) {
        return Star.from((Symbol)sym);
	}
	
	public Object plus(Object sym) {
		return Plus.from((Symbol)sym);
	}
	
	public Object opt(Object sym) {
		return Opt.from((Symbol)sym);
	}
	
	public Object seqGroup(List<Object> syms) {
        return org.iguana.grammar.symbol.Sequence.from(syms.stream().map(sym -> (Symbol)sym).collect(Collectors.toList()));
	}
	
	public Object altGroup(List<Object> elems) {
        List<Symbol> syms = new ArrayList<>();
        for (Object elem : elems) {
            if (elem instanceof Symbol)
                syms.add((Symbol)elem);
            else if (elem instanceof Sequence)
                syms.add(org.iguana.grammar.symbol.Sequence.from(((Sequence)elem).syms));
        }
		return Alt.from(syms);
	}

    public Object syms(List<Object> syms) {
        if (syms.isEmpty())
            return Epsilon.getInstance();

        if (syms.size() == 1)
            return syms.get(0);

        return new Sequence(syms.stream().map(sym -> (Symbol)sym).collect(Collectors.toList()), null);
    }

    public Object regStar(Object s) {
        return star(s);
    }

    public Object regPlus(Object s) {
        return plus(s);
    }

    public Object regOpt(Object s) {
        return opt(s);
    }

    public Object regSeqGroup(List<Object> ss) {
        return seqGroup(ss);
    }

    public Object regAltGroup(List<Object> ss) {
        return altGroup(ss);
    }

    public Object regs(List<Object> ss) {
        return syms(ss);
    }

	public Nonterminal nontCall(Object sym, List<Object> args) {
		org.iguana.grammar.symbol.Nonterminal.Builder builder = Nonterminal.builder((Nonterminal) sym);
		if (args.isEmpty()) 
			return builder.build();
		return builder
				.apply(args.stream().map(arg -> (Expression) args)
						.toArray(Expression[]::new)).build();
	}
	
	public Nonterminal variable(Object name, Object sym) {
		return Nonterminal.builder(((Nonterminal)sym)).setVariable((String) name).build();
	}
	
	public Symbol label(Object name, Object sym) {
		return ((Symbol)sym).copyBuilder().setLabel((String)name).build();
	}
	
	public Nonterminal nont(Object name) {
		return Nonterminal.withName((String)name);
	}
	
	public iguana.regex.Seq<Char> string(Object obj) {
        String s = (String) obj;
        s = s.substring(1, s.length() - 1);
        return iguana.regex.Seq.from(s.chars().toArray());
    }
	
	/*
	 *  Helper classes
	 */
	
	public static class PrecGroup {
		public final List<Object> alts;
		public PrecGroup(List<Object> alts) {
			this.alts = alts;
		}
	}
	
	public static class AssocGroup {
		public final List<Sequence> seqs;
		public final String assoc;
		public AssocGroup(List<Sequence> seqs, String assoc) {
			this.seqs = seqs;
			this.assoc = assoc;
		}
	}
	
	public static class Sequence {
		public final List<Symbol> syms;
		public final List<String> attrs;
		public Sequence(List<Symbol> syms, List<String> attrs) {
			this.syms = syms;
			this.attrs = attrs;
		}
	}

}
