package com.plugins.pomfromjar.mojo;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.plugins.pomfromjar.utils.ProxyServer;

/**
 * Copyright (c) 2012-2013 FMR LLC.
 * 
 * Licensed under The Apache Software License, Version 2.0 : http://www.apache.org/licenses/LICENSE-2.0.txt
 */

/**
 * For more information on writing maven plugins : http://maven.apache.org/guides/plugin/guide-java-plugin-development.html
 * 
 * Sets up the various parameters used, configured in the pom file.
 * 
 * @author Adrian Ronayne
 * @goal generatedependency
 */
public class PomDependencyMojo extends AbstractMojo{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PomDependencyMojo.class);
	
	 /**
     * @parameter expression="${generatedPomFile}" default-value="c:\\pom\\generated-dependencies-pom.xml"
     */
	private String generatedPomFile;
	
	 /**
     * @parameter expression="${repositoryUri}" default-value="http://repository.sonatype.org/service/local/lucene/search?sha1="
     */
	private String repositoryUri;
	
	 /**
     * @parameter expression="${jarDependenciesDir}" default-value="c:\\pom\\jars"
     */
	private String jarDependenciesDir;

	/**
     *
     * @parameter expression="${proxyHost}" default-value=""
     */
	private String proxyHost;
	
	 /**
     * @parameter expression="${proxyPort}" default-value=""
     */
	private String proxyPort;
	
	public PomDependencyMojo(String generatedPomFile, String jarDependenciesDir, String repositoryUri, String proxyHost, String proxyPort){
		this.generatedPomFile = generatedPomFile;
		this.repositoryUri = repositoryUri;
		this.jarDependenciesDir = jarDependenciesDir;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}
	
	/**
	 * Default constructor required by mojo when running via Maven
	 */
	public PomDependencyMojo(){

	}
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		  //Needed to access external repository if behind a proxy
		  if(!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort)){
			 new ProxyServer(proxyHost , proxyPort);
		  }
		  
		  JarDependencyGenerator jarDependencyGenerator = new JarDependencyGenerator(jarDependenciesDir , repositoryUri , generatedPomFile);
		  try {
			jarDependencyGenerator.generateDependencyFile();
		} catch (IOException e) {
			LOGGER.debug("**************** Please check you proxy settings ***************");
			e.printStackTrace();
		}
	}

}
