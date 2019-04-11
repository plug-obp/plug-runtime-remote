package plug.language.remote.runtime;

import plug.core.IAtomicPropositionsEvaluator;
import plug.language.remote.driver.TCPDriver;

public class RemoteAtomicPropositionsEvaluator implements IAtomicPropositionsEvaluator<Configuration, FireableTransition> {

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
	public boolean[] getAtomicPropositionValuations(Configuration source, FireableTransition fireable, Object payload, Configuration target) {
		return driver.getAtomicPropositionValuations(source, fireable, payload, target);
	}
}
