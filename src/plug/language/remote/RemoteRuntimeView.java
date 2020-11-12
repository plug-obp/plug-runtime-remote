package plug.language.remote;

import java.util.List;
import java.util.Objects;
import plug.runtime.core.ITransitionRelation;
import plug.runtime.core.v0.IRuntimeView;
import plug.runtime.core.v0.view.ConfigurationItem;
import plug.language.remote.runtime.Configuration;
import plug.language.remote.runtime.FireableTransition;
import plug.language.remote.runtime.RemoteRuntime;


/**
 * Created by Ciprian TEODOROV on 03/03/17.
 */
public class RemoteRuntimeView implements IRuntimeView<Configuration, FireableTransition> {
	protected final RemoteRuntime runtime;

    public RemoteRuntimeView(RemoteRuntime runtime) {
        this.runtime = runtime;
    }

	@Override
	public ITransitionRelation<Configuration, FireableTransition> getRuntime() {
    	return runtime;
	}

	@Override
	public List<ConfigurationItem> getConfigurationItems(Configuration value) {
    	return runtime.getDriver().getConfigurationItems(value);
	}

	@Override
	public String getFireableTransitionDescription(FireableTransition transition) {
    	return runtime.getDriver().getFireableTransitionDescription(transition);
	}

	@Override
	public String getOutputDescription(Object action) {
    	if (action instanceof FireableTransition) {
			return getFireableTransitionDescription((FireableTransition) action);
		} else {
    		return Objects.toString(action);
		}
	}
}
