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

package net.imagej.ops.morphology;

import java.util.List;

import net.imagej.ops.AbstractNamespace;
import net.imagej.ops.Namespace;
import net.imagej.ops.OpMethod;
import net.imagej.ops.Ops;
import net.imagej.ops.filter.gauss.DefaultGaussRAI;
import net.imagej.ops.filter.gauss.GaussRAISingleSigma;
import net.imagej.ops.morphology.thinning.strategies.ThinningStrategy;
import net.imagej.ops.special.UnaryComputerOp;
import net.imagej.ops.special.UnaryFunctionOp;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;

import org.scijava.plugin.Plugin;

/**
 * The morphology namespace contains morphological operations.
 *
 * @author Curtis Rueden
 */
@Plugin(type = Namespace.class)
public class MorphologyNamespace extends AbstractNamespace {

	// -- close --

	@OpMethod(op = net.imagej.ops.Ops.Morphology.Close.class)
	public Object close(final Object... args) {
		return ops().run(net.imagej.ops.Ops.Morphology.Close.class, args);
	}

	// -- dilate --

	@OpMethod(op = net.imagej.ops.Ops.Morphology.Dilate.class)
	public Object dilate(final Object... args) {
		return ops().run(net.imagej.ops.Ops.Morphology.Dilate.class, args);
	}

	// -- erode --

	@OpMethod(op = net.imagej.ops.Ops.Morphology.Erode.class)
	public Object erode(final Object... args) {
		return ops().run(net.imagej.ops.Ops.Morphology.Erode.class, args);
	}

	// -- open --

	@OpMethod(op = net.imagej.ops.Ops.Morphology.Open.class)
	public Object open(final Object... args) {
		return ops().run(net.imagej.ops.Ops.Morphology.Open.class, args);
	}

	// -- thinning --

	@OpMethod(op = net.imagej.ops.Ops.Morphology.Thinning.class)
	public Object thinning(final Object... args) {
		return ops().run(net.imagej.ops.Ops.Morphology.Thinning.class, args);
	}

	@OpMethod(op = net.imagej.ops.morphology.thinning.ThinningOp.class)
	public RandomAccessibleInterval<BitType> thinning(final RandomAccessibleInterval<BitType> out, final RandomAccessibleInterval<BitType> in, final boolean m_foreground, final boolean m_background, final ThinningStrategy m_strategy) {
		final RandomAccessibleInterval<BitType> result =
			(RandomAccessibleInterval<BitType>) ops().run(net.imagej.ops.morphology.thinning.ThinningOp.class, out, in, m_foreground, m_background, m_strategy);
		return result;
	}	
	
	// -- Namespace methods --

	@Override
	public String getName() {
		return "morphology";
	}

}
