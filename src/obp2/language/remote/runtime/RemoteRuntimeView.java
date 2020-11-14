package obp2.language.remote.runtime;

import obp2.runtime.core.TreeItem;
import obp2.runtime.core.defaults.DefaultTreeProjector;


/**
 * Created by Ciprian TEODOROV on 03/03/17.
 */
public class RemoteRuntimeView extends DefaultTreeProjector<Configuration, FireableTransition, byte[]> {

	RemoteTransitionRelation getTransitionRelation() {
		return (RemoteTransitionRelation) this.getModule().getTransitionRelation();
	}

	@Override
	public TreeItem projectConfiguration(Configuration value) {
    	return new TreeItem(null, "remote", null, getTransitionRelation().getDriver().getConfigurationItems(value));
	}

	@Override
	public TreeItem projectFireable(FireableTransition transition) {
    	return new TreeItem(getTransitionRelation().getDriver().getFireableTransitionDescription(transition));
	}
}
