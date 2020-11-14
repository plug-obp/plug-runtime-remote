package obp2.language.remote.runtime;

import obp2.language.remote.driver.TCPDriver;
import obp2.runtime.core.defaults.DefaultAtomicPropositionEvaluator;

public class RemoteAtomicPropositionsEvaluator extends DefaultAtomicPropositionEvaluator<Configuration, FireableTransition, byte[]> {

	protected final TCPDriver driver;

	public RemoteAtomicPropositionsEvaluator(TCPDriver driver) {
		this.driver = driver;
	}

	@Override
	public int[] registerAtomicPropositions(String[] atomicPropositions) throws Exception {
		return driver.registerAtomicPropositions(atomicPropositions);
	}

	@Override
	public boolean[] getAtomicPropositionValuations(Configuration target) {
		return driver.getAtomicPropositionValuations(target);
	}

	@Override
	public boolean[] getAtomicPropositionValuations(Configuration source, FireableTransition fireable, byte[] payload, Configuration target) {
		return driver.getAtomicPropositionValuations(source, fireable, payload, target);
	}
}
