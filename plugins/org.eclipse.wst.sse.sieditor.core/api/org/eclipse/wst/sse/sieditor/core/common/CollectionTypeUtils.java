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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Collection utility functions.
 
 */
public class CollectionTypeUtils
{
	/**
	 * Replaces the content of one {@link Collection} with the content of another one
	 * if they are not equal.
	 * @param <T> content type
	 * @param source source {@link Collection}
	 * @param target target {@link Collection}
	 * @pre source != null
	 * @pre target != null
	 */
	public static <T> void copyContents(
		final Collection<T> source,
		final Collection<T> target
	){
		if (!Checker.isEqual(source, target)){
			copyContentsWithoutCheck(source, target);
		}
	}
	
	/**
	 * Returns an object different from the given one.
	 * @param set the set to search
	 * @param obj the object to avoid
	 * @param <T> type of the objects
	 * @param equal equality relation to use for comparison
	 * @return an object different from <code>obj</code>,
	 * 	if there is one, otherwise nil
	 * @pre docs != null
	 * @pre schema != null
 	 */
	public static <T> T findOther(
		final Collection<? extends T> set,
		final T obj,
		final Equal<? super T> equal		
	){
		Nil.checkNil(set, "docs"); //$NON-NLS-1$
		
		T res = null;
		
		for (final T doc: set){
			if (!equal.isEqual(doc, obj)){
				res = doc;
				break;
			}
		}
		
		return res;
	}

	/**
	 * Replaces the content of one {@link Collection} with the content of another one
	 * if they are not equal.
	 * @param <T> content type
	 * @param source source {@link Collection}
	 * @param target target {@link Collection}
	 * @param equal the comparison operator
	 * @pre source != null
	 * @pre target != null
	 */
	public static <T> void copyContents(
		final Collection<T> source,
		final Collection<T> target,
		final Equal<? super T> equal
	){
		if (!Checker.isEqual(source, target, equal)){
			copyContentsWithoutCheck(source, target);
		}
	}

	/**
	 * Replaces the content of one {@link Collection} with the content of another one,
	 * clearing the source {@link Collection} first,
	 * if they are not equal.
	 * @param <T> content type
	 * @param source source {@link Collection}
	 * @param target target {@link Collection}
	 * @param equal equality relation for <code>T</code> objects
	 * @pre source != null
	 * @pre target != null
	 */
	public static <T> void moveContents(
		final Collection<T> source,
		final Collection<T> target,
		final Equal<T> equal
	){
		if (!Checker.isEqual(source, target, equal)){
			clearSourceAndTargetAndCopy(source, target);
		}
	}
	
	/**
	 * Replaces the content of one {@link Collection} with the content of another one
	 * without checking equality. The target <em>and source</em> {@link Collection}s 
	 * are emptied before the
	 * target {@link Collection} is filled.
	 * @param <T> content type
	 * @param source source {@link Collection}
	 * @param target target {@link Collection}
	 * @pre source != null
	 * @pre target != null
	 */
	public static <T> void clearSourceAndTargetAndCopy(
		final Collection<T> source,
		final Collection<T> target
	){
		Nil.checkNil(source, "source"); //$NON-NLS-1$
		Nil.checkNil(target, "target"); //$NON-NLS-1$
		
		final Collection<T> copy = duplicate(source);
		
		source.clear();
		target.clear();
		target.addAll(copy);
	}

	/**
	 * Replaces the content of one {@link Collection} with the content of another one
	 * without checking equality. The <em>source</em> {@link Collection}
	 * is emptied before the
	 * target {@link Collection} is filled.
	 * @param <T> content type
	 * @param source source {@link Collection}
	 * @param target target {@link Collection}
	 * @pre source != null
	 * @pre target != null
	 */
	public static <T> void clearSourceAndCopy(
		final Collection<T> source,
		final Collection<T> target
	){
		Nil.checkNil(source, "source"); //$NON-NLS-1$
		Nil.checkNil(target, "target"); //$NON-NLS-1$
		
		final Collection<T> copy = duplicate(source);
		
		source.clear();
		target.addAll(copy);
	}
	
	/**
	 * Returns a new {@link ArrayList} with the same contents as <code>source</code>,
	 * except for the case that <code>source</code> is nil or empty, in which <code>source</code>
	 * itself is returned.
	 * @param <T> element type
	 * @param source the {@link Collection} to clone
	 * @return a new {@link ArrayList} with the same contents as <code>source</code>,
	 * 	except for the case that <code>source</code> is nil or empty, in which <code>source</code>
	 * 	itself is returned
	 */
	public static <T> Collection<T> duplicate(Collection<T> source){
		final Collection<T> result;
		
		if (source == null || source.isEmpty()){
			result = source;
		}else{
			result = new ArrayList<T>(source.size());
			result.addAll(source);
		}
		
		return result;
	}

	/**
	 * Replaces the content of one {@link Collection} with the content of another one
	 * without checking equality. Cears the source collection afterwards.
	 * @param <T> content type
	 * @param source source {@link Collection}
	 * @param target target {@link Collection}
	 * @pre source != null
	 * @pre target != null
	 */
	public static <T> void moveContentsWithoutCheck(
		final Collection<T> source,
		final Collection<T> target
	){
		Nil.checkNil(source, "source"); //$NON-NLS-1$
		Nil.checkNil(target, "target"); //$NON-NLS-1$
		target.clear();
		target.addAll(source);
		source.clear();
	}

