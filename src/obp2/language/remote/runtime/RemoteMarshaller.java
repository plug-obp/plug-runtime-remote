package obp2.language.remote.runtime;

import obp2.runtime.core.IMarshaller;
import obp2.runtime.core.defaults.DefaultLanguageService;

public class RemoteMarshaller
        extends DefaultLanguageService<Configuration, RemoteAction, byte[]>
        implements IMarshaller<Configuration, RemoteAction, byte[]> {

    public RemoteMarshaller(RemoteLanguageModule remoteLanguageModule) {
        super();
        this.setModule(remoteLanguageModule);
    }

    @Override
    public byte[] serializeConfiguration(Configuration configuration) {
        return configuration.state;
    }

    @Override
    public byte[] serializeFireable(RemoteAction fireable) {
        return fireable.data;
    }

    @Override
    public byte[] serializeOutput(byte[] output) {
        return output;
    }

    @Override
    public Configuration deserializeConfiguration(byte[] buffer) {
        return new Configuration(buffer);
    }

    @Override
    public RemoteAction deserializeFireable(byte[] buffer) {
        return new RemoteAction(buffer);
    }

    @Override
    public byte[] deserializeOutput(byte[] buffer) {
        return buffer;
    }
}
