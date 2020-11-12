package plug.language.remote;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import plug.core.IConfiguration;
import plug.core.IFiredTransition;
import plug.runtime.core.ITransitionRelation;
import plug.core.RuntimeDescription;
import plug.core.defaults.DefaultConfiguration;
import plug.language.remote.client.DistantRuntime;
import plug.language.remote.client.IRuntimeSerializer;
import plug.statespace.transitions.FiredTransition;

public class DistantTest {

	public static void main(String[] args) throws Exception {
		DistantRuntime distantRuntime = new DistantRuntime(1238, getRuntimeDescription(), getSerializer());
		distantRuntime.connect();

		while(distantRuntime.handleRequest()) {
			// Nothing to do here
		}

		distantRuntime.close();
	}


	protected static class NumberConfiguration extends DefaultConfiguration<NumberConfiguration>
			implements IConfiguration<NumberConfiguration> {

		public final int count;

		public NumberConfiguration(int count) {
			this.count = count;
		}

		@Override
		public NumberConfiguration createCopy() {
			return new NumberConfiguration(count);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			NumberConfiguration that = (NumberConfiguration) o;
			return count == that.count;
		}

		@Override
		public int hashCode() {
			return Objects.hash(count);
		}
	}

	protected static RuntimeDescription getRuntimeDescription() {
		return new RuntimeDescription(null, () -> new ITransitionRelation<NumberConfiguration, Integer>() {
			@Override
			public Set<NumberConfiguration> initialConfigurations() {
				return Collections.singleton(new NumberConfiguration(0));
			}

			@Override
			public Collection<Integer> fireableTransitionsFrom(NumberConfiguration source) {
				if (source.count <= 5) {
					return Collections.singleton(1);
				} else {
					return Collections.singleton(2);
				}
			}

			@Override
			public IFiredTransition<NumberConfiguration, Integer> fireOneTransition(NumberConfiguration source, Integer transition) {
				if (transition == 1) {
					return new FiredTransition<>(source, new NumberConfiguration(source.count +1), transition);
				} else {
					return new FiredTransition<>(source, new NumberConfiguration(0), transition);
				}
			}
		});
	}

	protected static IRuntimeSerializer getSerializer() {
		return new IRuntimeSerializer<NumberConfiguration, Integer>() {
			@Override
			public byte[] serializeConfiguration(NumberConfiguration configuration) {
				return ByteBuffer.allocate(4).putInt(configuration.count).array();
			}

			@Override
			public NumberConfiguration deserializeConfiguration(byte[] bytes) {
				return new NumberConfiguration(ByteBuffer.wrap(bytes).getInt());
			}

			@Override
			public byte[] serializeTransition(Integer transition) {
				return ByteBuffer.allocate(4).putInt(transition).array();
			}

			@Override
			public Integer deserializeTransition(byte[] bytes) {
				return ByteBuffer.wrap(bytes).getInt();
			}

			@Override
			public byte[] serializePayload(Object payload) {
				return new byte[] {1};
			}

			@Override
			public Object deserializePayload(byte[] bytes) {
				return 1;
			}
		};
	}
}
