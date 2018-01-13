package plug.language.remote;

import plug.core.ILanguageLoader;
import plug.core.ILanguageModule;
import plug.core.IRuntimeView;
import plug.language.remote.runtime.RemoteRuntime;

/**
 * Created by Ciprian TEODOROV on 02/03/17.
 */
public class RemoteModule implements ILanguageModule<RemoteRuntime> {
    RemoteLoader loader = new RemoteLoader();

    @Override
    public String[] getExtensions() {
        return new String[] { ".remote"};
    }
    @Override
    public String getName() {
        return "RemoteModule";
    }

    @Override
    public ILanguageLoader getLoader() {
        return loader;
    }

    @Override
    public IRuntimeView getRuntimeView(RemoteRuntime runtime) {
        return new RemoteRuntimeView(runtime);
    }
}
