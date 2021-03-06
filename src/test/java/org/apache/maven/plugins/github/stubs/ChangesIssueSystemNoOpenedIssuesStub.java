package org.apache.maven.plugins.github.stubs;

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

import java.util.Properties;

import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Model;

/**
 * @author <a href="mailto:marcelo.riss@gmail.com">Marcelo Riss</a>
 * @version 2018 Oct, 4
 */
public class ChangesIssueSystemNoOpenedIssuesStub extends ChangesPluginProjectStub
{

    private Properties properties;

    @Override
    protected String getPOM()
    {
        return "pom2.xml";
    }

    @Override
    public IssueManagement getIssueManagement()
    {
        IssueManagement issueManagement = new IssueManagement();
        issueManagement.setSystem( "github" );
        issueManagement.setUrl( "https://github.com/MRISS-Projects/dsh/issues" );
        return issueManagement;
    }

    @Override
    public Properties getProperties()
    {
        if ( properties == null )
        {
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
