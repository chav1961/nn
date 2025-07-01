package chav1961.nn.vocab.loaders;

import chav1961.nn.vocab.interfaces.Grammeme;
import chav1961.nn.vocab.interfaces.WordForm;
import chav1961.nn.vocab.interfaces.WordRestrictionType;

public class Restriction {
	public final WordRestrictionType	type;
	public final boolean	auto;
	public final WordForm	leftForm;
	public final Grammeme	leftGram;
	public final WordForm	rightForm;
	public final Grammeme	rightGram;
	
	public Restriction(final WordRestrictionType type, final boolean auto, final WordForm leftForm, final Grammeme leftGram, final WordForm rightForm, final Grammeme rightGram) {
		this.type = type;
		this.auto = auto;
		this.leftForm = leftForm;
		this.leftGram = leftGram;
		this.rightForm = rightForm;
		this.rightGram = rightGram;
	}

	@Override
	public String toString() {
		return "Restriction [type=" + type + ", auto=" + auto + ", leftForm=" + leftForm + ", leftGram=" + leftGram
				+ ", rightForm=" + rightForm + ", rightGram=" + rightGram + "]";
	}
}