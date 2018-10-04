package org.apache.maven.plugin.github.stubs;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;

/**
 * @author <a href="mailto:marcelo.riss@gmail.com">Marcelo Riss</a>
 * @version 2018 Oct, 4
 */
public class ChangesPluginArtifactStub
    extends ArtifactStub
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
    public ChangesPluginArtifactStub( String groupId, String artifactId, String version, String packaging, String classifier )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.packaging = packaging;
        this.classifier = classifier;
        this.type = packaging;
        versionRange = VersionRange.createFromVersion( version );
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
	public String getClassifier() {
		return this.classifier;
	}

	@Override
	public boolean hasClassifier() {
		return this.classifier != null && !this.classifier.trim().isEmpty();
	}

	@Override
	public String getType() {
		return this.type;
	}
    
    
}
