/*
 * #%L
 * SciJava Common shared library for SciJava software.
 * %%
 * Copyright (C) 2009 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
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

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

import org.scijava.util.GenericUtils;

/**
 * Utility class for working with generic types, fields and methods.
 * 
 * @author Curtis Rueden
 */
public final class Types {
	
	private Types() {
		// NB: Prevent instantiation of utility class.
	}

	// TODO: Migrate all GenericUtils methods here.
	// TODO: Migrate all OpMatchingUtil methods here.

	public static String name(final Type t) {
		// NB: It is annoying that Class's toString() method prepends "class "
		// before its name; this method exists to work around that behavior.
		return t instanceof Class ? ((Class<?>) t).getName() : t.toString();
	}

	/**
	 * Discerns whether it would be legal to assign a reference of type
	 * {@code source} to a reference of type {@code target}.
	 * 
	 * @see Class#isAssignableFrom(Class)
	 */
	public static boolean isAssignable(final Type source, final Type target) {
		return TypeToken.of(target).isAssignableFrom(source);
	}

	public static ParameterizedType newParameterizedType(final Class<?> rawType,
		final Type[] typeArgs)
	{
		// TODO: Implement this more properly.
		// - Add support for inner classes (non-null owner types).
		// - Implement equals, hashCode, etc.
		// - Call Guava's Types.newParameterizedType via reflection?
		return new ParameterizedType() {

			@Override
			public Type[] getActualTypeArguments() {
				return typeArgs;
			}

			@Override
			public Type getRawType() {
				return rawType;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}

			@Override
			public String toString() {
				final StringBuilder sb = new StringBuilder();
				sb.append(rawType.getName());
				sb.append("<");
				boolean first = true;
				for (final Type t : typeArgs) {
					if (first) first = false;
					else sb.append(", ");
					sb.append(t);
				}
				sb.append(">");
				return sb.toString();
			}
		};
	}

	public static WildcardType newWildcardType() {
		return newWildcardType(null, null);
	}

	public static WildcardType newWildcardType(final Type upperBound,
		final Type lowerBound)
	{
		// TODO: Implement this more properly.
		return new WildcardType() {

			@Override
			public Type[] getUpperBounds() {
				return upperBound == null ? new Type[] {} : new Type[] {upperBound};
			}

			@Override
			public Type[] getLowerBounds() {
				return lowerBound == null ? new Type[] {} : new Type[] {lowerBound};
			}
			
			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder("?");
				if (upperBound != null) sb.append(" extends " + name(upperBound));
				if (lowerBound != null) sb.append(" super " + name(lowerBound));
				return sb.toString();
			}
		};
	}

  /**
	 * Gets the (first) raw class of the given type.
	 * <ul>
	 * <li>If the type is a {@code Class} itself, the type itself is returned.
	 * </li>
	 * <li>If the type is a {@link ParameterizedType}, the raw type of the
	 * parameterized type is returned.</li>
	 * <li>If the type is a {@link GenericArrayType}, the returned type is the
	 * corresponding array class. For example: {@code List<Integer>[] => List[]}.
	 * </li>
	 * <li>If the type is a type variable or wildcard type, the raw type of the
	 * first upper bound is returned. For example:
	 * {@code <X extends Foo & Bar> => Foo}.</li>
	 * </ul>
	 * <p>
	 * If you want <em>all</em> raw classes of the given type, use {@link #raws}.
	 * </p>
	 */
	public static Class<?> raw(final Type type) {
		// TODO: Consolidate with GenericUtils.
		return GenericUtils.getClass(type);
	}

	/**
	 * Gets all raw classes corresponding to the given type.
	 * <p>
	 * For example, a type parameter {@code A extends Number & Iterable} will
	 * return both {@link Number} and {@link Iterable} as its raw classes.
	 * </p>
	 * @see #raw
	 */
	public static List<Class<?>> raws(final Type type) {
		// TODO: Consolidate with GenericUtils.
		return GenericUtils.getClasses(type);
	}

	public static Field field(final Class<?> c, final String name) {
		if (c == null) throw new IllegalArgumentException("No such field: " + name);
		try {
			return c.getDeclaredField(name);
		}
		catch (final NoSuchFieldException e) {}
		return field(c.getSuperclass(), name);
	}

}
