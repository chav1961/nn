package chav1961.nn.vocab.loaders;

import org.junit.Assert;
import org.junit.Test;

import chav1961.nn.vocab.interfaces.Word;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;

import java.io.Reader;
import java.util.Arrays;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class OpCorporaLoaderTest {

	@Test
	public void basicTest() throws FileNotFoundException, IOException, SyntaxException {
		try(final Reader	rdr = new FileReader("c:/tmp/нейросети/dict.opcorpora.xml")) {
			final OpCorporaLoader	ldr = new OpCorporaLoader(rdr);
			final SyntaxTreeInterface<Word[]>	words = ldr.getVocab();
			
			final long		id = words.seekName("ёжик");
			
			Assert.assertTrue(id > 0);
			final Word[]	list = words.getCargo(id);
			
			Assert.assertEquals(2, list.length);
		}
	}
}
