/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.jdt.internal.corext.refactoring;

import org.eclipse.core.resources.IResource;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Represents a search result - exactly as found by SearchEngine
 */
public final class SearchResult {
	private IResource fResource;
	private IJavaElement fEnclosingElement;
	private int fStart, fEnd;
	private int fAccuracy;
	
	/**
	 * @see IJavaSearchResultCollector#accept
	 */
	public SearchResult(IResource resource, int start, int end, IJavaElement enclosingElement, int accuracy){
		fResource= resource;
		fStart= start;
		fEnd= end;
		fEnclosingElement= enclosingElement;
		fAccuracy= accuracy;
	}

	public int getStart(){
		return fStart;	
	}
	
	public int getEnd(){
		return fEnd;	
	}
	
	public IResource getResource(){
		return fResource;
	}
	
	public IJavaElement getEnclosingElement(){
		return fEnclosingElement;
	}
	
	public int getAccuracy(){
		return fAccuracy;
	}
	
	/* non java-doc
	 * for debugging only
	 */
	public String toString(){
		return "\n<Search Result"  //$NON-NLS-1$
			 + "\n\tstart:" + fStart //$NON-NLS-1$
			 + "\n\tend:" + fEnd //$NON-NLS-1$
			 + "\n\tresource:" + fResource.getFullPath() //$NON-NLS-1$
			 + "\n\tjavaElement:" + fEnclosingElement + "(instanceof " + fEnclosingElement.getClass() + ")" //$NON-NLS-1$ //$NON-NLS-3$ //$NON-NLS-2$
			 + getElementSourceRange()
			 + "\n\tAccuracy:" + fAccuracy //$NON-NLS-1$
			 + "/>"; //$NON-NLS-1$
	}
	
	//just for debugging
	private String getElementSourceRange(){
		try{
			if (fEnclosingElement instanceof ISourceReference)
			    return ((ISourceReference)fEnclosingElement).getSourceRange().toString();
			else return ""; //$NON-NLS-1$
		} catch (JavaModelException e){
			return "<Exception>"; //$NON-NLS-1$
		}	
	}
	
	public ICompilationUnit getCompilationUnit(){
		IJavaElement jElement= JavaCore.create(getResource());
		if (jElement == null || jElement.getElementType() != IJavaElement.COMPILATION_UNIT)
			return null;
		return (ICompilationUnit)jElement;
	}
	
	
}