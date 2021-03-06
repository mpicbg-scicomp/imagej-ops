/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2014 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, University of Konstanz and Brian Northan.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.ops.geom.geom3d;

import net.imagej.ops.Contingent;
import net.imagej.ops.Ops;
import net.imagej.ops.special.function.Functions;
import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imglib2.roi.IterableRegion;
import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

/**
 * Generic implementation of {@link net.imagej.ops.Ops.Geometric.Spareness}.
 * 
 * @author Tim-Oliver Buchholz (University of Konstanz)
 */
@Plugin(type = Ops.Geometric.Spareness.class,
	label = "Geometric (3D): Spareness", priority = Priority.VERY_HIGH_PRIORITY)
public class DefaultSpareness<B extends BooleanType<B>> extends
	AbstractUnaryHybridCF<IterableRegion<B>, DoubleType> implements
	Ops.Geometric.Spareness, Contingent
{

	private UnaryFunctionOp<IterableRegion<B>, DoubleType> mainElongation;

	private UnaryFunctionOp<IterableRegion<B>, DoubleType> medianElongation;

	private UnaryFunctionOp<IterableRegion<B>, CovarianceOf2ndMultiVariate3D> multivar;

	private UnaryFunctionOp<IterableRegion<B>, DoubleType> volume;

	@Override
	public void initialize() {
		mainElongation = Functions.unary(ops(), Ops.Geometric.MainElongation.class, DoubleType.class,
			in());
		medianElongation = Functions.unary(ops(), Ops.Geometric.MedianElongation.class, DoubleType.class,
			in());
		multivar = Functions.unary(ops(), DefaultSecondMultiVariate3D.class,
			CovarianceOf2ndMultiVariate3D.class, in());
		volume = Functions.unary(ops(), Ops.Geometric.Size.class, DoubleType.class, in());
	}

	@Override
	public void compute1(final IterableRegion<B> input, final DoubleType output) {

		double r1 = Math.sqrt(5.0 * multivar.compute1(input).getEigenvalue(0));
		double r2 = r1 / mainElongation.compute1(input).get();
		double r3 = r2 / medianElongation.compute1(input).get();

		double volumeEllipsoid = (4.18879 * r1 * r2 * r3);

		output.set(volume.compute1(input).get() / volumeEllipsoid);
	}
	
	@Override
	public DoubleType createOutput(IterableRegion<B> input) {
		return new DoubleType();
	}

	@Override
	public boolean conforms() {
		return in().numDimensions() == 3;
	}

}
