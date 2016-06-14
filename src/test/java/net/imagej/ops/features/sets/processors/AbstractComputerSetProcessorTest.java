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

import java.util.List;
import java.util.Map;

import net.imagej.ops.features.AbstractFeatureTest;
import net.imagej.ops.features.sets.ComputerSet;
import net.imagej.table.Column;
import net.imagej.table.Table;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Assert;

/**
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public abstract class AbstractComputerSetProcessorTest extends AbstractFeatureTest {


	protected void checkAllResultTableForOneComputerSet(
			Table<Column<DoubleType>, DoubleType> result, List<ComputerSet<?, DoubleType>> tmp) {
		String computerSetName = ComputerSetProcessorUtils.getUniqueNames(tmp).get(tmp.get(0));

		String[] names = tmp.get(0).getComputerNames();

		for (int i = 0; i < result.getColumnCount(); i++) {
			String tmpName = computerSetName + "_" + names[i];
			Assert.assertTrue("Wrong column order.", tmpName.equals(result.get(i).getHeader()));
		}
	}
	

	protected void checkResultTableForManyComputerSets(Table<Column<DoubleType>, DoubleType> result,
			List<ComputerSet<?, DoubleType>> tmp) {
		Map<ComputerSet<?, DoubleType>, String> computerSetNames = ComputerSetProcessorUtils.getUniqueNames(tmp);

		int i = 0;
		for (ComputerSet<?, DoubleType> set : tmp) {
			String computerSetName = computerSetNames.get(set);
			String[] names = set.getComputerNames();
			for (String s : names) {
				String tmpName = computerSetName + "_" + s;
				Assert.assertTrue("Wrong column order.", tmpName.equals(result.get(i++).getHeader()));
			}
		}
	}
}
