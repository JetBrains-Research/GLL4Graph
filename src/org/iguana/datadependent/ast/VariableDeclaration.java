package org.iguana.datadependent.ast;

import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.datadependent.traversal.IAbstractASTVisitor;

public class VariableDeclaration extends AbstractAST {
	
	private static final long serialVersionUID = 1L;

	static public Object defaultValue = new Object() {};
	
	private final String name;
	private final Expression expression;
	
	VariableDeclaration(String name, Expression expression) {
		this.name = name;
		this.expression = expression;
	}
	
	VariableDeclaration(String name) {
		this(name, null);
	}
	
	public String getName() {
		return name;
	}
	
	public Expression getExpression() {
		return expression;
	}

	@Override
	public Object interpret(IEvaluatorContext ctx) {
		Object value = defaultValue;
		if (expression != null) {
			value = expression.interpret(ctx);
		}
		ctx.declareVariable(name, value);
		return null;
	}
	
	@Override
	public String getConstructorCode() {
		return "AST.varDecl(" + "\"" + name + "\"" + (expression != null? "," + expression.getConstructorCode() : "") + ")";
	}
	
	@Override
	public String toString() {
		return expression != null? String.format( "var %s = %s", name, expression) 
				: String.format("var %s", name);
	}

	@Override
	public <T> T accept(IAbstractASTVisitor<T> visitor) {
		return visitor.visit(this);
	}

}