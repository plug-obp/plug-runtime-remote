package plug.language.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Map;
import plug.core.ILanguageLoader;
import plug.core.ILanguageRuntime;
import plug.language.remote.runtime.RemoteRuntime;
import plug.utils.exec.ProcessRunner;

public class RemoteLoader implements ILanguageLoader {

	public RemoteLoader() {

	}

	@Override
	public ILanguageRuntime getRuntime(URI modelURI, Map<String, Object> options) {
		try {
			RemoteDescription description = new ObjectMapper().readValue(modelURI.toURL(), RemoteDescription.class);

			String[] command = description.getCommand();
			String host = description.getHost();
			if (command != null && command.length > 0) {
				// starts the process
				ProcessRunner runner = new ProcessRunner(new PrintWriter(System.out), new PrintWriter(System.err));
				runner.setWorkingPath(Paths.get(modelURI.getPath()).getParent());
				runner.startProcess(command, null);

				// sets the host to localhost
				host = "localhost";

				// waits (and yield) to allow the command to listen to the connection.
				try {
					Thread.sleep(description.getDelay());
				} catch (InterruptedException e) {
					// nothing to do
				}
			}

			RemoteRuntime runtime = new RemoteRuntime(host, description.getPort());
			// TODO check when the runtime should be initialized
			runtime.initializeRuntime();
			return runtime;
		} catch (IOException e) {
			e.printStackTrace();
			// TODO handle errors
			return null;
		}
	}

	
}
