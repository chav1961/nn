package chav1961.nn.vocab.loaders;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteOrder;

import org.junit.Assert;
import org.junit.Test;

import chav1961.nn.vocab.interfaces.Word;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;
import chav1961.purelib.streams.byte2byte.MappedDataInputStream;

public class OpCorporaLoaderTest {

	@Test
	public void basicTest() throws FileNotFoundException, IOException, SyntaxException {
		try(final Reader	rdr = new FileReader("c:/tmp/нейросети/dict.opcorpora.xml")) {
			final long		start = System.currentTimeMillis();
			final OpCorporaLoader	ldr = new OpCorporaLoader(rdr);
			
//			System.err.println("Duration="+(System.currentTimeMillis()-start)+" msec");			
			final SyntaxTreeInterface<Word[]>	words = ldr.getVocab();
			
			final long		id = words.seekName("ёжик");
			
			Assert.assertTrue(id > 0);
			final Word[]	list = words.getCargo(id);
			
			Assert.assertEquals(2, list.length);
		}
	}

	@Test
	public void copyTest() throws FileNotFoundException, IOException, SyntaxException {
		try(final Reader	rdr = new FileReader("c:/tmp/нейросети/dict.opcorpora.xml")) {
			final long		start = System.currentTimeMillis();
			final OpCorporaLoader	ldr = new OpCorporaLoader(rdr);
			
//			System.err.println("Duration[1]="+(System.currentTimeMillis()-start)+" msec");
			
			try(final OutputStream		os = new FileOutputStream("c:/tmp/нейросети/temp.dmp");
				final OutputStream		bos = new BufferedOutputStream(os);
				final DataOutputStream	dos = new DataOutputStream(bos)) {
				final long		start2 = System.currentTimeMillis();
				
				ldr.store(dos);
				dos.flush();
//				System.err.println("Duration[2]="+(System.currentTimeMillis()-start2)+" msec");
			}

			try(final InputStream		is = new FileInputStream("c:/tmp/нейросети/temp.dmp");
				final InputStream		bis = new BufferedInputStream(is);
				final DataInputStream	dis = new DataInputStream(bis)) {
				final long				start3 = System.currentTimeMillis();
				final OpCorporaLoader	ldrNew = new OpCorporaLoader(dis);
				
				System.err.println("Duration[3]="+(System.currentTimeMillis()-start3)+" msec");
			}

			try(final MappedDataInputStream	dis = new MappedDataInputStream(new File("c:/tmp/нейросети/temp.dmp"), ByteOrder.BIG_ENDIAN)){
				final long				start4 = System.currentTimeMillis();
				final OpCorporaLoader	ldrNew = new OpCorporaLoader(dis);
					
				System.err.println("Duration[4]="+(System.currentTimeMillis()-start4)+" msec");
			}
		}
	}
}
