package plug.language.remote;

import java.util.LinkedList;
import java.util.Random;
import plug.core.ILanguagePlugin;
import plug.core.IRuntimeView;
import plug.language.remote.runtime.RemoteRuntime;

/**
 * Created by Ciprian TEODOROV on 02/03/17.
 */
public class RemotePlugin implements ILanguagePlugin<RemoteRuntime> {
    RemoteLoader loader = new RemoteLoader();

    @Override
    public String[] getExtensions() {
        return new String[] { ".remote"};
    }
    @Override
    public String getName() {
        return "Remote";
    }

    @Override
    public RemoteLoader getLoader() {
        return loader;
    }

    @Override
    public IRuntimeView getRuntimeView(RemoteRuntime runtime) {
        return new RemoteRuntimeView(runtime);
    }


    private final static Random random = new Random();
    private final static int startPort = 3456;
    private final static LinkedList<Integer> lastSelectedPorts = new LinkedList<>();

    public static int newPort() {
        int port = startPort + random.nextInt(0xFFFF - startPort);
        while (lastSelectedPorts.indexOf(port) >= 0) {
            port = startPort + random.nextInt(0xFFFF - startPort);
        }
        if (lastSelectedPorts.size() > 10) {
            lastSelectedPorts.removeFirst();
        }
        lastSelectedPorts.addLast(port);
        return port;
    }
}
