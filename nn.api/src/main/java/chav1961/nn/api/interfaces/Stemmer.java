package chav1961.nn.api.interfaces;

import java.util.function.Function;

public interface Stemmer {
	CharSequence resolve(CharSequence source);
	CharSequence resolve(CharSequence source, Stemmer... chain);
	CharSequence resolve(CharSequence source, Function<CharSequence, CharSequence> resolver);
}
