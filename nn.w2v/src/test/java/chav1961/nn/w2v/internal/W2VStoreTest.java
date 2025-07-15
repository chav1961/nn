package chav1961.nn.w2v.internal;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import chav1961.nn.vocab.loaders.OpCorporaLoader;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.streams.byte2byte.MappedDataInputStream;

public class W2VStoreTest {
	private static OpCorporaLoader	ldr;
	
	@BeforeClass
	public static void prepare() throws SyntaxException, IOException {
		try(final MappedDataInputStream	dis = new MappedDataInputStream(new File("c:/tmp/нейросети/dict.opcorpora.dmp"), ByteOrder.BIG_ENDIAN)) {
			ldr = new OpCorporaLoader(dis);
		}
	}

	@Test
	public void basicTest() throws IOException, SyntaxException {
		
		W2VStore	store = new W2VStore(ldr.getVocab());
	}
}
