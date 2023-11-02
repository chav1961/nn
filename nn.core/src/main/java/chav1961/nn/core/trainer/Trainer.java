package chav1961.nn.core.trainer;

import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.core.dataset.DataSetManager;
import chav1961.purelib.basic.interfaces.ProgressIndicator;

public class Trainer {
	private final NeuralNetwork	nn;
	
	public Trainer(final NeuralNetwork nn) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else {
			this.nn = nn;
		}
	}
	
	public boolean train(final DataSetManager mgr, final int maxEpoch, final float maxError, final float learningSpeed) {
		return train(mgr, maxEpoch, maxError, learningSpeed, ProgressIndicator.DUMMY);
	}

	public boolean train(final DataSetManager mgr, final int maxEpoch, final float maxError, final float learningSpeed, final ProgressIndicator pi) {
		if (mgr == null) {
			throw new NullPointerException("Manager can't be null"); 
		}
		else if (maxEpoch <= 0) {
			throw new IllegalArgumentException("Max epoch ["+maxEpoch+"] must be greater than 0"); 
		}
		else if (maxError <= 0) {
			throw new IllegalArgumentException("Max error ["+maxError+"] must be greater than 0"); 
		}
		else if (learningSpeed <= 0 || learningSpeed >= 1) {
			throw new IllegalArgumentException("Learning speed ["+maxError+"] must be in range 0..1 (exclusive)"); 
		}
		else if (pi == null) {
			throw new NullPointerException("Progress indicater can't be null"); 
		}
		else {
			final boolean[]	stop = new boolean[] {false};
			
			pi.start("", maxEpoch);
			for(int index = 0; index < maxEpoch && !stop[0]; index++) {
				pi.processed(index);
				mgr.forEach((in,out)->{stop[0] = learn(in, nn, out, maxError, learningSpeed);});
			}
			pi.end();
			return stop[0];
		}
	}

	public boolean test(final DataSetManager mgr, final float maxError) {
		if (mgr == null) {
			throw new NullPointerException("Manager can't be null"); 
		}
		else if (maxError <= 0) {
			throw new IllegalArgumentException("Max error ["+maxError+"] must be greater than 0"); 
		}
		else {
			final boolean[]	stop = new boolean[] {false};
			
			mgr.forEach((in,out)->{stop[0] = test(in, nn, out, maxError);});
			return stop[0];
		}
	}
	
	private boolean learn(final Tenzor in, final NeuralNetwork nn, final Tenzor out, final float maxError, final float learningSpeed) {
		final Tenzor	t = nn.forward(in);
		final float[]	content1 = t.getContent(), content2 = out.getContent();
		boolean			continuationRequired = false;
		
		for(int index = 0; index < content1.length; index++) {
			if (Math.abs(content1[index] - content2[index]) > maxError) {
				continuationRequired = true;
			}
		}
		if (continuationRequired) {
			for(int index = 0; index < content1.length; index++) {
				content1[index] = learningSpeed * (content1[index] - content2[index]);
			}
			nn.backward(t);
			return false;
		}
		else {
			return true;
		}
	}

	private boolean test(final Tenzor in, final NeuralNetwork nn, final Tenzor out, final float maxError) {
		final Tenzor	t = nn.forward(in);
		final float[]	content1 = t.getContent(), content2 = out.getContent();
		boolean			continuationRequired = false;
		
		for(int index = 0; index < content1.length; index++) {
			if (Math.abs(content1[index] - content2[index]) > maxError) {
				continuationRequired = true;
			}
		}
		return continuationRequired;
	}

}
