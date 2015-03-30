package datamodel.command;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * CommandUtils are used to wrap exceptions.
 * For more information see {@link http://stackoverflow.com/questions/25643348/java-8-method-reference-unhandled-exception};
 */
final class CommandUtils {

	@FunctionalInterface
	public interface Consumer_WithExceptions<T> {
	    void accept(T t) throws Exception;
	}

	@FunctionalInterface
	public interface Function_WithExceptions<T, R> {
		R apply(T t) throws Exception;
	}

	@FunctionalInterface
	public interface Supplier_WithExceptions<T> {
	    T get() throws Exception;
    }

	@FunctionalInterface
	public interface Runnable_WithExceptions {
	    void accept() throws Exception;
    }

	/** .forEach(rethrowConsumer(name -> System.out.println(Class.forName(name)))); or .forEach(rethrowConsumer(ClassNameUtil::println)); */
	public static <T> Consumer<T> rethrowConsumer(final Consumer_WithExceptions<T> consumer) {
	    return t -> {
		    try { consumer.accept(t); }
		    catch (final Exception exception) { throwAsUnchecked(exception); }
	    };
    }

	/** .map(rethrowFunction(name -> Class.forName(name))) or .map(rethrowFunction(Class::forName)) */
	public static <T, R> Function<T, R> rethrowFunction(final Function_WithExceptions<T, R> function) {
	    return t -> {
		    try { return function.apply(t); }
		    catch (final Exception exception) { throwAsUnchecked(exception); return null; }
	    };
    }

	/** rethrowSupplier(() -> new StringJoiner(new String(new byte[]{77, 97, 114, 107}, "UTF-8"))), */
	public static <T> Supplier<T> rethrowSupplier(final Supplier_WithExceptions<T> function) {
	    return () -> {
	    	try { return function.get(); }
	    	catch (final Exception exception) { throwAsUnchecked(exception); return null; }
	    };
    }

	/** uncheck(() -> Class.forName("xxx")); */
	public static void uncheck(final Runnable_WithExceptions t) {
	    try { t.accept(); }
	    catch (final Exception exception) { throwAsUnchecked(exception); }
	}

	/** uncheck(() -> Class.forName("xxx")); */
	public static <R> R uncheck(final Supplier_WithExceptions<R> supplier) {
	    try { return supplier.get(); }
	    catch (final Exception exception) { throwAsUnchecked(exception); return null; }
    }

	/** uncheck(Class::forName, "xxx"); */
	public static <T, R> R uncheck(final Function_WithExceptions<T, R> function, final T t) {
	    try { return function.apply(t); }
	    catch (final Exception exception) { throwAsUnchecked(exception); return null; }
    }

	@SuppressWarnings ("unchecked")
	private static <E extends Throwable> void throwAsUnchecked(final Exception exception) throws E { throw (E)exception; }
}
