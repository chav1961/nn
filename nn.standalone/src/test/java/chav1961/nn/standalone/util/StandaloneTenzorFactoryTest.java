package chav1961.nn.standalone.util;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;

import chav1961.nn.api.interfaces.Tenzor;

public class StandaloneTenzorFactoryTest {
	
	@Test
	public void spiTest() {
		final StandaloneTenzorFactory	stf = new StandaloneTenzorFactory();
		final URI						serveURI = URI.create(Tenzor.TenzorFactory.TENZOR_FACTORY_SCHEMA+":standalone:/"); 
		
		Assert.assertTrue(stf.canServe(serveURI));
		Assert.assertEquals(stf, stf.newInstance(serveURI));
		
		try {
			stf.canServe(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {
			stf.newInstance((URI)null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
	}


	@Test
	public void basicTenzorTest() {
		final StandaloneTenzorFactory	stf = new StandaloneTenzorFactory(); 
		final Tenzor	t = stf.newInstance(2, 3, 4);

		Assert.assertEquals(3, t.getArity());
		Assert.assertEquals(2, t.getSize(0));
		Assert.assertEquals(3, t.getSize(1));
		Assert.assertEquals(4, t.getSize(2));
		
		
	}
}
