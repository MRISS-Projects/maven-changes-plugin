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

import org.apache.maven.plugin.issues.Issue;

/**
 * 
 * @author riss
 *
 */
public class AptIssueListFormater implements IssueListFormater
{

    private static final String COLUMN_SEPARATOR = "|";

    private static final String APT_LINE_PREFIX = "";

    private static final String APT_COLUMN_SPACE = " ";

    private static final String LINE = APT_LINE_PREFIX
            + "*------------+----------+-------+-----------+--------------+\n";
    private static final String HEADER = APT_LINE_PREFIX
            + "|| Ticket Id || Summary || Type || Priority || Resolution ||\n";

    private boolean versionSeparator;

    public AptIssueListFormater( boolean versionSeparator )
    {
        this.versionSeparator = versionSeparator;
    }

    @Override
    public String formatIssueList( List<Issue> issueList )
    {
        String result = "";
        if ( !versionSeparator )
        {
            result = LINE + HEADER + LINE;
        }
        String version = "";
        for ( Iterator<Issue> iterator = issueList.iterator(); iterator.hasNext(); )
        {
            Issue issue = (Issue) iterator.next();
            if ( !issue.getVersion().equals( version ) && versionSeparator )
            {
                version = issue.getVersion();
                result += ( "\n Version " + issue.getVersion() + "\n\n" );
                result += ( LINE + HEADER + LINE );
            }
            result += ( APT_LINE_PREFIX + COLUMN_SEPARATOR );
            result += ( APT_COLUMN_SPACE + issue.getId() + APT_COLUMN_SPACE + COLUMN_SEPARATOR );
            result += ( APT_COLUMN_SPACE + issue.getSummary() + APT_COLUMN_SPACE + COLUMN_SEPARATOR );
            result += ( APT_COLUMN_SPACE + issue.getType() + APT_COLUMN_SPACE + COLUMN_SEPARATOR );
            result += ( APT_COLUMN_SPACE + issue.getPriority() + APT_COLUMN_SPACE + COLUMN_SEPARATOR );
            result += ( APT_COLUMN_SPACE + issue.getResolution() + APT_COLUMN_SPACE + COLUMN_SEPARATOR + "\n" );
            result += LINE;
        }
        return result;
    }

}
