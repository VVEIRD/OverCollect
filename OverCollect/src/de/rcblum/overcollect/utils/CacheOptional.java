package de.rcblum.overcollect.utils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Optional that expires its value after a certain time it has not been accessed.
 * @author wkiv894
 *
 * @param <T>
 */
public class CacheOptional<T>
{
	 /**
     * Common instance for {@code empty()}.
     */
    private static final CacheOptional<?> EMPTY = new CacheOptional<>();

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private T value;
    
    /**
     * Lock for multitheaded access
     */
    private final Object valueLock = new Object();
    
    /**
     * Default cache lifetime is 10 minutes
     */
    private final long cacheLifetime;
    
    /**
     * Timer that triggers the expiration. Is reset every time the 
     */
    private Timer t = null; 

    /**
     * Constructs an empty instance.
     *
     * @implNote Generally only one empty instance, {@link CacheOptional#EMPTY},
     * should exist per VM.
     */
    private CacheOptional() {
    	this.value = null;
    	this.cacheLifetime = 600_000;
    }

    /**
     * Returns an empty {@code CacheOptional} instance.  No value is present for this
     * CacheOptional.
     *
     * @apiNote Though it may be tempting to do so, avoid testing if an object
     * is empty by comparing with {@code ==} against instances returned by
     * {@code Option.empty()}. There is no guarantee that it is a singleton.
     * Instead, use {@link #isPresent()}.
     *
     * @param <T> Type of the non-existent value
     * @return an empty {@code CacheOptional}
     */
    public static<T> CacheOptional<T> empty() {
        @SuppressWarnings("unchecked")
        CacheOptional<T> t = (CacheOptional<T>) EMPTY;
        return t;
    }

    /**
     * Constructs an instance with the value present.
     *
     * @param value the non-null value to be present
     * @throws NullPointerException if value is null
     */
    private CacheOptional(T value) {
        this(value, 600_000);
    }

    /**
     * Constructs an instance with the value present.
     *
     * @param value the non-null value to be present
     * @throws NullPointerException if value is null
     */
    private CacheOptional(T value, long cacheLifetime) {
        this.cacheLifetime = cacheLifetime;
        this.value = value;
        this.t = new Timer(true);
        t.schedule(new ExpireTask(), this.cacheLifetime);
    }

    /**
     * Returns an {@code CacheOptional} with the specified present non-null value.
     *
     * @param <T> the class of the value
     * @param value the value to be present, which must be non-null
     * @return an {@code CacheOptional} with the value present
     * @throws NullPointerException if value is null
     */
    public static <T> CacheOptional<T> of(T value) {
        return new CacheOptional<>(value);
    }

    /**
     * Returns an {@code CacheOptional} with the specified present non-null value.
     *
     * @param <T> the class of the value
     * @param value the value to be present, which must be non-null
     * @return an {@code CacheOptional} with the value present
     * @throws NullPointerException if value is null
     */
    public static <T> CacheOptional<T> of(T value, long expireTime) {
        return new CacheOptional<>(value, expireTime);
    }

    /**
     * Returns an {@code CacheOptional} describing the specified value, if non-null,
     * otherwise returns an empty {@code CacheOptional}.
     *
     * @param <T> the class of the value
     * @param value the possibly-null value to describe
     * @return an {@code CacheOptional} with a present value if the specified value
     * is non-null, otherwise an empty {@code CacheOptional}
     */
    public static <T> CacheOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * If a value is present in this {@code CacheOptional}, returns the value,
     * otherwise throws {@code NoSuchElementException}.
     *
     * @return the non-null value held by this {@code CacheOptional}
     * @throws NoSuchElementException if there is no value present
     *
     * @see CacheOptional#isPresent()
     */
    public T get() {
    	synchronized(valueLock) {
	        if (value == null) {
	            throw new NoSuchElementException("No value present");
	        }
	   		resetTimer();
	        return value;
    	}
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
    	synchronized(valueLock) {
	    	if (this.value != null)
	    		resetTimer();
	        return value != null;
    	}
    }

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     * null
     */
    public void ifPresent(Consumer<? super T> consumer) {
    	synchronized(valueLock) {
	    	if (this.value != null)
	    		resetTimer();
	        if (value != null)
	            consumer.accept(value);
    	}
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * return an {@code CacheOptional} describing the value, otherwise return an
     * empty {@code CacheOptional}.
     *
     * @param predicate a predicate to apply to the value, if present
     * @return an {@code CacheOptional} describing the value of this {@code CacheOptional}
     * if a value is present and the value matches the given predicate,
     * otherwise an empty {@code CacheOptional}
     * @throws NullPointerException if the predicate is null
     */
    public CacheOptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        synchronized(valueLock) {
	        if (!isPresent())
	            return this;
	        else
	            return predicate.test(value) ? this : empty();
        }
    }

