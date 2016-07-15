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

package org.scijava.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.imagej.ops.AbstractOp;
import net.imagej.ops.AbstractOpTest;
import net.imagej.ops.Contingent;
import net.imagej.ops.Op;
import net.imagej.ops.OpCandidate;
import net.imagej.ops.OpRef;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Test;
import org.scijava.InstantiableException;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Tests {@link Nil}.
 * 
 * @author Curtis Rueden
 */
public class NilTest extends AbstractOpTest {

	@Test
	public <Q extends RealType<Q>> void testType() {
		final Nil<Img<Q>> nilImg = new Nil<Img<Q>>() {};
		assertEquals("net.imglib2.img.Img<Q>", nilImg.getType().getTypeName());
		final ParameterizedType pType = (ParameterizedType) nilImg.getType();
		final Type typeVar = ((TypeVariable<?>) pType.getActualTypeArguments()[0]).getBounds()[0];
		assertEquals("net.imglib2.type.numeric.RealType<Q>", typeVar.getTypeName());
	}

	@SuppressWarnings("cast")
	@Test
	public <N extends Number> void testProxy() {
		final List<N> listProxy = new Nil<List<N>>() {}.proxy();
		assertTrue(listProxy instanceof Iterable);
		assertTrue(listProxy instanceof Collection);
		assertTrue(listProxy instanceof List);
		assertTrue(listProxy instanceof GenericTyped);
		final GenericTyped genericTyped = (GenericTyped) listProxy;
		assertEquals("java.util.List<N>", genericTyped.getType().getTypeName());
		final ParameterizedType pType = (ParameterizedType) genericTyped.getType();
		final TypeVariable<?> nVar = (TypeVariable<?>) pType.getActualTypeArguments()[0];
		final Type qBounds = nVar.getBounds()[0];
		assertEquals("java.lang.Number", qBounds.getTypeName());
	}

	@Test
	public <N extends Number> void testCallbacks() {
		final Nil<List<N>> listNil = new Nil<List<N>>() {};
		final List<N> listProxy = listNil.proxy();
		assertEquals(0, listProxy.size()); // default method return value is 0/null
		assertNull(listProxy.iterator());

		final Nil<List<N>> listNil2 = new Nil<List<N>>() {
			@SuppressWarnings("unused")
			public int size() { return 21; }
		};
		final List<N> listProxy2 = listNil2.proxy();
		assertEquals(21, listProxy2.size()); // overridden method behavior

		final Collection<?> cProxy2 = new Nil<Collection<?>>(listProxy2) {}.proxy();
		assertEquals(21, cProxy2.size()); // behavior preserved by wrapping proxy

		final Collection<?> cProxy3 = new Nil<Collection<?>>(listNil2) {}.proxy();
		assertEquals(21, cProxy3.size()); // behavior preserved by wrapping nil
	}

	@Test
	public void testMatchNil() throws InstantiableException {
		final Nil<Img<FloatType>> nilImg = new Nil<Img<FloatType>>() {
			// NB: This method _only_ applies when proxy() is called!
			@SuppressWarnings("unused")
			public int numDimensions() { return 3; }
		};
		OpRef ref = OpRef.create(ImgOp.class, nilImg);
		final List<OpRef> refs = Collections.singletonList(ref);

		// find candidates with matching name & type
		final List<OpCandidate> candidates = matcher.findCandidates(ops, refs);
		assertEquals(1, candidates.size());
		final OpCandidate candidate = candidates.get(0);
		assertSame(ImgOp.class, candidate.getRef().getTypes().iterator().next());
		assertSame(FloatImgOp.class, candidate.cInfo().loadClass());

		// narrow down candidates to the exact matches
		final List<OpCandidate> matches = matcher.filterMatches(candidates);
		assertEquals(1, matches.size());
	}

	@Test
	public <S extends RealType<S>> void testMatchProxy()
		throws InstantiableException
	{
		final Img<S> nilImg = new Nil<Img<S>>() {
			@SuppressWarnings("unused")
			public int numDimensions() { return 3; }
		}.proxy();

		final OpRef ref = OpRef.create(ImgOp.class, nilImg);
		final OpCandidate result = matcher.findMatch(ops, ref);

		assertSame(ImgOp.class, result.opInfo().getType());
		assertSame(FloatImgOp.class, result.cInfo().loadClass());
	}

	public interface ImgOp extends Op {
		// NB: Marker interface.
	}

	@Plugin(type = ImgOp.class)
	public static class FloatImgOp extends AbstractOp implements ImgOp, Contingent {
		@Parameter
		private Img<FloatType> img;
		@Override
		public void run() {
			// NB: No implementation needed.
		}
		@Override
		public boolean conforms() {
			return img.numDimensions() == 3;
		}
	}

}
