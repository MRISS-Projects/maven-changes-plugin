package org.apache.maven.plugins.changes.textformater;

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

/**
 * 
 * @author riss
 *
 */
public class IssueListFormatterFactory
{

    // CHECKSTYLE_OFF: StaticVariableName
    private static IssueListFormatterFactory INSTANCE;
    // CHECKSTYLE_ON: StaticVariableName

    private IssueListFormatterFactory()
    {

    }

    public static IssueListFormatterFactory getInstance()
    {
        if ( INSTANCE == null )
        {
            INSTANCE = new IssueListFormatterFactory();
        }
        return INSTANCE;
    }

    public IssueListFormater getIssueListFormatter( String formatterId, boolean versionSeparator,
            String issueManagementUrl, String ticketSuffix, List<Integer> columnIds, Locale locale,
            int subtitleLevelNumber )
    {
        if ( formatterId.equals( "markdown" ) )
        {
            return new MarkdownIssueListFormater( versionSeparator, issueManagementUrl, ticketSuffix, columnIds,
                    locale, subtitleLevelNumber );
        }
        else if ( formatterId.equals( "apt" ) )
        {
            return new AptIssueListFormater( versionSeparator );
        }
        else
        {
            throw new IllegalArgumentException( "Illegal issue formatter id: " + formatterId );
        }
    }

}