    /**
     * If a value is present, apply the provided mapping function to it,
     * and if the result is non-null, return an {@code CacheOptional} describing the
     * result.  Otherwise return an empty {@code CacheOptional}.
     *
     * @apiNote This method supports post-processing on CacheOptional values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of file names, selects one that has
     * not yet been processed, and then opens that file, returning an
     * {@code CacheOptional<FileInputStream>}:
     *
     * <pre>{@code
     *     CacheOptional<FileInputStream> fis =
     *         names.stream().filter(name -> !isProcessedYet(name))
     *                       .findFirst()
     *                       .map(name -> new FileInputStream(name));
     * }</pre>
     *
     * Here, {@code findFirst} returns an {@code CacheOptional<String>}, and then
     * {@code map} returns an {@code CacheOptional<FileInputStream>} for the desired
     * file if one exists.
     *
     * @param <U> The type of the result of the mapping function
     * @param mapper a mapping function to apply to the value, if present
     * @return an {@code CacheOptional} describing the result of applying a mapping
     * function to the value of this {@code CacheOptional}, if a value is present,
     * otherwise an empty {@code CacheOptional}
     * @throws NullPointerException if the mapping function is null
     */
    public<U> CacheOptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        synchronized(valueLock) {
	        if (!isPresent())
	            return empty();
	        else 
	            return CacheOptional.ofNullable(mapper.apply(value));
        }
    }

    /**
     * If a value is present, apply the provided {@code CacheOptional}-bearing
     * mapping function to it, return that result, otherwise return an empty
     * {@code CacheOptional}.  This method is similar to {@link #map(Function)},
     * but the provided mapper is one whose result is already an {@code CacheOptional},
     * and if invoked, {@code flatMap} does not wrap it with an additional
     * {@code CacheOptional}.
     *
     * @param <U> The type parameter to the {@code CacheOptional} returned by
     * @param mapper a mapping function to apply to the value, if present
     *           the mapping function
     * @return the result of applying an {@code CacheOptional}-bearing mapping
     * function to the value of this {@code CacheOptional}, if a value is present,
     * otherwise an empty {@code CacheOptional}
     * @throws NullPointerException if the mapping function is null or returns
     * a null result
     */
    public<U> CacheOptional<U> flatMap(Function<? super T, CacheOptional<U>> mapper) {
        Objects.requireNonNull(mapper);
        synchronized(valueLock) {
	        if (!isPresent())
	            return empty();
	        else
	            return Objects.requireNonNull(mapper.apply(value));
        }
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present, may
     * be null
     * @return the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
    	synchronized(valueLock) {
	    	if (this.value != null)
	    		resetTimer();
	        return value != null ? value : other;
    	}
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code Supplier} whose result is returned if no value
     * is present
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is
     * null
     */
    public T orElseGet(Supplier<? extends T> other) {
    	synchronized(valueLock) {
	    	if (this.value != null)
	    		resetTimer();
	        return value != null ? value : other.get();
    	}
    }

    private void resetTimer() {
        t.cancel();
        t = new Timer(true);
        t.schedule(new ExpireTask(), this.cacheLifetime);
	}

	/**
     * Return the contained value, if present, otherwise throw an exception
     * to be created by the provided supplier.
     *
     * @apiNote A method reference to the exception constructor with an empty
     * argument list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to
     * be thrown
     * @return the present value
     * @throws X if there is no value present
     * @throws NullPointerException if no value is present and
     * {@code exceptionSupplier} is null
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    	synchronized(valueLock) {
	    	if (this.value != null)
	    		resetTimer();
	        if (value != null) {
	            return value;
	        } else {
	            throw exceptionSupplier.get();
	        }
    	}
    }

    /**
     * Indicates whether some other object is "equal to" this CacheOptional. The
     * other object is considered equal if:
     * <ul>
     * <li>it is also an {@code CacheOptional} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object
     * otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
    	synchronized(valueLock) {
	    	if (this.value != null)
	    		resetTimer();
	        if (this == obj) {
	            return true;
	        }
	
	        if (!(obj instanceof CacheOptional)) {
	            return false;
	        }
	
	        CacheOptional<?> other = (CacheOptional<?>) obj;
	        return Objects.equals(value, other.value);
    	}
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    @Override
    public int hashCode() {
    	synchronized(valueLock) {
	    	if (this.value != null)
	    		resetTimer();
	        return Objects.hashCode(value);
    	}
    }

    /**
     * Returns a non-empty string representation of this CacheOptional suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     *
     * @implSpec If a value is present the result must include its string
     * representation in the result. Empty and present CacheOptionals must be
     * unambiguously differentiable.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
    	synchronized(valueLock) {
	    	if (this.value != null)
	    		resetTimer();
	        return value != null
	            ? String.format("Optional[%s]", value)
	            : "Optional.empty";
    	}
    }
    
    private class ExpireTask extends TimerTask
    {
		@Override
		public void run() 
		{
			synchronized(valueLock) {
				value = null;
			}
		}
    	
    }
}
