package chav1961.nn.api.interfaces;

import chav1961.purelib.basic.NamedValue;

public interface WordDescriptor {
	CharSequence word();
	WordDescriptor lemma();
	Iterable<WordDescriptor> forms();
	NamedValue<?>[] props();
	boolean hasProps(NamedValue<?>... prop);
	boolean hasAnyProp(NamedValue<?>... prop);
}