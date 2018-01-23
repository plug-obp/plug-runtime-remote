package plug.language.remote.runtime;

import plug.core.IAtomicPropositionsEvaluator;
import plug.language.remote.driver.TCPDriver;

public class RemoteAtomicPropositionsEvaluator implements IAtomicPropositionsEvaluator<Configuration, FireableTransition> {


	protected final TCPDriver driver;

	public RemoteAtomicPropositionsEvaluator(TCPDriver driver) {
		this.driver = driver;
	}

	@Override
	public void registerAtomicPropositions(String[] atomicPropositions) {
		driver.registerAtomicPropositions(atomicPropositions);
	}

	@Override
	public boolean[] getAtomicPropositionValuations(Configuration target) {
		return driver.getAtomicPropositionValuations(target);
	}

	public boolean[] getAtomicPropositionValuations(Configuration source, Configuration target, FireableTransition transition) {
		return driver.getAtomicPropositionValuations(source, target, transition);
	}
}
