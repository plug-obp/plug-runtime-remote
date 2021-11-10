package obp2.language.remote.runtime;

import obp2.runtime.core.LanguageModule;

import java.io.IOException;

public class RemoteLanguageModule extends LanguageModule<Configuration, RemoteAction, byte[]> {

    public RemoteLanguageModule(String host, int port) {
        super();
        RemoteTransitionRelation transitionRelation = new RemoteTransitionRelation(this, host, port);
        try {
            transitionRelation.initializeRuntime();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.transitionRelation = transitionRelation;
        this.atomicPropositionsEvaluator = new RemoteAtomicPropositionsEvaluator(this, transitionRelation.getDriver());
        this.treeProjector = new RemoteRuntimeView(this);
        this.marshaller = new RemoteMarshaller(this);
    }

    @Override
    public void close() throws Exception {
        ((RemoteTransitionRelation)getTransitionRelation()).close();
    }
}
