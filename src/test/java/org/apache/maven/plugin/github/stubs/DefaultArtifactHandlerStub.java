package org.apache.maven.plugin.github.stubs;

import org.apache.maven.artifact.handler.DefaultArtifactHandler;

/**
 * @author <a href="mailto:marcelo.riss@gmail.com">Marcelo Riss</a>
 * @version 2018 Oct, 4
 */
public class DefaultArtifactHandlerStub
    extends DefaultArtifactHandler
{
    private String language;

    @Override
    public String getLanguage()
    {
        if ( language == null )
        {
            language = "java";
        }

        return language;
    }

    /**
     * @param language the language. Defaults to "java".
     */
    public void setLanguage( String language )
    {
        this.language = language;
    }
}