	/**
    * Tries to find an one occurrence of an object in the given collection.
    * @param objects the set to search
    * @param <T> the object type
    * @param criterion the search criterion
    * @return the found object if it was found, nil otherwise
    * @pre objects != null
    * @pre criterion != null
    */
   public static <T> List<T> find(
   	final Collection<T> objects,
   	final Condition<? super T> criterion
   ){
   	Nil.checkNil(objects, "objects"); //$NON-NLS-1$
   	Nil.checkNil(criterion, "criterion"); //$NON-NLS-1$
   	List<T> findings = new ArrayList<T>();
   	
   	for (final T obj: objects){
   		if (criterion.isSatisfied(obj)){
   			findings.add(obj);
   		}
   	}
   	return findings;
   }

	/**
    * Returns the position of the first occurrence of an object in the given sequence.
    * @param objects the sequence to search
    * @param <T> the object type
    * @param criterion the search criterion
    * @return the  position of the first occurrence of the object if it was found, -1 otherwise
    * @pre objects != null
    * @pre criterion != null
    */
   public static <T> int indexOf(
   	final List<? extends T> objects,
   	final Condition<T> criterion
   ){
   	Nil.checkNil(objects, "objects"); //$NON-NLS-1$
   	Nil.checkNil(criterion, "criterion"); //$NON-NLS-1$
   	
   	int found = -1;
   	
   	for (int i = 0, n = objects.size(); i < n; ++i){
   		if (criterion.isSatisfied(objects.get(i))){
   			found = i;
   			break;
   		}
   	}
   	
   	return found;
   }

	/**
    * Returns all occurrences of objects satisfying some <code>criterion</code>
    * in the given collection.
    * @param objects the set to search
    * @param criterion the search criterion
    * @param <T> the object type
    * @return the objects satisfying the <code>criterion</code>
    * @pre objects != null
    * @pre criterion != null
    * @post return != null
    */
   public static <T> Collection<T> findAll(
   	final Collection<T> objects,
   	final Condition<? super T> criterion
   ){
   	Nil.checkNil(objects, "objects"); //$NON-NLS-1$
   	Nil.checkNil(criterion, "criterion"); //$NON-NLS-1$
   	
   	Collection<T> found = null;
   	
   	for (final T obj: objects){
   		if (criterion.isSatisfied(obj)){
   			if (found == null){
   				found = new LinkedList<T>();
    			}
  				found.add(obj);
   		}
  		}

   	if (found == null){
   		found = Collections.emptyList();
   	}
   	
   	return found;
   }

	/**
    * Returns all occurrences of objects of a certain runtime type
    * in the given collection.
    * @param objects the set to search
    * @param type the target type
    * @param <T> the target type
    * @return the objects of type <code>type</code> 
    * @pre objects != null
    * @pre type != null
    * @post return != null
    */
   public static <T> Collection<T> findAllOfType(
   	final Collection<? super T> objects,
   	final Class<T> type
   ){
   	Nil.checkNil(objects, "objects"); //$NON-NLS-1$
   	Nil.checkNil(type, "type"); //$NON-NLS-1$
   	
   	Collection<T> found = null;
   	
   	for (final Object obj: objects){
   		if (type.isInstance(obj)){
   			if (found == null){
   				found = new LinkedList<T>();
    			}
  				found.add((T)obj);
   		}
  		}

   	if (found == null){
   		found = Collections.emptyList();
   	}
   	
   	return found;
   }

	/**
    * Returns all occurrences objects satisfying some <code>criterion</code> in the given collection.
    * @param objects the set to search
    * @param criterion the search criterion
    * @param <T> the object type
    * @return the objects satisfying the <code>criterion</code>
    * @pre objects != null
    * @pre criterion != null
    * @post return != null
    */
   public static <T> Set<T> findAllAsSet(
   	final Collection<T> objects,
   	final Condition<? super T> criterion
   ){
   	Nil.checkNil(objects, "objects"); //$NON-NLS-1$
   	Nil.checkNil(criterion, "criterion"); //$NON-NLS-1$
   	
   	Set<T> found = null;
   	
   	for (final T obj: objects){
   		if (criterion.isSatisfied(obj)){
   			if (found == null){
   				found = new HashSet<T>();
    			}
  				found.add(obj);
   		}
  		}

   	if (found == null){
   		found = Collections.emptySet();
   	}
   	
   	return found;
   }

	/**
	 * Replaces the content of one {@link Collection} with the content of another one
	 * without checking equality.
	 * @param <T> content type
	 * @param source source {@link Collection}
	 * @param target target {@link Collection}
	 * @pre source != null
	 * @pre target != null
	 */
	private static <T> void copyContentsWithoutCheck(
		final Collection<T> source,
		final Collection<T> target
	){
		Nil.checkNil(source, "source"); //$NON-NLS-1$
		Nil.checkNil(target, "target"); //$NON-NLS-1$
		target.clear();
		target.addAll(source);
	}

	/**
	 * Search whether any object in 'objects' array does equal 'searchForObject'.
	 * @param objects can be null, and can contain null values
	 * @param searchForObject can be null
	 * @return true if searchForObject is found in 'objects'
	 */
	public static boolean containsObject(Object objects[], Object searchForObject) {
		if(objects == null) {
			return false;
		}
		for(Object object : objects) {
			boolean found = object == null ? searchForObject == null : object.equals(searchForObject);
			if(found) {
				return true;
			}
		}
		return false;
	}
}
