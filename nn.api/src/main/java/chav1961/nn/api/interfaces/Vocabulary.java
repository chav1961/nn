package chav1961.nn.api.interfaces;

import java.util.function.Function;

public interface Vocabulary extends Iterable<WordDescriptor>{
	boolean hasWord(CharSequence word);
	Iterable<WordDescriptor> resolve(CharSequence word);
	Iterable<WordDescriptor> resolve(CharSequence word, Vocabulary... chain);
	Iterable<WordDescriptor> resolve(CharSequence word, Function<CharSequence, CharSequence> resolver);
}
