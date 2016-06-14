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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.imagej.ops.Op;
import net.imagej.ops.features.sets.ComputerSet;
import net.imagej.ops.features.sets.tables.ComputerSetTableService;
import net.imagej.ops.features.sets.tables.DefaultTable;
import net.imagej.table.Column;
import net.imagej.table.Table;
import net.imglib2.type.Type;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * An IterableProcessor holds {@link ComputerSet}s and
 * {@link IterableComputerSetProcessor#compute1(Iterable, Table)}
 * computes the features on the given {@link Iterable} and returns a
 * {@link DefaultTable}.
 *
 * The {@link DefaultTable} has one row and as many columns as features were
 * calculated.
 *
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <I>
 *            Iterable type
 * @param <O>
 *            Output type of the features.
 */
@Plugin(type = Op.class)
public class IterableComputerSetProcessor<I, O extends Type<O>>
		extends AbstractUnaryComputerSetProcessor<Iterable<I>, Iterable<I>, O> {

	@Parameter
	private ComputerSetTableService<O> fs;

	/**
	 * Maps each {@link ComputerSet} to a unique name. This ensures unique
	 * column names in the {@link DefaultTable}.
	 */
	private Map<ComputerSet<?, O>, String> names;

	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	public Table<Column<O>, O> createOutput(final Iterable<I> input1) {
		names = ComputerSetProcessorUtils.getUniqueNames(Arrays.asList(sets));
		return fs.createTable(sets, names, 1);
	}

	@Override
	public void compute1(final Iterable<I> input1, final Table<Column<O>, O> output) {

		final List<Future<Void>> futures = new ArrayList<>();

		for (final ComputerSet<Iterable<I>, O> featureset : sets) {

			futures.add(es.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					final Map<String, O> result = featureset.compute1(input1);
					for (final String name : result.keySet()) {
						output.set(ComputerSetProcessorUtils.getFeatureTableName(names.get(featureset), name), 0,
								result.get(name));
					}
					return null;
				}
			}));
		}

		for (final Future<Void> future : futures) {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException exc) {
				throw new RuntimeException(exc);
			}
		}
	}

}
