package obp2.language.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import obp2.language.remote.runtime.Configuration;
import obp2.language.remote.runtime.RemoteAction;
import obp2.language.remote.runtime.RemoteLanguageModule;
import obp2.runtime.core.ILanguageModule;
import obp2.runtime.core.ILanguagePlugin;
import obp2.runtime.core.LanguageModule;
import plug.utils.Pair;
import plug.utils.exec.ProcessRunner;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Random;
import java.util.function.Function;

/**
 * Created by Ciprian TEODOROV on 02/03/17.
 */
public class RemotePlugin implements ILanguagePlugin<URI, Configuration, RemoteAction, byte[]> {
    public RemotePlugin(){}

    @Override
    public String[] getExtensions() {
        return new String[] { ".remote"};
    }
    @Override
    public String getName() {
        return "Remote";
    }

    public static LanguageModule<Configuration, RemoteAction, byte[]> getLanguageModule(String host, int port) {
        return new RemoteLanguageModule(host, port);
    }

    @Override
    public Function<URI, ILanguageModule<Configuration, RemoteAction, byte[]>> languageModuleFunction() {
        return (description) -> {
            Pair<String, Integer> endpoint = getEndpoint(description);
            return getLanguageModule(endpoint.a, endpoint.b);
        };
    }

    private Pair<String, Integer> getEndpoint(URI description) {
        RemoteResource resource = null;
        try {
            resource = new ObjectMapper().readValue(description.toURL(), RemoteResource.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int port = resource.getPort();
        if (port <= 0) {
            // select a random port
            port = RemotePlugin.newPort();
        }

        String[] command = resource.getCommand();
        String host = resource.getHost();
        if (command != null && command.length > 0) {
            // starts the process
            ProcessRunner runner = new ProcessRunner(new PrintWriter(System.out), new PrintWriter(System.err));

            // sets port
            String portString = Integer.toString(port);
            runner.setEnv(new String[] { "PLUG_PORT", portString });
            for (int i = 0; i < command.length; i++) {
                command[i] = command[i].replaceAll("\\$PLUG_PORT", portString);
            }

            runner.setWorkingPath(Paths.get(description.getPath()).getParent());
            runner.startProcess(command, null);

            // sets the host to localhost
            host = "localhost";
        }
        return new Pair<>(host, port);
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
