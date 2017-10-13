package plug.language.remote.driver;

import plug.core.IFiredTransition;
import plug.language.remote.runtime.Configuration;
import plug.language.remote.runtime.FireableTransition;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Ciprian TEODOROV on 08/09/17.
 */
public abstract class AbstractDriver {
    public abstract void connect();
    public abstract void disconnect();
    public abstract Set<Configuration> initialConfigurations();
    public abstract Collection<FireableTransition> fireableTransitionsFrom(Configuration configuration);
    public abstract Set<Configuration> fireOneTransition(Configuration source, FireableTransition toFire);
}
