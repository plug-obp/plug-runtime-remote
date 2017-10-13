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
    RemoteRuntimeView runtimeView;

    static RemoteModule instance = new RemoteModule();

    public static RemoteModule getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return "ViaTCP";
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
