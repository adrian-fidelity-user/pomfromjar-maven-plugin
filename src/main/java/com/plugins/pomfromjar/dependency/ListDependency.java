package com.plugins.pomfromjar.dependency;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Copyright (c) 2012-2013 FMR LLC.
 * 
 * Licensed under The Apache Software License, Version 2.0 : http://www.apache.org/licenses/LICENSE-2.0.txt
 */

/**
 * Parses response from rest endpoint and adds dependecy to List
 * 
 * @author Adrian Ronayne
 *
 */
public interface ListDependency {

	/**
	 * Create the dependency based on its fileName and complete URI
	 * 
	 * @param fileName
	 * @param restServiceUri
	 * @throws IOException 
	 */
	public abstract void addDependency(String fileName,
			String repositoryUri, String sha1) throws IOException;

	/**
	 * 
	 * @return JarFileDependency List of dependencies not created 
	 */
	public abstract List<JarFileDependency> getDependencyNotCreatedList();

	/**
	 * 
	 * @return JarFileDependency of all dependencies (includes duplicates)
	 */
	public abstract List<JarFileDependency> getAllDependenciesList();
	
	/**
	 * 
	 * @return JarFileDependency List of without duplicated
	 */
	public abstract Set<JarFileDependency> getDistinctDependenciesList();

}