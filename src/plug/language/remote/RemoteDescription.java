package plug.language.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoteDescription {

	private final String host;
	private final String command;
	private final int port;

	public RemoteDescription(
			@JsonProperty("host")
			String host,
			@JsonProperty("command")
			String command,
			@JsonProperty("port")
			int port)
	{
		this.host = host;
		this.command = command;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public String getCommand() {
		return command;
	}

	public int getPort() {
		return port;
	}
}
