package obp2.language.remote.runtime;

import obp2.language.remote.driver.TCPDriver;
import obp2.runtime.core.IAtomicPropositionsEvaluator;
import obp2.runtime.core.defaults.DefaultLanguageService;

public class RemoteAtomicPropositionsEvaluator
		extends DefaultLanguageService<Configuration, RemoteAction, byte[]>
		implements IAtomicPropositionsEvaluator<Configuration, RemoteAction, byte[]> {

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
	public boolean[] getAtomicPropositionValuations(Configuration source, RemoteAction fireable, byte[] payload, Configuration target) {
		return driver.getAtomicPropositionValuations(source, fireable, payload, target);
	}
}
