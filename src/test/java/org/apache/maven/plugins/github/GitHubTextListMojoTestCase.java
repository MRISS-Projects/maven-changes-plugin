package org.apache.maven.plugins.github;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * 
 * @author riss
 *
 */
public class GitHubTextListMojoTestCase extends AbstractMojoTestCase
{

    /** INIT THIS WITH THE PLUGIN NAME **/
    private String pluginName = "maven-changes-plugin";

    protected void setUp() throws Exception
    {
        // required for mojo lookups to work
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Regular test
     * @throws Exception 
     */
    public void testExecute() throws Exception
    {
        testPom( "pom1" );
    }

    /**
     * No Opened Issues
     * @throws Exception 
     */
    public void testExecuteNoOpenedIssues() throws Exception
    {
        testPom( "pom2" );
    }

    /**
     * Remove snapshot suffix
     */
    public void testExecuteRemoveSnapshotSuffix()
    {
        try
        {
            testPom( "pom3" );
            fail();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
        }
    }
    
    /**
     * Remove snapshot suffix, but this won't cause a failure
     */
    public void testExecuteRemoveSnapshotSuffixNoFail()
    {
        Exception result = null;
        try
        {
            testPom( "pom4" );
        }
        catch ( Exception e )
        {
            result = e;
        }
        assertNull( result );
        
    }
    

    private void testPom( String testPomName ) throws Exception
    {
        File testPom = new File( getBasedir(), "target/test-classes/poms/" + testPomName + ".xml" );
        try
        {
            PlexusConfiguration pc = this.extractPluginConfiguration( pluginName, testPom );
            GitHubTextListMojo mojo = new GitHubTextListMojo();
            this.configureMojo( mojo, pc );
            assertNotNull( mojo );
            mojo.execute();
            System.out.println( mojo.getMavenProject().getProperties().get( "issues.text.list" ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw e;
        }
    }

}
