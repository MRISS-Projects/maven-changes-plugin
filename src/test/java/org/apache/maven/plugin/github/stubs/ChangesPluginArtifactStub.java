package org.apache.maven.plugin.github.stubs;

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

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;

/**
 * @author <a href="mailto:marcelo.riss@gmail.com">Marcelo Riss</a>
 * @version 2018 Oct, 4
 */
public class ChangesPluginArtifactStub extends ArtifactStub
{
    private String groupId;

    private String artifactId;

    private String version;

    private String packaging;

    private VersionRange versionRange;

    private ArtifactHandler handler;

    private String classifier;

    private String type;

    /**
     * @param groupId
     * @param artifactId
     * @param version
     * @param packaging
     */
    public ChangesPluginArtifactStub( String groupId, String artifactId, String version, String packaging,
            String classifier )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.packaging = packaging;
        this.classifier = classifier;
        this.type = packaging;
        this.versionRange = VersionRange.createFromVersion( version );
        setFile( new File( "target/dummyFile.jar" ) );
    }

    public ChangesPluginArtifactStub( String groupId, String artifactId, VersionRange versionRange, String type,
            String classifier, String scope )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.classifier = classifier;
        this.type = type;
        this.versionRange = versionRange;
        setArtifactHandler( new DefaultArtifactHandlerStub() );
    }

    @Override
    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    @Override
    public String getGroupId()
    {
        return groupId;
    }

    @Override
    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    @Override
    public String getArtifactId()
    {
        return artifactId;
    }

    @Override
    public void setVersion( String version )
    {
        this.version = version;
    }

    @Override
    public String getVersion()
    {
        return version;
    }

    /**
     * @param packaging
     */
    public void setPackaging( String packaging )
    {
        this.packaging = packaging;
    }

    /**
     * @return the packaging
     */
    public String getPackaging()
    {
        return packaging;
    }

    @Override
    public VersionRange getVersionRange()
    {
        return versionRange;
    }

    @Override
    public void setVersionRange( VersionRange versionRange )
    {
        this.versionRange = versionRange;
    }

    @Override
    public ArtifactHandler getArtifactHandler()
    {
        return handler;
    }

    @Override
    public void setArtifactHandler( ArtifactHandler handler )
    {
        this.handler = handler;
    }

    @Override
    public String getClassifier()
    {
        return this.classifier;
    }

    @Override
    public boolean hasClassifier()
    {
        return this.classifier != null && !this.classifier.trim().isEmpty();
    }

    @Override
    public String getType()
    {
        return this.type;
    }

}
