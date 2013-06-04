package com.plugins.pomfromjar.dependency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Copyright Fidelity
 * 
 * Licensed under The Apache Software License, Version 2.0 : http://www.apache.org/licenses/LICENSE-2.0.txt
 */

/**
 * Parses response from rest endpoint and adds dependecy to List
 * 
 * @author Adrian Ronayne
 *
 */
public class ListDependencyImpl implements ListDependency {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ListDependencyImpl.class);

	private DocumentBuilderFactory documentBuilderFactory;
	private XPathFactory xPathfactory;
	private DocumentBuilder documentBuilder;
	private XPath xpath;
	private Document document;
	private List<JarFileDependency> dependencyNotCreatedList = new ArrayList<JarFileDependency>();
	private List<JarFileDependency> allDependenciesList = new ArrayList<JarFileDependency>();
	
	public ListDependencyImpl(){
		this.setupXPath();
	}
	/**
	 * Setup the xpath objects for use later
	 */
	private void setupXPath() {

		try {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			xPathfactory = XPathFactory.newInstance();
			xpath = xPathfactory.newXPath();
		} catch (ParserConfigurationException pce) {
			LOGGER.error("Exception thrown in ListDependencyImpl.setupXPath "+pce.getMessage());
		}
	}
	
	
	/**
	 * Creates the dependency Lists
	 * 
	 * allDependencies - contains all of the dependencies, including duplicates
	 * dependencyNotCreatedList - dependencies which have not been created (not found on rest endpoint)
	 * @throws IOException 
	 * 
	 */
	public void addDependency(String fileName, String repositoryUri, String sha1) throws IOException {
		try {
			
			String restServiceUri = repositoryUri + sha1;
			LOGGER.info("URI : " + restServiceUri);
			document = documentBuilder.parse(restServiceUri);

			XPathExpression xPathexpression = xpath.compile("/searchNGResponse/data/artifact/groupId/text()");
			String groupId = (String) xPathexpression.evaluate(document,XPathConstants.STRING);
			xPathexpression = xpath.compile("/searchNGResponse/data/artifact/artifactId/text()");
			String artifactId = (String) xPathexpression.evaluate(document,XPathConstants.STRING);
			xPathexpression = xpath.compile("/searchNGResponse/data/artifact/version/text()");
			String version = (String) xPathexpression.evaluate(document,XPathConstants.STRING);

			if (!groupId.isEmpty()) {

				JarFileDependency dependency = new JarFileDependency(fileName , sha1 , restServiceUri);
				dependency.setGroupId(groupId);
				dependency.setArtifactId(artifactId);
				dependency.setVersion(version);

				allDependenciesList.add(dependency);
			} else {
				dependencyNotCreatedList.add(new JarFileDependency(fileName , sha1 , restServiceUri));
			}
		} catch (XPathExpressionException xpee) {
			LOGGER.error("Exception thrown in ListDependencyImpl.addDependency "+xpee.getMessage());
		}  catch (SAXException se) {
			LOGGER.error("Exception thrown in ListDependencyImpl.addDependency "+se.getMessage());
		}
	}


	/**
	 * @return dependencies which have not been created
	 */
	public List<JarFileDependency> getDependencyNotCreatedList() {
		return dependencyNotCreatedList;
	}

	/**
	 * @return all dependencies including duplicates
	 */
	public List<JarFileDependency> getAllDependenciesList() {
		return allDependenciesList;
	} 
	
	/**
	 * @return all dependencies including duplicates
	 */
	public Set<JarFileDependency> getDistinctDependenciesList() {	
		
		Set<JarFileDependency> uniqueDependencies = new HashSet<JarFileDependency>();
		uniqueDependencies.addAll(this.getAllDependenciesList());
		
		return uniqueDependencies;
	} 
	
	
	
}
