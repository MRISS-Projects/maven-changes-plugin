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

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.apache.maven.plugins.issues.Issue;

/**
 * Orders GitHub issues newest version first, then most recently updated first. Issues without a version sort last,
 * and so do issues without an update date within their version. Versions compare as Maven versions, after any
 * "-SNAPSHOT" suffix is removed, so a milestone and its released title are the same version.
 */
public class GitHubIssueComparator implements Comparator<Issue>
{

    @Override
    public int compare( Issue o1, Issue o2 )
    {
        int result = compareVersions( getVersion( o1 ), getVersion( o2 ) );
        if ( result == 0 )
        {
            result = compareDates( o1.getUpdated(), o2.getUpdated() );
        }
        return result;
    }

    private int compareVersions( ComparableVersion version1, ComparableVersion version2 )
    {
        if ( version1 == null || version2 == null )
        {
            return nullsLast( version1, version2 );
        }
        return version2.compareTo( version1 );
    }

    private int compareDates( Date date1, Date date2 )
    {
        if ( date1 == null || date2 == null )
        {
            return nullsLast( date1, date2 );
        }
        return date2.compareTo( date1 );
    }

    private int nullsLast( Object value1, Object value2 )
    {
        if ( value1 == value2 )
        {
            return 0;
        }
        return value1 == null ? 1 : -1;
    }

    private ComparableVersion getVersion( Issue issue )
    {
        String version = issue.getVersion();
        if ( StringUtils.isBlank( version ) )
        {
            List<String> versions = issue.getFixVersions();
            if ( versions == null || versions.isEmpty() || StringUtils.isBlank( versions.get( 0 ) ) )
            {
                return null;
            }
            version = versions.get( 0 );
        }
        return new ComparableVersion( version.replaceAll( "-SNAPSHOT", "" ) );
    }

}
