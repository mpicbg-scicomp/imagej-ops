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

package net.imagej.ops.map;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imagej.ops.AbstractOpTest;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.features.AbstractFeatureTest;
import net.imagej.ops.map.MapUnaryComputers.IIToII;
import net.imagej.ops.map.MapUnaryComputers.IIToIIParallel;
import net.imagej.ops.slice.SlicesII;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.inplace.BinaryInplaceOp;
import net.imagej.ops.special.inplace.Inplaces;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import ij.io.Opener;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.scijava.io.IOService;
import org.scijava.plugin.Parameter;

/**
 * Tests for {@link MapOp}s that are not covered in the auto generated tests.
 *  
 * @author Leon Yang
 * @author Christian Dietz (University of Konstanz)
 */
public class MapTest extends AbstractOpTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Op sub;

	@Test
	public void testIterable() {
		final Img<ByteType> in = generateByteArrayTestImg(true, 10, 10);

		Op nullary = Computers.nullary(ops, Ops.Math.Zero.class, ByteType.class);
		ops.run(MapNullaryIterable.class, in, nullary);

		for (ByteType ps : in)
			assertEquals(ps.get(), 0);
	}

	@Test
	public void testII() {
		final Img<ByteType> in = generateByteArrayTestImg(true, 10, 10);

		Op nullary = Computers.nullary(ops, Ops.Math.Zero.class, ByteType.class);
		ops.run(MapNullaryII.class, in, nullary);

		for (ByteType ps : in)
			assertEquals(ps.get(), 0);
	}

	@Test
	public void testIIAndIIInplace() {
		final Img<ByteType> first = generateByteArrayTestImg(true, 10, 10);
		final Img<ByteType> firstCopy = first.copy();
		final Img<ByteType> second = generateByteArrayTestImg(false, 10, 10);
		for (ByteType px : second)
			px.set((byte) 1);
		final Img<ByteType> secondCopy = second.copy();
		final Img<ByteType> secondDiffDims = generateByteArrayTestImg(false, 10, 10,
			2);

		sub = Inplaces.binary(ops, Ops.Math.Subtract.class, ByteType.class,
			ByteType.class);
		final BinaryInplaceOp<Img<ByteType>> map = Inplaces.binary(ops,
			MapIIAndIIInplaceParallel.class, firstCopy, second, sub);
		map.run(firstCopy, second, firstCopy);
		map.run(first, secondCopy, secondCopy);

		assertImgSubEquals(first, second, firstCopy);
		assertImgSubEquals(first, second, secondCopy);

		// Expect exception when in2 has different dimensions
		thrown.expect(IllegalArgumentException.class);
		ops.op(MapIIAndIIInplace.class, first, secondDiffDims, sub);
	}

	@Test
	public void testIIAndIIInplaceParallel() {
		final Img<ByteType> first = generateByteArrayTestImg(true, 10, 10);
		final Img<ByteType> firstCopy = first.copy();
		final Img<ByteType> second = generateByteArrayTestImg(false, 10, 10);
		for (ByteType px : second)
			px.set((byte) 1);
		final Img<ByteType> secondCopy = second.copy();
		final Img<ByteType> secondDiffDims = generateByteArrayTestImg(false, 10, 10,
			2);

		sub = Inplaces.binary(ops, Ops.Math.Subtract.class, ByteType.class,
			ByteType.class);
		final BinaryInplaceOp<Img<ByteType>> map = Inplaces.binary(ops,
			MapIIAndIIInplaceParallel.class, firstCopy, second, sub);
		map.run(firstCopy, second, firstCopy);
		map.run(first, secondCopy, secondCopy);

		assertImgSubEquals(first, second, firstCopy);
		assertImgSubEquals(first, second, secondCopy);

		// Expect exception when in2 has different dimensions
		thrown.expect(IllegalArgumentException.class);
		ops.op(MapIIAndIIInplace.class, first, secondDiffDims, sub);
	}

	@Test
	public void testIIInplaceParallal() {
		final Img<ByteType> arg = generateByteArrayTestImg(true, 10, 10);
		final Img<ByteType> argCopy = arg.copy();

		sub = Inplaces.unary(ops, Ops.Math.Subtract.class, ByteType.class,
			ByteType.class, new ByteType((byte) 1));
		ops.run(MapIIInplaceParallel.class, argCopy, sub);

		assertImgSubOneEquals(arg, argCopy);
	}

	@Test
	public void testIterableInplace() {
		final Img<ByteType> arg = generateByteArrayTestImg(true, 10, 10);
		final Img<ByteType> argCopy = arg.copy();

		sub = Inplaces.unary(ops, Ops.Math.Subtract.class, ByteType.class,
			ByteType.class, new ByteType((byte) 1));
		ops.run(MapIterableInplace.class, argCopy, sub);

		assertImgSubOneEquals(arg, argCopy);
	}

	@Test
	public void testIterableToIterable() {
		final Img<ByteType> in = generateByteArrayTestImg(true, 10, 10);
		final Img<ByteType> out = generateByteArrayTestImg(false, 10, 10);

		sub = Computers.unary(ops, Ops.Math.Subtract.class, ByteType.class,
			ByteType.class, new ByteType((byte) 1));
		ops.run(MapIterableToIterable.class, out, in, sub);

		assertImgSubOneEquals(in, out);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testIIToIIParallel() {
		final String imageName = "confocal-series.tif";
		final Img<UnsignedByteType> in = ImageJFunctions.wrapByte(new Opener().openImage(
			MapTest.class.getResource(imageName).getPath()));
		
		Img<BitType> outLinear = null;
		Img<BitType> outParallel = null;
		try {
			outLinear = in.factory().imgFactory(new BitType()).create(in, new BitType());
			outParallel = in.factory().imgFactory(new BitType()).create(in, new BitType());
		}
		catch (IncompatibleTypeException exc) {
			// TODO Auto-generated catch block
			exc.printStackTrace();
		}
		UnaryComputerOp<RandomAccessibleInterval<UnsignedByteType>, RandomAccessibleInterval<BitType>> op = (UnaryComputerOp) ops.op(Ops.Threshold.Otsu.class, in);
		
		UnaryComputerOp<SlicesII<UnsignedByteType>, SlicesII<BitType>> mapperLinear =
			(UnaryComputerOp) Computers.unary(ops, IIToII.class, SlicesII.class,
				SlicesII.class, op);
		
		UnaryComputerOp<SlicesII<UnsignedByteType>, SlicesII<BitType>> mapperParallel =
				(UnaryComputerOp) Computers.unary(ops, IIToIIParallel.class, SlicesII.class,
					SlicesII.class, op);
		
		mapperLinear.compute1(new SlicesII<>(in, new int[]{0, 1}, true),
			new SlicesII<>(outLinear, new int[]{0, 1}, true));
		
		mapperParallel.compute1(new SlicesII<>(in, new int[]{0, 1}, true),
			new SlicesII<>(outParallel, new int[]{0, 1}, true));

		assertImgEquals(outLinear, outParallel);
	}

	// -- helper methods --

	private static <T> void assertImgEquals(Img<T> in,
		Img<T> out)
	{
		final Cursor<T> inCursor = in.cursor();
		final Cursor<T> outCursor = out.cursor();

		while (inCursor.hasNext()) {
			assertEquals(inCursor.next(), outCursor.next());
		}
	}
	
	private static void assertImgSubEquals(Img<ByteType> in1, Img<ByteType> in2,
		Img<ByteType> out)
	{
		final Cursor<ByteType> in1Cursor = in1.cursor();
		final Cursor<ByteType> in2Cursor = in2.cursor();
		final Cursor<ByteType> outCursor = out.cursor();

		while (in1Cursor.hasNext()) {
			assertEquals((byte) (in1Cursor.next().get() - in2Cursor.next().get()),
				outCursor.next().get());
		}
	}

	private static void assertImgSubOneEquals(Img<ByteType> in,
		Img<ByteType> out)
	{
		final Cursor<ByteType> in1Cursor = in.cursor();
		final Cursor<ByteType> outCursor = out.cursor();

		while (in1Cursor.hasNext()) {
			assertEquals((byte) (in1Cursor.next().get() - 1), outCursor.next().get());
		}
	}

}
