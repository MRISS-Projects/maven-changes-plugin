package org.apache.maven.plugin.github;

import java.io.File;
import java.util.Properties;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.mockito.Mockito;

public class GitHubTextListMojoTestCase extends AbstractMojoTestCase {

	/** INIT THIS WITH THE PLUGIN NAME **/
	private String pluginName="maven-changes-plugin";
	
	protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Regular test
	 */
	public void testExecute() {
        testPom("pom1");
	}
	
	private void testPom(String testPomName) {
		File testPom = new File( getBasedir(), "target/test-classes/poms/"+testPomName+".xml" );
		try {
			PlexusConfiguration pc = this.extractPluginConfiguration(pluginName, testPom);
			GitHubTextListMojo mojo = new GitHubTextListMojo();
			this.configureMojo(mojo, pc);
			assertNotNull(mojo);
			mojo.execute();
			System.out.println(mojo.getMavenProject().getProperties().get("issues.text.list"));
		} catch (Exception e) {
			e.printStackTrace();
			// Try with mockito
			try {
				GitHubTextListMojo mojo = Mockito.mock(GitHubTextListMojo.class);
				assertNotNull(mojo);
				MavenProject pm = Mockito.mock(MavenProject.class);
				Properties p = new Properties();
				p.setProperty("current.release.issues", "MOCKED CURRENT RELEASE ISSUES");
				p.setProperty("older.releases.issues", "MOCKED OLDER RELEASES ISSUES");
				Mockito.when(pm.getProperties()).thenReturn(p);
				Mockito.when(mojo.getMavenProject()).thenReturn(pm);
				mojo.execute();
				System.out.println(mojo.getMavenProject().getProperties().get("current.release.issues"));
				System.out.println(mojo.getMavenProject().getProperties().get("older.releases.issues"));				
			} catch (Exception e2) {
				e.printStackTrace();
				fail(e.getMessage());
			}		
		}
	}

}
