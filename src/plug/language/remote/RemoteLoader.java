package plug.language.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import plug.core.ILanguageLoader;
import plug.core.ILanguageRuntime;
import plug.language.remote.runtime.RemoteRuntime;

public class RemoteLoader implements ILanguageLoader {

	public RemoteLoader() {

	}

	@Override
	public ILanguageRuntime getRuntime(URI modelURI, Map<String, Object> options) {
		try {
			RemoteDescription description = new ObjectMapper().readValue(modelURI.toURL(), RemoteDescription.class);
			RemoteRuntime runtime = new RemoteRuntime(description.getHost(), description.getPort());
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
