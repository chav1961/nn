package chav1961.nn.vocab.loaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteOrder;

import org.junit.Assert;
import org.junit.Test;

import chav1961.nn.api.interfaces.Word;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;
import chav1961.purelib.streams.byte2byte.DataOutputStream;
import chav1961.purelib.streams.byte2byte.MappedDataInputStream;
import chav1961.purelib.streams.byte2byte.NIOOutputStream;
import chav1961.purelib.streams.byte2char.UTF8Reader;

public class OpCorporaLoaderTest {
	final File		vocab = new File("c:/tmp/нейросети/dict.opcorpora.xml");
	final File		dump = new File("c:/tmp/нейросети/dict.opcorpora.dmp");

	@Test
	public void basicTest() throws FileNotFoundException, IOException, SyntaxException {
		try(final Reader	rdr = new UTF8Reader(vocab)) {
			final long		start = System.currentTimeMillis();
			final OpCorporaLoader	ldr = new OpCorporaLoader(rdr);
			
			System.err.println("Duration="+(System.currentTimeMillis()-start)+" msec");			
			final SyntaxTreeInterface<Word[]>	words = ldr.getVocab();
			
			final long		id = words.seekName("ёжик");
			
			Assert.assertTrue(id > 0);
			final Word[]	list = words.getCargo(id);
			
			Assert.assertEquals(2, list.length);
		}
	}

	@Test
	public void copyTest() throws FileNotFoundException, IOException, SyntaxException {
		
		try(final Reader	rdr = new UTF8Reader(vocab);/*;
			final Reader	brdr = new BufferedReader(rdr)*/) {
			final long		start = System.currentTimeMillis();
			final OpCorporaLoader	ldr = new OpCorporaLoader(rdr);
			
			System.err.println("Duration[1]="+(System.currentTimeMillis()-start)+" msec");
			
			try(final OutputStream		os = new NIOOutputStream(dump);
				final DataOutputStream	dos = new DataOutputStream(os, ByteOrder.BIG_ENDIAN)) {
				final long		start2 = System.currentTimeMillis();
				
				ldr.store(dos);
				dos.flush();
				System.err.println("Duration[2]="+(System.currentTimeMillis()-start2)+" msec");
			}

			try(final MappedDataInputStream	dis = new MappedDataInputStream(dump, ByteOrder.BIG_ENDIAN)){
				final long				start4 = System.currentTimeMillis();
				final OpCorporaLoader	ldrNew = new OpCorporaLoader(dis);
				final SyntaxTreeInterface<Word[]>	words = ldrNew.getVocab();
					
				System.err.println("Duration[3]="+(System.currentTimeMillis()-start4)+" msec");

				final long		id = words.seekName("ёжик");
				
				Assert.assertTrue(id > 0);
				final Word[]	list = words.getCargo(id);
				
				Assert.assertEquals(2, list.length);
			}
		}
	}
}
