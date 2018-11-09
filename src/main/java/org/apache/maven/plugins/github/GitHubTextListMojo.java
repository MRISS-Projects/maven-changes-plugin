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

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.changes.textformater.IssueListFormater;
import org.apache.maven.plugins.changes.textformater.IssueListFormatterFactory;
import org.apache.maven.plugins.issues.Issue;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReportException;

import java.util.Collections;
import java.util.Comparator;

/**
 * Mojo to format a list of issues from github in a text format. It will generate a apt or markdown text formatting
 * style which can be set to a property. That property can then be inserted in another markdown or apt text file.
 * 
 * @author riss
 * @since 2.12.3
 *
 */
@Mojo( name = "github-text-list" )
public class GitHubTextListMojo extends GitHubMojo
{

    /**
     * 
     * 
     * @author riss
     *
     */
    public class IssueComparator implements Comparator<Issue>
    {

        @Override
        public int compare( Issue o1, Issue o2 )
        {
            int version1 = getVersion( o1 );
            int version2 = getVersion( o2 );
            if ( version1 < version2 )
            {
                return 1;
            }
            else if ( version1 > version2 )
            {
                return -1;
            }
            else
            {
                if ( o1.getUpdated().before( o2.getUpdated() ) )
                {
                    return 1;
                }
                else if ( o1.getUpdated().after( o2.getUpdated() ) )
                {
                    return -1;
                }
                else
                {
                    return 0;
                }
            }
        }

        private int getVersion( Issue o1 )
        {
            String version = o1.getVersion();
            if ( StringUtils.isEmpty( version ) )
            {
                List<String> versions = o1.getFixVersions();
                if ( versions != null && !versions.isEmpty() )
                {
                    version = versions.get( 0 );
                }
                else
                {
                    return 0;
                }
            }
            version = version.replaceAll( "-SNAPSHOT", "" );
            version = version.replaceAll( "\\.", "" );
            return Integer.parseInt( version );
        }
    }

    /***
     * The text formatter to be used to format issues. Accepted values are <code>apt</code> or <code>markdown</code>.
     */
    @Parameter( property = "changes.text.list.formater", defaultValue = "markdown" )
    private String textListFormater;

    /***
     * The name of the property to be set with the formatted result of issues, following the format defined at
     * <code>textListFormater</code>.
     */
    @Parameter( property = "changes.issue.list.propert.name", defaultValue = "issues.text.list" )
    private String issueListPropertyName;

    /***
     * The number of symbols used for sub-title leveling in case of version title separators. 
     * In case of markdown the number of '#' characters used in the version sub-title.
     */
    @Parameter( property = "subtitle.level.number", defaultValue = "3" )
    private int subtitleLevelNumber;

    @Override
    protected void generateReport( Locale locale, List<Integer> columnIds, List<Issue> issueList )
    {
        Collections.sort( issueList, new IssueComparator() );
        IssueListFormater issueFormatter = IssueListFormatterFactory.getInstance()
                .getIssueListFormatter( textListFormater, true, project.getIssueManagement().getUrl(), "", columnIds,
                        locale, subtitleLevelNumber );
        project.getProperties().put( issueListPropertyName, issueFormatter.formatIssueList( issueList ) );
    }

    public MavenProject getMavenProject()
    {
        return project;
    }

    @Override
    public void execute() throws MojoExecutionException
    {
        if ( !canGenerateReport() )
        {
            return;
        }
        Locale locale = Locale.getDefault();
        try
        {
            executeReport( locale );
        }
        catch ( MavenReportException e )
        {
            throw new MojoExecutionException(
                    "An error has occurred in " + getName( Locale.ENGLISH ) + " report generation.", e );
        }
    }

}
