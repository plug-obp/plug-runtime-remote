package obp2.language.remote.driver;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import obp2.core.IFiredTransition;
import obp2.language.remote.runtime.Configuration;
import obp2.runtime.core.TreeItem;
import obp2.language.remote.runtime.FireableTransition;

/**
 * Created by Ciprian TEODOROV on 08/09/17.
 */
public abstract class AbstractDriver {
    public abstract void connect() throws IOException;
    public abstract void disconnect();
    public abstract Set<Configuration> initialConfigurations();
    public abstract Collection<FireableTransition> fireableTransitionsFrom(Configuration configuration);
    public abstract IFiredTransition<Configuration, FireableTransition> fireOneTransition(Configuration source, FireableTransition toFire);

    public abstract int[] registerAtomicPropositions(String[] atomicPropositions) throws Exception;
    public abstract boolean[] getAtomicPropositionValuations(Configuration source);
    public abstract boolean[] getAtomicPropositionValuations(Configuration source, FireableTransition fireable, Object payload, Configuration target);


    public abstract List<TreeItem> getConfigurationItems(Configuration value);

    public String getConfigurationDescription(Configuration value) {
        return Integer.toHexString(value.hashCode());
    }

    public abstract String getFireableTransitionDescription(FireableTransition transition);
}
