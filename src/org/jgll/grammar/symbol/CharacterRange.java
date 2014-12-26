package org.jgll.grammar.symbol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.parser.HashFunctions;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.util.unicode.UnicodeUtil;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class CharacterRange extends AbstractRegularExpression implements Comparable<CharacterRange> {
	
	private static final long serialVersionUID = 1L;
	
	private final int start;
	
	private final int end;
	
	public static CharacterRange in(int start, int end) {
		return new Builder(start, end).build();
	}
		
	public CharacterRange(int start, int end, String label, Set<Condition> conditions, Object object) {
		super(getName(start, end), label, conditions, object);
		
		if(end < start) throw new IllegalArgumentException("Start cannot be less than end.");
		
		this.start = start;
		this.end = end;
	}

	private static String getName(int start, int end) {
		if (start == end) {
			return Character.getName(start);
		} else {
			return Character.getName(start) + "-" + Character.getName(end);			
		}
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public boolean contains(CharacterRange other) {
		return start <= other.start && end >= other.end;
	}
	
	public boolean overlaps(CharacterRange other) {
		return end > other.start || other.end > start;
	}

	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(start, end);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CharacterRange)) {
			return false;
		}
		
		CharacterRange other = (CharacterRange) obj;
		
		return start == other.start && end == other.end;
	}

	@Override
	protected Automaton createAutomaton() {
		State startState = new State();
		State finalState = new State(StateType.FINAL);
//		startState.addTransition(new Transition(start, end, finalState);//.addTransitionAction(getPostActions(conditions)));
		return new Automaton(startState, name);
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	public CharacterClass not() {
		return UnicodeUtil.reverse(this);
	}

	@Override
	public int compareTo(CharacterRange o) {
		return start == o.start ? end - o.end : start - o.start;
	}

	@Override
	public Set<CharacterRange> getFirstSet() {
		Set<CharacterRange> firstSet = new HashSet<>();
		firstSet.add(this);
		return firstSet;
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}

	public static Builder builder(int start, int end) {
		return new Builder(start, end);
	}
	
	public static class Builder extends SymbolBuilder<CharacterRange> {

		private int start;
		private int end;

		public Builder(int start, int end) {
			this.start = start;
			this.end = end;
		}
		
		public Builder(CharacterRange range) {
			super(range);
			this.start = range.start;
			this.end = range.end;
		}
		
		@Override
		public CharacterRange build() {
			return new CharacterRange(start, end, label, conditions, object);
		}
		
	}

	@Override
	public SymbolBuilder<CharacterRange> builder() {
		return new Builder(this);
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return "Range.in(" + start + ", " + end + ")";
	}
	
}