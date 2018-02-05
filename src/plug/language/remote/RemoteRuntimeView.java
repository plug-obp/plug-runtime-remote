package plug.language.remote;

import java.util.Arrays;
import plug.core.ILanguageRuntime;
import plug.core.IRuntimeView;
import plug.core.view.ConfigurationItem;
import plug.language.remote.runtime.Configuration;
import plug.language.remote.runtime.FireableTransition;
import plug.language.remote.runtime.RemoteRuntime;


/**
 * Created by Ciprian TEODOROV on 03/03/17.
 */
public class RemoteRuntimeView implements IRuntimeView<Configuration, FireableTransition> {
	RemoteRuntime runtime;

    public RemoteRuntimeView(RemoteRuntime runtime) {
        this.runtime = runtime;
    }

	@Override
	public ILanguageRuntime<Configuration, FireableTransition> getRuntime() {
    	return runtime;
	}

	@Override
	public ConfigurationItem getConfigurationItem(Configuration value) {
		return new ConfigurationItem("configuration", value.toString(), null, null);
	}

	@Override
	public String getFireableTransitionDescription(FireableTransition transition) {
    	return "Remote " + Arrays.toString(transition.rawTransitionData);
	}

}
