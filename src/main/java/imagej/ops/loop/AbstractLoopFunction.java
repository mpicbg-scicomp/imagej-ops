
package imagej.ops.loop;

import imagej.ops.AbstractFunction;
import imagej.ops.Function;

import org.scijava.plugin.Parameter;

/**
 * Abstract implementation of a {@link LoopFunction}.
 * 
 * @author Christian Dietz
 */
public abstract class AbstractLoopFunction<F extends Function<I, I>, I> extends
	AbstractFunction<I, I> implements LoopFunction<I>
{

	/** Function to loop. */
	@Parameter
	protected Function<I, I> function;

	/** Buffer for intermediate results. */
	@Parameter(required = false)
	protected I buffer;

	/** Number of iterations. */
	@Parameter
	protected int n;

	@Override
	public Function<I, I> getFunction() {
		return function;
	}

	@Override
	public void setFunction(final Function<I, I> function) {
		this.function = function;
	}
}