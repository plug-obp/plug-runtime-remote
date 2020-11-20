package obp2.language.remote.runtime;

import obp2.runtime.core.LanguageModule;

import java.io.IOException;

public class RemoteLanguageModule extends LanguageModule<Configuration, RemoteAction, byte[]> {

    public RemoteLanguageModule(String host, int port) {
        RemoteTransitionRelation transitionRelation = new RemoteTransitionRelation(host, port);
        try {
            transitionRelation.initializeRuntime();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        RemoteAtomicPropositionsEvaluator atomicPropositionsEvaluator = new RemoteAtomicPropositionsEvaluator(transitionRelation.getDriver());

        this.transitionRelation = transitionRelation;
        this.atomicPropositionsEvaluator = atomicPropositionsEvaluator;
        this.treeProjector = new RemoteRuntimeView();
        this.transitionRelation.setModule(this);
        this.atomicPropositionsEvaluator.setModule(this);
        this.atomicPropositionsProvider.setModule(this);
        this.marshaller.setModule(this);
        this.treeProjector.setModule(this);
    }

    @Override
    public void close() throws Exception {
        ((RemoteTransitionRelation)getTransitionRelation()).close();
    }
}
