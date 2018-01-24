package plug.language.remote.driver;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import plug.language.remote.runtime.Configuration;
import plug.language.remote.runtime.FireableTransition;

/**
 * Created by Ciprian TEODOROV on 08/09/17.
 */
public abstract class AbstractDriver {
    public abstract void connect() throws IOException;
    public abstract void disconnect();
    public abstract Set<Configuration> initialConfigurations();
    public abstract Collection<FireableTransition> fireableTransitionsFrom(Configuration configuration);
    public abstract Set<Configuration> fireOneTransition(Configuration source, FireableTransition toFire);

    public abstract void registerAtomicPropositions(String[] atomicPropositions);
    public abstract boolean[] getAtomicPropositionValuations(Configuration source);
}
