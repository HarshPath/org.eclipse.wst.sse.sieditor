/*******************************************************************************
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Emil Simeonov - initial API and implementation.
 *    Dimitar Donchev - initial API and implementation.
 *    Dimitar Tenev - initial API and implementation.
 *    Nevena Manova - initial API and implementation.
 *    Georgi Konstantinov - initial API and implementation.
 *    Jakob Spies - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.core.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 Static class containing simple check procedures.
 */
public final class Checker
{
	/**
	 * Returns whether the 2 object references are both nil or refer to equal objects.
	 * @param x the 1st reference
	 * @param y the 2nd reference
	 * @return whether the 2 object references refer to equal objects
	 */
	public static boolean isEqual(final Object x, final Object y){
		return x == null && y == null || x != null && x.equals(y);
	}
	
	/**
	 * Returns whether the 2 object references are both nil or refer to equal objects.
	 * @param x the 1st reference
	 * @param y the 2nd reference
	 * @param equal used for checking object equality
	 * @param <T> type of the arguments
	 * @return whether the 2 object references refer to equal objects
	 */
	public static <T> boolean isEqual(final T x, final T y, final Equal<? super T> equal){
		return x == null && y == null || x != null && y != null && equal.isEqual(x, y);
	}
	
	/* *
	 * Compares 2 {@link Comparable} references. </code>null</code> is considered to be 
	 * less than every non-nil reference.
	 * @param x1 the first object
	 * @param x2 the second object
	 * @return the difference &quot;<code>x1</code> - <code>x2</code>&quot;
	 */
//	public static <T> int compare(final Comparable<T> x1, final T x2){
//		return
//			x1 == null?
//					x2 == null?
//							0
//						:
//							-1
//				:
//					x2 == null?
//							1
//						:
//							x1.compareTo(x2);
//	}
	
	/**
	 * Checks equality of 2 {@link Collection}s, hoping that the <code>equals</code>
	 * method of the contained objects is properly implemented.
	 * @param <T> type of the elements
	 * @param s1 the {@link Collection} to compare with <code>s2</code>
	 * @param s2 the {@link Collection} to compare with <code>s1</code>
	 * @return whether they are equal
	 */
	public static <T> boolean isEqual(final Collection<T> s1, final Collection<T> s2){
		return
				s1 == null && s2 == null
			|| s1 != null && s2 != null && s1.size() == s2.size() && s1.containsAll(s2);
	}

	/**
	 * Checks whether the contents of 2 sets are equal according to the specified
	 * equality relation <code>equal</code>.
	 * @param <T> the class of the types to be inspected
	 * @param fac1 the set to compare with <code>fac2</code>
	 * @param fac2 the set to compare with <code>fac1</code>
	 * @param equal the object user for comparing <code>T</code>s
	 * @return whether the 2 collections have equal content
	 * @pre fac1 != null
	 * @pre fac1 != null
	 */
	public static <T> boolean isEqual(
		final Collection<T> fac1,
		final Collection<T> fac2,
		final Equal<? super T> equal
	){
		Nil.checkNil(fac1, "fac1"); //$NON-NLS-1$
		Nil.checkNil(fac2, "fac2"); //$NON-NLS-1$
		
		boolean isEqual = fac1.size() == fac2.size();
		
		if (isEqual){
			boolean found = true;
			
			for (T f1: fac1){
				found = false;
				
				for (T f2: fac2){
					if (isEqual(f1, f2, equal)){
						found = true;
						break;
					}
				}
				if (!found) break;
			}
			
			isEqual = found;
		}
	
		return isEqual;
	}
	
	/**
	 * Checks whether 2 sequences are equal with respect to the specified
	 * equality relation <code>equal</code>.
	 * @param <T> the class of the types to be inspected
	 * @param fac1 the list to compare with <code>fac2</code>
	 * @param fac2 the list to compare with <code>fac1</code>
	 * @param equal the object user for comparing <code>T</code>s
	 * @return whether the 2 sequences are equal
	 * @pre fac1 != null
	 * @pre fac1 != null
	 */
	public static <T> boolean isEqual(
		final List<T> fac1,
		final List<T> fac2,
		final Equal<T> equal
	){
		Nil.checkNil(fac1, "fac1"); //$NON-NLS-1$
		Nil.checkNil(fac2, "fac2"); //$NON-NLS-1$
		
		boolean isEqual = fac1.size() == fac2.size();
		
		if (isEqual){
			Iterator<T> it2 = fac2.iterator();
			
			for (T f1: fac1){
				if (!equal.isEqual(f1, it2.next())){
					isEqual = false;
					break;
				}
			}
		}
	
		return isEqual;
	}

	/**
	 * Checks whether the contents of 2 sets are equal according to the specified
	 * equality relation <code>equal</code>. Nil input is allowed.
	 * @param <T> the class of the types to be inspected
	 * @param fac1 the set to compare with <code>fac2</code>
	 * @param fac2 the set to compare with <code>fac1</code>
	 * @param equal the object user for comparing <code>T</code>s
	 * @return whether the 2 collections have equal content
	 */
	public static <T> boolean isEqualNil(
		final Collection<T> fac1,
		final Collection<T> fac2,
		final Equal<T> equal
	){
		return 
				fac1 == null && fac2 == null 
			|| fac1 != null && fac2 != null && isEqual(fac1, fac2, equal);
	}
	
	/**
	 * Says whether a set of objects contains the given one.
	 * @param objects the set to search
	 * @param object the object to find
	 * @param <T> the object type
	 * @param equal used for comparison
	 * @return whether <code>objects</code> contains <code>object</code>
	 * @pre objects != null
 	 */
	public static <T> boolean isContained(
		final Collection<? extends T> objects,
		final T object,
		final Equal<T> equal
	){
		Nil.checkNil(objects, "objects"); //$NON-NLS-1$
		
		boolean contains = false;
		
		for (T doc: objects){
			if (isEqual(doc, object, equal)){
				contains = true;
				break;
			}
		}
		
		return contains;
	}
	
	/** Prevents instantiation. */
	private Checker(){}
}
