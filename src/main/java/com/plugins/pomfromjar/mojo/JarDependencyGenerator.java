package com.plugins.pomfromjar.mojo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plugins.pomfromjar.dependency.JarFileDependency;
import com.plugins.pomfromjar.dependency.ListDependency;
import com.plugins.pomfromjar.dependency.ListDependencyImpl;

/**
 * Copyright (c) 2012-2013 FMR LLC.
 * 
 * Licensed under The Apache Software License, Version 2.0 : http://www.apache.org/licenses/LICENSE-2.0.txt
 */

/**
 * Creates maven dependencies from a jar dir and writes them to file
 * 
 * @author Adrian Ronayne
 * 
 */
public class JarDependencyGenerator {

	private final static Logger LOGGER = LoggerFactory.getLogger(JarDependencyGenerator.class);

	private String jarDir;
	private String repositoryUri;
	private String pomFile;
	private File path;	
	private Model model = new Model();
	private Map<JarFileDependency, List<JarFileDependency>> dependencyMap = new HashMap<JarFileDependency, List<JarFileDependency>>();
	private ListDependency dependencyList = new ListDependencyImpl();
	
	public JarDependencyGenerator(String jarDir, String repositoryUri,String pomFile) {
		this.jarDir = jarDir;
		this.repositoryUri = repositoryUri;
		this.pomFile = pomFile;		
	}

	public void generateDependencyFile() throws IOException {
		this.createDependenciesFromJarDir();
		this.filterDependencies();
		this.writeDependencyToFile(this.pomFile);
		this.writeSummary();
	}

	/**
	 * Iterate through jar files in dir (non recursive) and create the
	 * corresponding dependencies
	 * 
	 * Documentation available on
	 * http://commons.apache.org/codec/apidocs/org/apache/commons/codec/digest/DigestUtils.html 
	 * An advanced maven search accepts a SHA-1 checksum - http://search.maven.org/#advancedsearch
	 */
	private void createDependenciesFromJarDir() throws IOException {

		String sha1;
		FileInputStream fileInputStream;
		path = new File(jarDir);
		LOGGER.info("JAR DIR : " + jarDir);

		File dir = new File(jarDir);
		for (File child : dir.listFiles()) {
			String fileName = child.getName();
			if (child.isFile() && fileName.endsWith(".jar")) {
					fileInputStream = new FileInputStream(child);
					sha1 = DigestUtils.sha1Hex(fileInputStream);
					dependencyList.addDependency(fileName, repositoryUri , sha1);
			}
		}
	}

	/**
	 * Associates multiple dependencies with a dependency key
	 * 
	 * The key is a dependency and its value is a list of dependencies
	 * For each iteration attempt to get the associated list of dependencies using the dependency key
	 * If the key is returned add the dependency to this list, if not create a key/value entry
	 * 
	 * dependencyMap is used to output duplicated dependencies associated with a key
	 */
	private void filterDependencies(){
		
		List<JarFileDependency> jarFileDependencyList;

		for(JarFileDependency jarFileDependency : dependencyList.getAllDependenciesList()){		
			jarFileDependencyList = dependencyMap.get(jarFileDependency);
		    if(jarFileDependencyList == null){
		        jarFileDependencyList = new ArrayList<JarFileDependency>();
		        dependencyMap.put(jarFileDependency, jarFileDependencyList);
		    }
		    jarFileDependencyList.add(jarFileDependency);
		}
	}
	
	/**
	 * Writes the depenendecy to a pom file & increments logging counters
	 * @param fileName 
	 * 
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @throws IOException
	 */
	private void writeDependencyToFile(String fileName) {

		try {	

			for(JarFileDependency jarFileDependency : dependencyList.getDistinctDependenciesList()){	
				model.addDependency(jarFileDependency);
			}
	
			OutputStream fileOutputStream = new FileOutputStream(this.pomFile);
			Writer writer = new BufferedWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
			MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();
			mavenXpp3Writer.write(writer, model);
		} 
		catch (FileNotFoundException e) {
			LOGGER.error("********* Exception thrown in JarDependencyGeneratorecreateDependenciesFromJarDir.writeDependencyToFile ********* : "+e.getMessage());
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			LOGGER.error("********* Exception thrown in JarDependencyGeneratorecreateDependenciesFromJarDir.writeDependencyToFile ********* : "+e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e) {
			LOGGER.error("********* Exception thrown in JarDependencyGeneratorecreateDependenciesFromJarDir.writeDependencyToFile ********* : "+e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Write summary information of created dependencies
	 */
	private void writeSummary() {

		int dupplicateDependencyCounter = 0;
		LOGGER.warn("********* The following libraries were not found on the Maven repository ********* : ");
		for (JarFileDependency dependency : dependencyList.getDependencyNotCreatedList()) {
			LOGGER.info("Dependency missing : " + dependency.getJarFileName() + " from URI : "+ dependency.getRestServiceUri());
		}
		
		for (JarFileDependency jarFileDependencyKey : dependencyMap.keySet()) {
			List<JarFileDependency> list = dependencyMap.get(jarFileDependencyKey);
			if(list.size() > 1){
				for(JarFileDependency duplicatedDependency : list){		
					LOGGER.info("Duplicated dependency  : filename : "+duplicatedDependency.getJarFileName()+", Details (GroupId, ArtifictId, Version) : " + duplicatedDependency.getGroupId()+ "," + duplicatedDependency.getArtifactId() + 
							","+ duplicatedDependency.getVersion());
					++dupplicateDependencyCounter;
				}
			}
		}

		LOGGER.info("Total number of jars : "+ path.listFiles().length);
		LOGGER.info("Total number dependencies created : "+ model.getDependencies().size());
		LOGGER.warn("********* Total number of dependencies not created ********* : "+ dependencyList.getDependencyNotCreatedList().size());
		LOGGER.info("Total number of duplicated dependencies : "+ dupplicateDependencyCounter);

	}
}
