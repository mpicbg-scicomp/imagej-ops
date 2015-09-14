/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2014 - 2015 Board of Regents of the University of
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
package net.imagej.ops.features.haralick;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops.Haralick;
import net.imagej.ops.Ops.Haralick.ClusterShade;
import net.imagej.ops.features.haralick.helper.CoocMeanX;
import net.imagej.ops.features.haralick.helper.CoocMeanY;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Implementation of Cluster Shade Haralick Feature
 * 
 * @author Andreas Graumann, University of Konstanz
 * @author Christian Dietz, University of Konstanz
 *
 */
@Plugin(type = HaralickFeature.class, label = "Haralick: Cluster Shade", name = Haralick.ClusterShade.NAME)
public class DefaultClusterShade<T extends RealType<T>> extends
		AbstractHaralickFeature<T> implements ClusterShade {

	@Parameter
	private OpService ops;

	@Override
	public void compute(final IterableInterval<T> input, final DoubleType output) {
		final double[][] matrix = getCooccurrenceMatrix(input);

		final double mux = ((DoubleType) ops.run(CoocMeanX.class,
				(Object) matrix)).getRealDouble();

		final double muy = ((DoubleType) ops.run(CoocMeanY.class,
				(Object) matrix)).getRealDouble();

		double res = 0;
		for (int j = 0; j < matrix.length; j++) {
			for (int i = 0; i < matrix.length; i++) {
				res += (Math.pow((i + j - mux - muy), 3) * matrix[j][i]);
			}
		}
		output.setReal(res);
	}

}