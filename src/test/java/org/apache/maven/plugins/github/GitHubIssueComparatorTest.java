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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.maven.plugins.issues.Issue;

import junit.framework.TestCase;

/**
 * Tests for {@link GitHubIssueComparator}, covering issue #37: newest version first, then most recently updated
 * first, and issues without a version last.
 */
public class GitHubIssueComparatorTest extends TestCase
{

    private static final Date OLDER = new Date( 1000L );

    private static final Date NEWER = new Date( 2000L );

    private Issue issue( String id, String fixVersion, Date updated )
    {
        Issue issue = new Issue();
        issue.setId( id );
        if ( fixVersion != null )
        {
            issue.addFixVersion( fixVersion );
        }
        issue.setUpdated( updated );
        return issue;
    }

    private List<String> sortedIds( Issue... issues )
    {
        List<Issue> list = new ArrayList<Issue>( Arrays.asList( issues ) );
        Collections.sort( list, new GitHubIssueComparator() );
        List<String> ids = new ArrayList<String>();
        for ( Issue issue : list )
        {
            ids.add( issue.getId() );
        }
        return ids;
    }

    /**
     * C1: versions compare as versions, not as digit strings. 3.10.0 was 3100 and sorted below 3.9.10 (3910).
     */
    public void testVersionsCompareAsVersions()
    {
        assertEquals( Arrays.asList( "a", "b", "c" ), sortedIds( issue( "c", "3.9.2", NEWER ),
            issue( "b", "3.9.10", NEWER ), issue( "a", "3.10.0", NEWER ) ) );
    }

    /**
     * C1: a -SNAPSHOT milestone and its released title are the same version.
     */
    public void testSnapshotSuffixDoesNotChangeTheVersion()
    {
        assertEquals( 0, new GitHubIssueComparator().compare( issue( "a", "3.9.0-SNAPSHOT", NEWER ),
            issue( "b", "3.9.0", NEWER ) ) );
    }

    /**
     * C2: within the same version, the most recently updated issue comes first.
     */
    public void testSameVersionOrdersByUpdateDateDescending()
    {
        assertEquals( Arrays.asList( "new", "old" ),
            sortedIds( issue( "old", "3.9.0", OLDER ), issue( "new", "3.9.0", NEWER ) ) );
    }

    /**
     * C3: issues without a version, or with a blank one, sort after every versioned issue.
     */
    public void testIssueWithoutVersionSortsLast()
    {
        assertEquals( Arrays.asList( "v", "none", "blank" ), sortedIds( issue( "none", null, NEWER ),
            issue( "v", "0.0.1", OLDER ), issue( "blank", "", OLDER ) ) );
    }

    /**
     * C3: {@code Issue.getVersion()} takes precedence over the fix versions.
     */
    public void testVersionTakesPrecedenceOverFixVersion()
    {
        Issue versioned = issue( "a", "1.0", NEWER );
        versioned.setVersion( "2.0" );
        assertEquals( Arrays.asList( "a", "b" ), sortedIds( issue( "b", "1.5", NEWER ), versioned ) );
    }

    /**
     * C4: a non-numeric milestone title no longer throws, and sorts after numeric versions but before issues
     * without a version.
     */
    public void testNonNumericTitleSortsWithoutThrowing()
    {
        assertEquals( Arrays.asList( "v", "backlog", "none" ), sortedIds( issue( "none", null, NEWER ),
            issue( "backlog", "Backlog", NEWER ), issue( "v", "1.0", NEWER ) ) );
    }

    /**
     * C5: a missing update date sorts last within its version, and does not throw.
     */
    public void testMissingUpdateDateSortsLastWithinVersion()
    {
        assertEquals( Arrays.asList( "dated", "undated" ),
            sortedIds( issue( "undated", "1.0", null ), issue( "dated", "1.0", OLDER ) ) );
    }

}
