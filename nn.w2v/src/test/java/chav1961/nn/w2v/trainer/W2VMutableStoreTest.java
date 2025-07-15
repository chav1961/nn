package chav1961.nn.w2v.trainer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteOrder;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import chav1961.nn.api.interfaces.Word;
import chav1961.nn.vocab.loaders.OpCorporaLoader;
import chav1961.purelib.basic.exceptions.CalculationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.streams.byte2byte.MappedDataInputStream;

public class W2VMutableStoreTest {
	private static OpCorporaLoader	ldr;
	
	@BeforeClass
	public static void prepare() throws SyntaxException, IOException {
		try(final MappedDataInputStream	dis = new MappedDataInputStream(new File("c:/tmp/нейросети/dict.opcorpora.dmp"), ByteOrder.BIG_ENDIAN)) {
			ldr = new OpCorporaLoader(dis);
		}
	}

	@Test
	public void lifeCycleTest() throws IOException, CalculationException {
		final W2VMutableStore	store = new W2VMutableStore(ldr.getVocab());
		long	frogId = 0;
		float[]	frogVector = null;
		
		try(final Reader	rdr = new InputStreamReader(this.getClass().getResourceAsStream("Sentences.txt"))) {
			store.buildCurrentVocab(rdr);

			try {
				store.buildCurrentVocab((Reader)null);
				Assert.fail("Mandatory exception was not detected (null 1-st argument)");
			} catch (NullPointerException exc) {
			}

			frogId = ldr.getVocab().seekName("лягушка");
			Assert.assertTrue(frogId > 0);
			frogVector = store.encode(ldr.getVocab().getCargo(frogId)[0]);
			
		}
		
		try(final ByteArrayOutputStream	baos = new ByteArrayOutputStream()) {
			try(final DataOutputStream	dos = new DataOutputStream(baos)) {
				store.uploadVocab(dos);
				
				try {
					store.uploadVocab(null);
					Assert.fail("Mandatory exception was not detected (null 1-st argument)");
				} catch (NullPointerException exc) {
				}
			}
			
			try(final ByteArrayInputStream	bais = new ByteArrayInputStream(baos.toByteArray());
				final DataInputStream		dis = new DataInputStream(bais)) {
				final W2VMutableStore		newStore = new W2VMutableStore(ldr.getVocab());
				
				newStore.downloadVocab(dis);
				
				try {
					store.downloadVocab(null);
					
					Assert.fail("Mandatory exception was not detected (null 1-st argument)");
				} catch (NullPointerException exc) {
				}

				Assert.assertArrayEquals(frogVector, newStore.encode(ldr.getVocab().getCargo(frogId)[0]), 0.001f);
			}
		}
	}
}
