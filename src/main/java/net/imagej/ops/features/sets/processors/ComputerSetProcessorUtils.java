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
package net.imagej.ops.features.sets.processors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.imagej.ops.features.sets.ComputerSet;
import net.imagej.ops.features.sets.DefaultFirstOrderStatsComputerSet;
import net.imagej.ops.features.sets.DefaultGeometric2DComputerSet;
import net.imagej.ops.features.sets.DefaultZernikeComputerSet;
import net.imagej.ops.geom.geom3d.mesh.Vertex;
import net.imglib2.RealPoint;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.roi.geometric.Polygon;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * A utility class to handle {@link ComputerSet} with a
 * {@link ComputerSetProcessor}.
 *
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class ComputerSetProcessorUtils {

	/**
	 * Add unique suffix if needed.
	 *
	 * @param names
	 *            already used names
	 * @param n
	 *            the new suggested name
	 * @return the unique name
	 */
	private static String uniqueName(final Collection<String> names, final String n) {

		if (!names.contains(n)) {
			return n;
		}

		int c = 0;

		while (names.contains(n + "[" + c + "]")) {
			c++;
		}

		return n + "[" + c + "]";
	}

	/**
	 * Concatenate featureset name with feature name.
	 * 
	 * @param featuresetName
	 *            the name of the featureset
	 * @param featureName
	 *            the name of the feature
	 * @return the concatenated string
	 */
	public static String getFeatureTableName(final String featuresetName, final String featureName) {
		return featuresetName + "_" + featureName;
	}

	/**
	 * Creates a map from {@link ComputerSet} to a unique String. If a
	 * {@link ComputerSet} has a duplicate the suffix "[i]" (i = 0,...) is
	 * added.
	 *
	 * @param featuresets
	 *            for which unique names are required
	 * @return map from featuresets to unique names
	 */
	public static <O> Map<ComputerSet<?, O>, String> getUniqueNames(final List<ComputerSet<?, O>> featuresets) {

		final Map<ComputerSet<?, O>, String> names = new HashMap<>();

		for (final ComputerSet<?, O> fs : featuresets) {
			final String n = uniqueName(names.values(), fs.getClass().getSimpleName());
			names.put(fs, n);
		}

		return names;
	}

	/**
	 * This method is used in {@link DefaultGeometric2DComputerSet} as input
	 * object which is needed for the matching.
	 *
	 * Note: This is just a workaround.
	 *
	 * @return polygon with two vertices.
	 */
	public static Polygon get2DPolygon() {
		final List<RealPoint> v = new ArrayList<>();

		v.add(new RealPoint(0, 0));
		v.add(new RealPoint(1, 1));
		return new Polygon(v);
	}

	/**
	 * This method is used in {@link DefaultFirstOrderStatsComputerSet} as input
	 * object which is needed for the matching.
	 *
	 * Note: This is just a workaround.
	 *
	 * @return an Iterable
	 */
	public static <T extends Type<T>> Iterable<T> getIterable() {
		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				// just a fake object
				return null;
			}
		};
	}

	/**
	 * This method is used in {@link DefaultZernikeComputerSet} as input object
	 * which is needed for the matching.
	 *
	 * Note: This is just a workaround.
	 *
	 * @return a 2D img
	 */
	public static <T extends Type<T>> ArrayImg<DoubleType, ?> get2DImg() {
		return new ArrayImgFactory<DoubleType>().create(new long[] { 2, 2 }, new DoubleType());
	}
}
