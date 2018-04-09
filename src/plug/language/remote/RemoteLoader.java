package plug.language.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Map;
import plug.core.ILanguageLoader;
import plug.language.remote.runtime.RemoteRuntime;
import plug.utils.exec.ProcessRunner;

public class RemoteLoader implements ILanguageLoader<RemoteRuntime> {

	@Override
	public RemoteRuntime getRuntime(URI modelURI, Map<String, Object> options) throws IOException {
		RemoteDescription description = new ObjectMapper().readValue(modelURI.toURL(), RemoteDescription.class);

		int port = description.getPort();
		if (port <= 0) {
			// select a random port
			port = RemotePlugin.newPort();
		}


		String[] command = description.getCommand();
		String host = description.getHost();
		if (command != null && command.length > 0) {
			// starts the process
			ProcessRunner runner = new ProcessRunner(new PrintWriter(System.out), new PrintWriter(System.err));

			// sets port
			String portString = Integer.toString(port);
			runner.setEnv(new String[] { "PLUG_PORT", portString });
			for (int i = 0; i < command.length; i++) {
				command[i] = command[i].replaceAll("\\$PLUG_PORT", portString);
			}

			runner.setWorkingPath(Paths.get(modelURI.getPath()).getParent());
			runner.startProcess(command, null);

			// sets the host to localhost
			host = "localhost";
		}

		RemoteRuntime runtime = new RemoteRuntime(host, port);
		// TODO check when the runtime should be initialized
		runtime.initializeRuntime();
		return runtime;
	}

	
}
