package com.plugins.pomfromjar.dependency;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.maven.model.Dependency;

/**
 * Copyright Fidelity
 * 
 * Licensed under The Apache Software License, Version 2.0 : http://www.apache.org/licenses/LICENSE-2.0.txt
 */
/**
 * Extends Maven Dependency and adds attribute jarFileName This is useful for
 * linking dependency information with a specific jar file
 * 
 * @author Adrian Ronayne
 * 
 */
public class JarFileDependency extends Dependency {

	private String jarFileName;
	private String checkSum;
	private String restServiceUri;
	
	public String getCheckSum() {
		return checkSum;
	}

	public JarFileDependency(String jarFileName, String checkSum, String restServiceUri) {
		this.jarFileName = jarFileName;
		this.checkSum = checkSum;
		this.restServiceUri = restServiceUri;
	}

	/**
	 * 
	 * @return the rest endpoint uri for this jar file
	 */
	public String getRestServiceUri() {
		return restServiceUri;
	}

	/**
	 * 
	 * @return the jar fileName for this dependency
	 */
	public String getJarFileName() {
		return this.jarFileName;
	}

	/**
	 * 
	 * Some dependencies(commons-codec-1.6.jar & commons-codec-1.6-tests.jar)  could break the overriding equals & hashcode contract : 
	 * whenever a.equals(b), then a.hashCode() must be same as b.hashCode()
	 * 
	 * commons-codec-1.6.jar & commons-codec-1.6-tests.jar generates a different sha1 but has same dependency information
	 *
	 * Using classes EqualsBuilder and HashCodeBuilder 
	 * from the Apache Commons Lang library for overriding  hashCode() &
	 * equals methods
	 * For more info refer to http://stackoverflow.com/questions/2707541/why-should-i-override-hashcode-when-i-override-equals-method
	 *
	 */
	@Override
	public int hashCode() {
		
		 return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		            append(this.getGroupId()).
		            append(this.getArtifactId()).
		            append(this.getVersion()).
		            toHashCode();

	}

	@Override
	public boolean equals(Object object) {
		
		if (object == null)
            return false;
        if (object == this)
            return true;
        if (object.getClass() != getClass())
            return false;

        JarFileDependency jfd = (JarFileDependency) object;
        return new EqualsBuilder().
            // if deriving: appendSuper(super.equals(obj)).
            append(this.getGroupId(), jfd.getGroupId()).
            append(this.getArtifactId(), jfd.getArtifactId()).
            append(this.getVersion(), jfd.getVersion()).

            isEquals();
      
	}

}
