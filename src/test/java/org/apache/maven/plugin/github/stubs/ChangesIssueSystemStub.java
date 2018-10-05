package org.apache.maven.plugin.github.stubs;

import java.util.Properties;

import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Model;

/**
 * @author <a href="mailto:marcelo.riss@gmail.com">Marcelo Riss</a>
 * @version 2018 Oct, 4
 */
public class ChangesIssueSystemStub
    extends ChangesPluginProjectStub
{
	
	private Properties properties;
	
    @Override
    protected String getPOM()
    {
        return "pom1.xml";
    }
    
	@Override
	public IssueManagement getIssueManagement() {
		IssueManagement issueManagement = new IssueManagement();
		issueManagement.setSystem("github");
		issueManagement.setUrl("https://github.com/MRISS-Projects/dsh/issues");
		return issueManagement;
	}
	
	@Override
	public Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}
	
	@Override
    public Model getModel()
    {
        Model model = super.getModel();
        if ( model == null )
        {
            model = new Model();
            setModel( model );
        }
        return model;
    }


}
