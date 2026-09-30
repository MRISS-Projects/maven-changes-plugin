package org.apache.maven.plugins.issues;

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

/**
 * Stands in for the issues of a closed milestone that has none, so that the milestone still gets its section in a
 * text issue list. Formatters render it as a single "no issues" row.
 *
 * @since 2.12.10
 */
public class EmptyMilestoneIssue
    extends Issue
{

    /** The Summary cell of the "no issues" row. */
    public static final String SUMMARY = "No issues";

    /**
     * @param milestone the title of the closed milestone that has no issues. It becomes both the version and the
     *            fix version, since formatters group sections by one or the other.
     */
    public EmptyMilestoneIssue( String milestone )
    {
        setVersion( milestone );
        addFixVersion( milestone );
        setStatus( "closed" );
        setSummary( SUMMARY );
    }

}
