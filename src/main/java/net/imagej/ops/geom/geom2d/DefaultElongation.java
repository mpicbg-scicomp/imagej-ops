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

package net.imagej.ops.geom.geom2d;

import net.imagej.ops.Ops;
import net.imagej.ops.special.chain.RTs;
import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imglib2.roi.geometric.Polygon;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.plugin.Plugin;

/**
 * Generic implementation of {@code geom.mainElongation}.
 * 
 * @author Daniel Seebacher (University of Konstanz)
 */
@Plugin(type = Ops.Geometric.MainElongation.class, label = "Geometric (2D): Elongation")
public class DefaultElongation extends AbstractUnaryHybridCF<Polygon, DoubleType>
		implements Ops.Geometric.MainElongation {

	private UnaryFunctionOp<Polygon, DoubleType> minorAxisFunc;
	private UnaryFunctionOp<Polygon, DoubleType> majorAxisFunc;

	@Override
	public void initialize() {
		minorAxisFunc = RTs.function(ops(), Ops.Geometric.MinorAxis.class, in());
		majorAxisFunc = RTs.function(ops(), Ops.Geometric.MajorAxis.class, in());
	}

	@Override
	public void compute1(final Polygon input, final DoubleType output) {
		output.set(1d - minorAxisFunc.compute1(input).getRealDouble() / majorAxisFunc.compute1(input).getRealDouble());
	}

	@Override
	public DoubleType createOutput(Polygon input) {
		return new DoubleType();
	}

}
