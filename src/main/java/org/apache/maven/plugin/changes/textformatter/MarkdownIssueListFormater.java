package org.apache.maven.plugin.changes.textformatter;

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

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.issues.Issue;
import org.apache.maven.plugin.issues.IssuesReportHelper;

import java.text.DateFormat;

/**
 * 
 * @author riss
 *
 */
public class MarkdownIssueListFormater implements IssueListFormater
{

    private static final String COLUMN_SEPARATOR = "|";

    private static final String MD_LINE_PREFIX = "";

    private static final String MD_COLUMN_SPACE = " ";

    private static final String NOT_AVAILABLE = "n/a";

    // private static final String HEADER = MD_LINE_PREFIX + "| Ticket Id | Summary | Type | Priority | Resolution |\n"
    // + "| --------- | ------- | ---- | -------- | ---------- |\n";
    private boolean versionSeparator;

    private String issueManagementUrl;

    private String ticketSuffix;

    private List<Integer> columnIds;

    private Locale locale;

    private String[] headerLabelKeys;

    /**
     * 
     * @param versionSeparator
     *            if there should e a version text separating issues for each version/release
     * @param issueManagementUrl
     *            the issue management URL to e used to get the issues. Typically configured as maven issue management
     *            at a POM file.
     * @param ticketSuffix
     *            a suffix to be appended to the issue management URL to identify the unique URL for each issue
     * @param columnIds
     *            the integer IDs for columns as stated at {@link IssuesReportHelper} class.
     * @param locale
     */
    public MarkdownIssueListFormater( boolean versionSeparator, String issueManagementUrl, String ticketSuffix,
            List<Integer> columnIds, Locale locale )
    {
        this.versionSeparator = versionSeparator;
        if ( issueManagementUrl.endsWith( "/" ) )
        {
            this.issueManagementUrl = issueManagementUrl;
        }
        else
        {
            this.issueManagementUrl = issueManagementUrl + "/";
        }
        this.ticketSuffix = ticketSuffix;
        this.columnIds = columnIds;
        this.locale = locale;
        this.headerLabelKeys = new String[] { "report.issues.label.assignee", "report.issues.label.component",
                "report.issues.label.created", "report.issues.label.fixVersion", "report.issues.label.id",
                "report.issues.label.key", "report.issues.label.priority", "report.issues.label.reporter",
                "report.issues.label.resolution", "report.issues.label.status", "report.issues.label.summary",
                "report.issues.label.type", "report.issues.label.updated", "report.issues.label.version" };
    }

    @Override
    public String formatIssueList( List<Issue> issueList )
    {
        String header = generateHeader();
        String result = "";
        if ( !versionSeparator )
        {
            result = header;
        }
        String version = "";
        DateFormat df = DateFormat.getDateInstance( DateFormat.SHORT, locale );
        int[] columns = IssuesReportHelper.toIntArray( columnIds );
        for ( Iterator<Issue> iterator = issueList.iterator(); iterator.hasNext(); )
        {
            Issue issue = (Issue) iterator.next();
            if ( issue == null || issue.getType() == null )
            {
                continue;
            }
            String issueVersion = getVersion( issue );
            if ( issueVersion == null && version != null && isOpened( issue ) && versionSeparator )
            {
                if ( !version.isEmpty() )
                {
                    result += "\n";
                }
                version = issueVersion;
                result += ( "### Opened Issues\n\n" );
                result += ( header );
            }
            else if ( issueVersion != null && !issueVersion.equals( version ) && versionSeparator )
            {
                if ( !version.isEmpty() )
                {
                    result += "\n";
                }
                version = issueVersion;
                result += ( "### Version " + issueVersion + "\n\n" );
                result += ( header );
            }

            result += ( MD_LINE_PREFIX + COLUMN_SEPARATOR );

            result = generateDetail( result, df, columns, issue, issueVersion );
        }
        return result;
    }

