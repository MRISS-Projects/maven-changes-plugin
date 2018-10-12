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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.issues.Issue;

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

    private static final String HEADER = MD_LINE_PREFIX + "| Ticket Id | Summary | Type | Priority | Resolution |\n"
            + "| --------- | ------- | ---- | -------- | ---------- |\n";
    private boolean versionSeparator;

    private String issueManagementUrl;

    private String ticketSuffix;

    public MarkdownIssueListFormater( boolean versionSeparator, String issueManagementUrl, String ticketSuffix )
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
    }

    @Override
    public String formatIssueList( List<Issue> issueList )
    {
        String result = "";
        if ( !versionSeparator )
        {
            result = HEADER;
        }
        String version = "";
        for ( Iterator<Issue> iterator = issueList.iterator(); iterator.hasNext(); )
        {
            Issue issue = (Issue) iterator.next();
            String issueVersion = getVersion( issue );
            if ( StringUtils.isEmpty( issueVersion ) || issue == null )
            {
                continue;
            }
            if ( !issueVersion.equals( version ) && versionSeparator )
            {
                if ( !version.isEmpty() )
                {
                    result += "\n";
                }
                version = issueVersion;
                result += ( "### Version " + issueVersion + "\n\n" );
                result += ( HEADER );
            }
            result += ( MD_LINE_PREFIX + COLUMN_SEPARATOR );
            result += ( MD_COLUMN_SPACE + "[" + issue.getId() + "](" + this.issueManagementUrl
                    + ( this.ticketSuffix.isEmpty() ? "" : this.ticketSuffix + "/" ) + issue.getId() + ")"
                    + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
            result += ( MD_COLUMN_SPACE + issue.getSummary() + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
            result += ( MD_COLUMN_SPACE + issue.getType() + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
            if ( issue.getPriority() != null )
            {
                result += ( MD_COLUMN_SPACE + issue.getPriority() + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
            }
            else
            {
                result += ( MD_COLUMN_SPACE + "N/A" + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
            }
            if ( issue.getResolution() != null )
            {
                result += ( MD_COLUMN_SPACE + issue.getResolution() + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
            }
            else
            {
                result += ( MD_COLUMN_SPACE + "N/A" + MD_COLUMN_SPACE + COLUMN_SEPARATOR );
            }
            result += "\n";
        }
        return result;
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

}
