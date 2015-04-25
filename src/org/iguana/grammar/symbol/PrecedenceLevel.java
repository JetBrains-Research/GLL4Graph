package org.iguana.grammar.symbol;

import java.io.Serializable;

public class PrecedenceLevel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final int lhs;
	private int rhs = -1;
	
	private boolean hasPrefixUnary = false;
	private boolean hasPrefixUnaryBelow = false;
	
	private boolean hasPostfixUnary = false;
	private boolean hasPostfixUnaryBelow = false;
	
	private int undefined = -1;
	
	private int index;
	
	private boolean containsAssociativityGroup = false;
	private int assoc_lhs = -1;
	private int assoc_rhs = -1;
	
	PrecedenceLevel(int lhs) {
		this.lhs = lhs;
		this.index = lhs;
	}
	
	public static PrecedenceLevel from(int lhs, int rhs, int undefined, boolean hasPrefixUnary, boolean hasPostfixUnary, 
										boolean hasPrefixUnaryBelow, boolean hasPostfixUnaryBelow) {
		PrecedenceLevel level = new PrecedenceLevel(lhs);
		level.rhs = rhs;
		level.undefined = undefined;
		level.hasPrefixUnary = hasPrefixUnary;
		level.hasPostfixUnary = hasPostfixUnary;
		level.hasPrefixUnaryBelow = hasPrefixUnaryBelow;
		level.hasPostfixUnaryBelow = hasPostfixUnaryBelow;
		return level;
	}
	
	public static PrecedenceLevel getFirst() {
		return new PrecedenceLevel(1);
	}
	
	public static PrecedenceLevel getFirstAndDone() {
		PrecedenceLevel level = new PrecedenceLevel(1);
		level.done();
		return level;
	}
	
	public PrecedenceLevel getNext() {
		this.done();
		
		PrecedenceLevel next = new PrecedenceLevel(index);
		
		if (hasPrefixUnary || hasPrefixUnaryBelow)
			next.hasPrefixUnaryBelow = true;
		
		if (hasPostfixUnary || hasPostfixUnaryBelow)
			next.hasPostfixUnaryBelow = true;
		
		return next;
	}
	
	public int getLhs() {
		return lhs;
	}
	
	public int getRhs() {
		return rhs;
	}
	
	public boolean hasPrefixUnary() {
		return hasPrefixUnary;
	}
	
	public boolean hasPostfixUnary() {
		return hasPostfixUnary;
	}
	
	public boolean hasPrefixUnaryBelow() {
		return hasPrefixUnaryBelow;
	}
	
	public boolean hasPostfixUnaryBelow() {
		return hasPostfixUnaryBelow;
	}
	
	public int getPrecedence(Rule rule) {
		
		if (rule.isUnary() && rule.isRightRecursive()) hasPrefixUnary = true;
		if (rule.isUnary() && rule.isLeftRecursive()) hasPostfixUnary = true;
		
		if (!rule.isLeftOrRightRecursive()) return -1;
		else if (rule.getAssociativity() == Associativity.UNDEFINED) {
			if (undefined == -1)
				undefined = index++;
			return undefined;
		} else
			return index++;
	}
	
	int getPrecedenceFromAssociativityGroup(Rule rule) {
		if (rule.isUnary() && rule.isRightRecursive()) hasPrefixUnary = true;
		if (rule.isUnary() && rule.isLeftRecursive()) hasPostfixUnary = true;
		
		if (!rule.isLeftOrRightRecursive()) return -1;
		else return index++;
	}
	
	void setHasPrefixUnaryFromAssociativityGroup() {
		this.hasPrefixUnary = true;
	}
	
	void setHasPostfixUnaryFromAssociativityGroup() {
		this.hasPostfixUnary = true;
	}
	
	public void setUndefinedIfNeeded() {
		if (undefined == -1) {
			int rhs = index == lhs? index : index - 1;
			if (lhs != rhs && !(containsAssociativityGroup && lhs == assoc_lhs && rhs == assoc_rhs)) 
				undefined = index++;
		} 
	}
	
	public boolean isUndefined(int precedence) {
		return this.undefined != -1 && this.undefined == precedence;
	}
	
	public int getUndefined() {
		if (lhs == 1)
			return 0;
		return undefined;
	}
	
	public void done() {
		assert rhs != -1;
		rhs = index == lhs? index : index - 1;
	}
	
	int getCurrent() {
		return index == lhs? index : index - 1;
	}
	
	public void containsAssociativityGroup(int l, int r) {
		this.containsAssociativityGroup = true;
		this.assoc_lhs = l;
		this.assoc_rhs = r;
	}
		
	public String getConstructorCode() {
		return getClass().getSimpleName() + ".from(" + lhs + "," + rhs + "," + undefined + "," + hasPrefixUnary + "," + hasPostfixUnary + "," 
														+ hasPrefixUnaryBelow + "," + hasPostfixUnaryBelow + ")";
	}
	
	@Override
	public String toString() {
		return "PREC(" + lhs + "," + rhs + ")";
	}
}