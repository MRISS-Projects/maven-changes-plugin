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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugins.issues.Issue;

import junit.framework.TestCase;

/**
 * 
 * @author riss
 *
 */
public class AptIssueListFormaterTest extends TestCase
{

    public void testFormatIssueList()
    {
        AptIssueListFormater formatter = new AptIssueListFormater( false );
        List<Issue> issues = new ArrayList<Issue>();
        Issue issue = new Issue();
        issue.setId( "test" );
        issue.setSummary( "the summary" );
        issue.setType( "type" );
        issue.setPriority( "priority" );
        issue.setResolution( "resolution" );
        issues.add( issue );
        issue.setVersion( "the version" );
        assertNotNull( formatter.formatIssueList( issues ) );
    }

    /**
     * Issue #36: the Type cell of an issue without a type (a GitHub issue with no label) shows {@code n/a},
     * never the literal {@code null}. Priority and resolution are set, so only Type could print {@code null}.
     */
    public void testIssueWithoutTypeShowsNotAvailable()
    {
        AptIssueListFormater formatter = new AptIssueListFormater( false );
        List<Issue> issues = new ArrayList<Issue>();
        Issue issue = new Issue();
        issue.setId( "8" );
        issue.setSummary( "the summary" );
        issue.setPriority( "priority" );
        issue.setResolution( "resolution" );
        issue.setVersion( "the version" );
        issues.add( issue );

        String output = formatter.formatIssueList( issues );

        assertTrue( "The Type cell must show n/a, in: " + output,
            output.contains( "| 8 | the summary | n/a | priority | resolution |" ) );
        assertFalse( "No cell may print null, in: " + output, output.contains( "null" ) );
    }

}
