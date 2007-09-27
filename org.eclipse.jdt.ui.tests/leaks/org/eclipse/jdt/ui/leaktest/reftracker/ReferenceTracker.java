/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.ui.leaktest.reftracker;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.jdt.testplugin.JavaTestPlugin;

/**
 * Tracks all instances reachable though reflection from a given root object. To visit all elements in a VM
 * pass a class loader as class loaders know all loaded classes. This normally covers
 * all references held by fields.
 * <p>Instances only referenced through local variables or native roots can not be found.</p>
 */
public final class ReferenceTracker {
	
	private IdentityHashSet fVisitedElements;
	private final ReferenceVisitor fRequestor;
	private FIFOQueue fQueue;
	private MultiStatus fStatus;
		
	public ReferenceTracker(ReferenceVisitor requestor) {
		fRequestor= requestor;
		fStatus= null;
		fVisitedElements= null;
		fQueue= null;
	}
	
	private static boolean isInteresting(Class clazz) {
		String name= clazz.getName();
		if (name.startsWith("org.eclipse.reftracker.") || name.startsWith("sun.reflect.")) {  //$NON-NLS-1$//$NON-NLS-2$
			return false;
		}
		return true;
	}
	
	private void followArrayReference(ReferencedObject prev, int index, Object to) {
		fQueue.add(new ReferencedArrayElement(prev, index, to));
	}
		
	private void followFieldReference(ReferencedObject ref, Object curr, Field fld) {
		try {
			boolean isAccessible= setAccessible(fld, true);
			try {
				Object fieldVal= fld.get(curr);
				if (fieldVal != null) {
					fQueue.add(new ReferencedFieldElement(ref, fld, fieldVal));
				}
			} finally {
				setAccessible(fld, isAccessible);
			}
		} catch (IllegalArgumentException e) {
			handleError(e, fld);
		} catch (IllegalAccessException e) {
			handleError(e, fld);
		} catch (ExceptionInInitializerError e) {
			handleError(e, fld);
		}
	}
	
	private void handleError(Throwable t, Field fld) {
		fStatus.add(new Status(IStatus.ERROR, JavaTestPlugin.getPluginId(), IStatus.ERROR, "Problem on access of " + fld.toString(), t));
	}
	
	private void followStaticReferences(Class classInstance) {
		Field[] declaredFields= classInstance.getDeclaredFields();
		for (int i= 0; i < declaredFields.length; i++) {
			Field fld= declaredFields[i];
			if (isStatic(fld.getModifiers()) && !fld.getType().isPrimitive()) {
				followFieldReference(new RootReference(classInstance), null, fld);
			}
		}
	}
	
	private static boolean setAccessible(Field fld, boolean enable) {
		boolean isAccessible= fld.isAccessible();
		if (isAccessible != enable) {
			fld.setAccessible(enable);
		}
		return isAccessible;
	}
	
	private void visit(ReferencedObject ref) {
		Object curr= ref.getValue();
		Class currClass= curr.getClass();
		if (!isInteresting(currClass)) {
			return;
		}

		boolean firstVisit= fVisitedElements.add(curr);
		fRequestor.visit(ref, curr.getClass(), firstVisit);
		
		if (!firstVisit) {
			return;
		}

		if (currClass.isArray()) {
			if (currClass.getComponentType().isPrimitive()) {
				return;
			}
			Object[] array= (Object[]) curr;
			for (int i= 0; i < array.length; i++) {
				Object elem= array[i];
				if (elem != null) {
					followArrayReference(ref, i, elem);
				}
			}
		} else if (currClass.isPrimitive()) {
			return;
		} else {
			if (currClass == Class.class) {
				followStaticReferences((Class) curr);
			}
			do {
				Field[] declaredFields= currClass.getDeclaredFields();
				for (int i= 0; i < declaredFields.length; i++) {
					Field fld= declaredFields[i];
					if (!isStatic(fld.getModifiers()) && !fld.getType().isPrimitive()) {
						followFieldReference(ref, curr, fld);
					}
				}
				currClass= currClass.getSuperclass();
			} while (currClass != null);
		}
	}

	private static boolean isStatic(int modifiers) {
		return (modifiers & Modifier.STATIC) != 0;
	}

	public IStatus start(Object root) {		
		fVisitedElements= new IdentityHashSet(1 << 21); // 2 M -> 8 MB
		fQueue= new FIFOQueue(100);
		fStatus= new MultiStatus(JavaTestPlugin.getPluginId(), IStatus.OK, "Problem tracking resources", null);
		
		try {
			visit(new RootReference(root));
			
			FIFOQueue queue= fQueue;
			
			ReferencedObject next= (ReferencedObject) queue.poll();
			while (next != null) {
				visit(next);
				next= (ReferencedObject) queue.poll();
			}
		} finally {
			// make sure not to hold on any references
			fVisitedElements.clear();
			fVisitedElements= null;
			fQueue= null;
		}
		return fStatus;
	}


	
}
