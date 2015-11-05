package net.imagej.ops.coloc.icq;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractOp;
import net.imagej.ops.Contingent;
import net.imagej.ops.Ops;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;

/**
 * This algorithm calculates Li et al.'s ICQ (intensity
 * correlation quotient).
 *
 * @param <T>
 */
@Plugin(type=Ops.Coloc.ICQ.class)
public class LiICQ<T extends RealType< T >> extends AbstractOp implements Contingent {
	/** the resulting ICQ value. */
	@Parameter(type=ItemIO.OUTPUT)
	private double icqValue;

	@Parameter
	private IterableInterval<T> img1;
	
	@Parameter
	private IterableInterval<T> img2;
	
	@Parameter
	private double mean1;
	
	@Parameter
	private double mean2;
	
	@Override
	public void run() {
		// variables to count the positive and negative results
		// of Li's product of the difference of means.
		long numPositiveProducts = 0;
		long numNegativeProducts = 0;
		// iterate over image
		Cursor<T> cursor1 = img1.cursor();
		Cursor<T> cursor2 = img2.cursor();
		
		while (cursor1.hasNext()) {
			
			cursor1.fwd();
			cursor2.fwd();
			double ch1 = cursor1.get().getRealDouble();
			double ch2 = cursor2.get().getRealDouble();

			double productOfDifferenceOfMeans = (mean1 - ch1) * (mean2 - ch2);

			// check for positive and negative values
			if (productOfDifferenceOfMeans < 0.0 )
				++numNegativeProducts;
			else
				++numPositiveProducts;
		}

		/* calculate Li's ICQ value by dividing the amount of "positive pixels" to the
		 * total number of pixels. Then shift it in the -0.5,0.5 range.
		 */
		icqValue = ( (double) numPositiveProducts / (double) (numNegativeProducts + numPositiveProducts) ) - 0.5;
	}


	@Override
	public boolean conforms() {
		return Intervals.equalDimensions(img1, img2) && img1.iterationOrder().equals(img2.iterationOrder());
	}

}
