package obp2.language.remote.runtime;

import obp2.runtime.core.TreeItem;
import obp2.runtime.core.defaults.DefaultTreeProjector;


/**
 * Created by Ciprian TEODOROV on 03/03/17.
 */
public class RemoteRuntimeView extends DefaultTreeProjector<Configuration, RemoteAction, byte[]> {

	RemoteTransitionRelation getTransitionRelation() {
		return (RemoteTransitionRelation) this.getModule().getTransitionRelation();
	}

	@Override
	public TreeItem projectConfiguration(Configuration value) {
    	return new TreeItem(null, "remote", null, getTransitionRelation().getDriver().getConfigurationItems(value));
	}

	@Override
	public TreeItem projectFireable(RemoteAction transition) {
    	return new TreeItem(getTransitionRelation().getDriver().getFireableTransitionDescription(transition));
	}

//	@Override
//	public TreeItem projectPayload(byte[] output) {
//		return new TreeItem(new String(output, StandardCharsets.UTF_8));
//	}
}
