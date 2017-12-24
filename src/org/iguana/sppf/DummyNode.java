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

package org.iguana.sppf;

import org.iguana.grammar.slot.DummySlot;
import org.iguana.traversal.SPPFVisitor;

import java.util.List;

/**
 *
 * @author Ali Afroozeh
 *
 */
public class DummyNode extends NonPackedNode {

	private int rightExtent;

	public DummyNode(int rightExtent) {
		this.rightExtent = rightExtent;
	}

	@Override
	public PackedNode getChildAt(int index) {
		return null;
	}

	@Override
	public List<PackedNode> getChildren() {
		return null;
	}

	@Override
	public int childrenCount() {
		return 0;
	}

	@Override
	public DummySlot getGrammarSlot() {
		return DummySlot.getInstance();
	}

	@Override
	public int getLeftExtent() {
		return 0;
	}

	@Override
	public int getRightExtent() {
		return rightExtent;
	}

	@Override
	public <R> R accept(SPPFVisitor<R> visitAction) {
		return null;
	}

	@Override
	public boolean isDummy() {
		return true;
	}

	@Override
	public String toString() {
		return "$";
	}

}