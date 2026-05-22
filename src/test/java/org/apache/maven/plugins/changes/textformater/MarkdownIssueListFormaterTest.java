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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugins.issues.Issue;
import org.apache.maven.plugins.issues.IssuesReportHelper;

import junit.framework.TestCase;

/**
 * Tests for {@link MarkdownIssueListFormater}, covering issue #33:
 * table separator cells must contain at least 3 dashes so that
 * flexmark-ext-tables (MIN_SEPARATOR_DASHES=3) recognises the row as a
 * delimiter row and produces a proper HTML/PDF table.
 *
 * @author riss
 */
public class MarkdownIssueListFormaterTest extends TestCase
{

    private static final String ISSUE_MANAGEMENT_URL = "https://github.com/example/project/issues";

    /** Matches a GFM table separator cell, e.g. {@code | --- |}. */
    private static final Pattern SEPARATOR_CELL = Pattern.compile( "\\|\\s*(-+)\\s*(?=\\|)" );

    // ------------------------------------------------------------------
    // helper
    // ------------------------------------------------------------------

    private MarkdownIssueListFormater createFormatter( List<Integer> columnIds )
    {
        return new MarkdownIssueListFormater( false, ISSUE_MANAGEMENT_URL, "", columnIds, Locale.ENGLISH, 2 );
    }

    private Issue buildIssue( String id, String summary, String type, String version )
    {
        Issue issue = new Issue();
        issue.setId( id );
        issue.setSummary( summary );
        issue.setType( type );
        issue.setVersion( version );
        issue.setStatus( "open" );
        issue.setPriority( "normal" );
        issue.setAssignee( "someone" );
        issue.setReporter( "reporter" );
        issue.setResolution( "unresolved" );
        return issue;
    }

    /**
     * Extracts the separator line from the generated markdown (the second line
     * of the header block which starts with {@code |---}).
     */
    private String extractSeparatorLine( String markdown )
    {
        for ( String line : markdown.split( "\n" ) )
        {
            if ( line.startsWith( "|" ) && line.contains( "---" ) )
            {
                return line;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------

    /**
     * Issue #33: when the COLUMN_ID column is included, the generated separator
     * for that column must contain at least 3 dashes.  The label for COLUMN_ID
     * is {@code #} (length 1), which previously produced {@code | - |}.
     */
    public void testSeparatorForIdColumnHasAtLeastThreeDashes()
    {
        List<Integer> columns = Arrays.asList( IssuesReportHelper.COLUMN_ID, IssuesReportHelper.COLUMN_SUMMARY );
        MarkdownIssueListFormater formatter = createFormatter( columns );

        List<Issue> issues = new ArrayList<Issue>();
        issues.add( buildIssue( "42", "Some bug", "bug", "1.0" ) );

        String output = formatter.formatIssueList( issues );
        String separatorLine = extractSeparatorLine( output );

        assertNotNull( "Separator line must be present in the output", separatorLine );

        Matcher m = SEPARATOR_CELL.matcher( separatorLine );
        while ( m.find() )
        {
            int dashes = m.group( 1 ).length();
            assertTrue(
                "Each separator cell must contain at least 3 dashes, but found " + dashes + " in: " + separatorLine,
                dashes >= 3 );
        }
    }

    /**
     * The separator line must not contain the pattern {@code | - |} or
     * {@code | -- |} (fewer than 3 dashes) for any column.
     */
    public void testNoSeparatorCellWithFewerThanThreeDashes()
    {
        // Use all available columns so every label length is exercised.
        List<Integer> columns = Arrays.asList(
            IssuesReportHelper.COLUMN_ASSIGNEE,
            IssuesReportHelper.COLUMN_CREATED,
            IssuesReportHelper.COLUMN_FIX_VERSION,
            IssuesReportHelper.COLUMN_ID,
            IssuesReportHelper.COLUMN_REPORTER,
            IssuesReportHelper.COLUMN_STATUS,
            IssuesReportHelper.COLUMN_SUMMARY,
            IssuesReportHelper.COLUMN_TYPE,
            IssuesReportHelper.COLUMN_UPDATED,
            IssuesReportHelper.COLUMN_VERSION
        );
        MarkdownIssueListFormater formatter = createFormatter( columns );

        List<Issue> issues = new ArrayList<Issue>();
        issues.add( buildIssue( "1", "Test issue", "bug", "2.0" ) );

        String output = formatter.formatIssueList( issues );
        String separatorLine = extractSeparatorLine( output );

        assertNotNull( "Separator line must be present in the output", separatorLine );
        assertFalse( "Separator line must not contain '| - |'", separatorLine.contains( "| - |" ) );
        assertFalse( "Separator line must not contain '| -- |'", separatorLine.contains( "| -- |" ) );

        Matcher m = SEPARATOR_CELL.matcher( separatorLine );
        while ( m.find() )
        {
            int dashes = m.group( 1 ).length();
            assertTrue(
                "Each separator cell must contain at least 3 dashes, but found " + dashes + " in: " + separatorLine,
                dashes >= 3 );
        }
    }

    /**
     * Verify that the formatter still produces a non-null, non-empty result
     * for a basic set of columns (smoke test).
     */
    public void testFormatIssueListProducesOutput()
    {
        List<Integer> columns = Arrays.asList( IssuesReportHelper.COLUMN_ID, IssuesReportHelper.COLUMN_SUMMARY,
            IssuesReportHelper.COLUMN_TYPE );
        MarkdownIssueListFormater formatter = createFormatter( columns );

        List<Issue> issues = new ArrayList<Issue>();
        issues.add( buildIssue( "7", "A summary", "feature", "1.1" ) );

        String output = formatter.formatIssueList( issues );
        assertNotNull( output );
        assertTrue( "Output must not be empty", output.length() > 0 );
        assertTrue( "Output must contain the issue id", output.contains( "7" ) );
        assertTrue( "Output must contain the summary", output.contains( "A summary" ) );
    }

}
