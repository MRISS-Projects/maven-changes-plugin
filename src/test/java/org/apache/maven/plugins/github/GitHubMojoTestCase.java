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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.maven.plugins.issues.EmptyMilestoneIssue;
import org.apache.maven.plugins.issues.Issue;

import junit.framework.TestCase;

/**
 * Tests for {@link GitHubMojo#executeReport(Locale)}. Issue #37: it hands {@code generateReport} the issues newest
 * version first, then most recently updated first, for {@code github-report} and {@code github-text-list} alike.
 * Issue #38: {@code github-text-list} adds a placeholder for each closed milestone with no issues, when asked to.
 */
public class GitHubMojoTestCase extends TestCase
{

    /** The downloaded issues, in an order the API could return them. */
    private List<Issue> unsortedIssues()
    {
        return new ArrayList<Issue>( Arrays.asList( issue( "none", null, 3000L ), issue( "old", "3.9.10", 1000L ),
            issue( "newest", "3.10.0", 1000L ), issue( "new", "3.9.10", 2000L ) ) );
    }

    private static final List<String> EXPECTED_ORDER = Arrays.asList( "newest", "new", "old", "none" );

    private Issue issue( String id, String fixVersion, long updated )
    {
        Issue issue = new Issue();
        issue.setId( id );
        if ( fixVersion != null )
        {
            issue.addFixVersion( fixVersion );
        }
        issue.setUpdated( new Date( updated ) );
        return issue;
    }

    private GitHubDownloader downloaderReturning( List<Issue> issues )
        throws Exception
    {
        GitHubDownloader downloader = mock( GitHubDownloader.class );
        when( downloader.getIssueList() ).thenReturn( issues );
        return downloader;
    }

    private static void setColumnNames( GitHubMojo mojo )
        throws Exception
    {
        Field field = GitHubMojo.class.getDeclaredField( "columnNames" );
        field.setAccessible( true );
        field.set( mojo, "Id,Type,Summary" );
    }

    private static List<String> ids( List<Issue> issues )
    {
        List<String> ids = new ArrayList<String>();
        for ( Issue issue : issues )
        {
            ids.add( issue.getId() );
        }
        return ids;
    }

    /**
     * M1: github-report sorts before generating the report.
     */
    public void testGitHubReportReceivesSortedIssues()
        throws Exception
    {
        final GitHubDownloader downloader = downloaderReturning( unsortedIssues() );
        final List<List<Issue>> received = new ArrayList<List<Issue>>();
        GitHubMojo mojo = new GitHubMojo()
        {
            @Override
            protected GitHubDownloader createDownloader()
            {
                return downloader;
            }

            @Override
            protected void generateReport( Locale locale, List<Integer> columnIds, List<Issue> issueList )
            {
                received.add( issueList );
            }
        };
        setColumnNames( mojo );

        mojo.executeReport( Locale.ENGLISH );

        assertEquals( 1, received.size() );
        assertEquals( EXPECTED_ORDER, ids( received.get( 0 ) ) );
    }

    /**
     * M2: github-text-list gets the same order from the same sort.
     */
    public void testGitHubTextListReceivesSortedIssues()
        throws Exception
    {
        final GitHubDownloader downloader = downloaderReturning( unsortedIssues() );
        final List<List<Issue>> received = new ArrayList<List<Issue>>();
        GitHubTextListMojo mojo = new GitHubTextListMojo()
        {
            @Override
            protected GitHubDownloader createDownloader()
            {
                return downloader;
            }

            @Override
            protected void generateReport( Locale locale, List<Integer> columnIds, List<Issue> issueList )
            {
                received.add( issueList );
            }
        };
        setColumnNames( mojo );

        mojo.executeReport( Locale.ENGLISH );

        assertEquals( 1, received.size() );
        assertEquals( EXPECTED_ORDER, ids( received.get( 0 ) ) );
    }

    /** A text-list mojo that downloads from the given downloader and records what reaches generateReport. */
    private GitHubTextListMojo textListMojo( final GitHubDownloader downloader, final List<List<Issue>> received,
                                             boolean includeEmptyMilestones )
        throws Exception
    {
        GitHubTextListMojo mojo = new GitHubTextListMojo()
        {
            @Override
            protected GitHubDownloader createDownloader()
            {
                return downloader;
            }

            @Override
            protected void generateReport( Locale locale, List<Integer> columnIds, List<Issue> issueList )
            {
                received.add( issueList );
            }
        };
        setColumnNames( mojo );
        Field field = GitHubTextListMojo.class.getDeclaredField( "includeEmptyMilestones" );
        field.setAccessible( true );
        field.set( mojo, includeEmptyMilestones );
        return mojo;
    }

    /** Ids of the issues, with each placeholder shown as {@code empty:<milestone>}. */
    private static List<String> idsWithPlaceholders( List<Issue> issues )
    {
        List<String> ids = new ArrayList<String>();
        for ( Issue issue : issues )
        {
            ids.add( issue instanceof EmptyMilestoneIssue ? "empty:" + issue.getFixVersions().get( 0 ) : issue.getId() );
        }
        return ids;
    }

    /**
     * #38 M1: with includeEmptyMilestones set, a closed milestone with no issues gets one placeholder, sorted into
     * version order; milestones that have issues get none.
     */
    public void testTextListAddsPlaceholderForEmptyClosedMilestone()
        throws Exception
    {
        GitHubDownloader downloader = downloaderReturning( new ArrayList<Issue>(
            Arrays.asList( issue( "old", "0.3.0", 1000L ), issue( "new", "0.3.2", 1000L ) ) ) );
        when( downloader.getClosedMilestoneTitles() ).thenReturn( Arrays.asList( "0.3.2", "0.3.1", "0.3.0" ) );
        List<List<Issue>> received = new ArrayList<List<Issue>>();

        textListMojo( downloader, received, true ).executeReport( Locale.ENGLISH );

        assertEquals( 1, received.size() );
        assertEquals( Arrays.asList( "new", "empty:0.3.1", "old" ), idsWithPlaceholders( received.get( 0 ) ) );
    }

    /**
     * #38 M2: off by default, the list is the downloaded issues only, and no milestones are fetched.
     */
    public void testTextListWithoutIncludeEmptyMilestonesIsUnchanged()
        throws Exception
    {
        GitHubDownloader downloader = downloaderReturning( new ArrayList<Issue>(
            Arrays.asList( issue( "old", "0.3.0", 1000L ), issue( "new", "0.3.2", 1000L ) ) ) );
        List<List<Issue>> received = new ArrayList<List<Issue>>();

        textListMojo( downloader, received, false ).executeReport( Locale.ENGLISH );

        assertEquals( 1, received.size() );
        assertEquals( Arrays.asList( "new", "old" ), idsWithPlaceholders( received.get( 0 ) ) );
        verify( downloader, never() ).getClosedMilestoneTitles();
    }

}
