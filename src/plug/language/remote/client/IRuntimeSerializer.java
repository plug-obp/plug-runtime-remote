package plug.language.remote.client;

import plug.core.IConfiguration;

public interface IRuntimeSerializer<C extends IConfiguration<C>, T extends Object> {
	byte[] serializeConfiguration(C configuration);

	C deserializeConfiguration(byte[] bytes);

	byte[] serializeTransition(T transition);

	T deserializeTransition(byte[] bytes);

	byte[] serializePayload(Object payload);

	Object deserializePayload(byte[] bytes);
}
