package chav1961.nn.w2v.trainer;

public class W2VTrainer {
	private final W2VMutableStore	store;
	
	public W2VTrainer(final W2VMutableStore store) {
		if (store == null) {
			throw new NullPointerException("Mutable store can't be null");
		}
		else {
			this.store = store;
		}
	}

	
}