    private String generateDetail( String result, DateFormat df, int[] columns, Issue issue, String issueVersion )
    {
        for ( int column : columns )
        {
            switch ( column ) 
            {
            case IssuesReportHelper.COLUMN_ASSIGNEE:
                result += ( MD_COLUMN_SPACE + issue.getAssignee() + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
                break;

            case IssuesReportHelper.COLUMN_COMPONENT:
                result += ( MD_COLUMN_SPACE + IssuesReportHelper.printValues( issue.getComponents() ) + MD_COLUMN_SPACE
                        + COLUMN_SEPARATOR );
                break;

            case IssuesReportHelper.COLUMN_CREATED:
                String created = NOT_AVAILABLE;
                if ( issue.getCreated() != null )
                {
                    created = df.format( issue.getCreated() ) + MD_COLUMN_SPACE + COLUMN_SEPARATOR;
                }
                result += ( MD_COLUMN_SPACE + created );
                break;

            case IssuesReportHelper.COLUMN_FIX_VERSION:
                result += ( MD_COLUMN_SPACE + IssuesReportHelper.printValues( issue.getFixVersions() ) + MD_COLUMN_SPACE
                        + COLUMN_SEPARATOR );
                break;

            case IssuesReportHelper.COLUMN_ID:
                result += ( MD_COLUMN_SPACE + "[" + issue.getId() + "](" + this.issueManagementUrl
                        + ( this.ticketSuffix.isEmpty() ? "" : this.ticketSuffix + "/" ) + issue.getId() + ")"
                        + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
                break;

            case IssuesReportHelper.COLUMN_KEY:
                result += ( MD_COLUMN_SPACE + "[" + issue.getKey() + "](" + issue.getLink() + ")" + MD_COLUMN_SPACE
                        + COLUMN_SEPARATOR );
                break;

            case IssuesReportHelper.COLUMN_PRIORITY:
                result += ( MD_COLUMN_SPACE + issue.getPriority() + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
                break;

            case IssuesReportHelper.COLUMN_REPORTER:
                result += ( MD_COLUMN_SPACE + issue.getReporter() + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
                break;

            case IssuesReportHelper.COLUMN_RESOLUTION:
                result += ( MD_COLUMN_SPACE + issue.getResolution() + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
                break;

            case IssuesReportHelper.COLUMN_STATUS:
                result += ( MD_COLUMN_SPACE + issue.getStatus() + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
                break;

            case IssuesReportHelper.COLUMN_SUMMARY:
                result += ( MD_COLUMN_SPACE + issue.getSummary() + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
                break;

            case IssuesReportHelper.COLUMN_TYPE:
                result += ( MD_COLUMN_SPACE + issue.getType() + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
                break;

            case IssuesReportHelper.COLUMN_UPDATED:
                String updated = NOT_AVAILABLE;
                if ( issue.getUpdated() != null )
                {
                    updated = df.format( issue.getUpdated() );
                }
                result += ( MD_COLUMN_SPACE + updated + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
                break;

            case IssuesReportHelper.COLUMN_VERSION:
                result += ( MD_COLUMN_SPACE + issueVersion + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
                break;

            default:
                // Do not add this column
                break;
            }
        }
        result += "\n";
        return result;
    }

    private String generateHeader()
    {
        String resultHeader = MD_LINE_PREFIX + COLUMN_SEPARATOR;
        String resultHeaderSeparator = COLUMN_SEPARATOR;
        ResourceBundle bundle = getBundle();
        int[] columns = IssuesReportHelper.toIntArray( columnIds );
        for ( int column : columns )
        {
            String headerText = bundle.getString( headerLabelKeys[column] );
            resultHeader += ( " " + headerText + " " + COLUMN_SEPARATOR );
            resultHeaderSeparator += ( " " + StringUtils.repeat( "-", headerText.length() ) + " " + COLUMN_SEPARATOR );
        }
        return resultHeader + "\n" + resultHeaderSeparator + "\n";
    }

    private boolean isOpened( Issue issue )
    {
        return issue.getStatus() != null && !issue.getStatus().equalsIgnoreCase( "closed" );
    }

    private String getVersion( Issue issue )
    {
        String version = issue.getVersion();
        if ( StringUtils.isEmpty( version ) )
        {
            List<String> fixVersions = issue.getFixVersions();
            if ( fixVersions == null || fixVersions.isEmpty() )
            {
                return null;
            }
            else
            {
                return fixVersions.get( 0 );
            }
        }
        return version;
    }

    private ResourceBundle getBundle()
    {
        return ResourceBundle.getBundle( "github-report", locale, this.getClass().getClassLoader() );
    }

}
