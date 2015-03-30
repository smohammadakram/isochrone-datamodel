package datamodel.command;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * CommandUtils are used to wrap exceptions. For more information see {@link http://stackoverflow.com/questions/25643348/java-8-method-reference-unhandled-exception};
 */
final class CommandUtils {

	private CommandUtils() { }

	@FunctionalInterface
	public interface ConsumerWithExceptions<T> {
		void accept(T t) throws Exception;
	}

	@FunctionalInterface
	public interface FunctionWithExceptions<T, R> {
		R apply(T t) throws Exception;
	}

	@FunctionalInterface
	public interface SupplierWithExceptions<T> {
		T get() throws Exception;
	}

	@FunctionalInterface
	public interface RunnableWithExceptions {
		void accept() throws Exception;
	}

	public static <T> Consumer<T> rethrowConsumer(final ConsumerWithExceptions<T> consumer) {
		return t -> {
			try {
				consumer.accept(t);
			} catch (final Exception exception) {
				throwAsUnchecked(exception);
			}
		};
	}

	public static <T, R> Function<T, R> rethrowFunction(final FunctionWithExceptions<T, R> function) {
		return t -> {
			try {
				return function.apply(t);
			} catch (final Exception exception) {
				throwAsUnchecked(exception);
				return null;
			}
		};
	}

	public static <T> Supplier<T> rethrowSupplier(final SupplierWithExceptions<T> function) {
		return () -> {
			try {
				return function.get();
			} catch (final Exception exception) {
				throwAsUnchecked(exception);
				return null;
			}
		};
	}

	public static void uncheck(final RunnableWithExceptions t) {
		try {
			t.accept();
		} catch (final Exception exception) {
			throwAsUnchecked(exception);
		}
	}

	public static <R> R uncheck(final SupplierWithExceptions<R> supplier) {
		try {
			return supplier.get();
		} catch (final Exception exception) {
			throwAsUnchecked(exception);
			return null;
		}
	}

	public static <T, R> R uncheck(final FunctionWithExceptions<T, R> function, final T t) {
		try {
			return function.apply(t);
		} catch (final Exception exception) {
			throwAsUnchecked(exception);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static <E extends Throwable> void throwAsUnchecked(final Exception exception) throws E {
		throw (E) exception;
	}

}
