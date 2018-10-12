package org.apache.maven.plugin.github.stubs;

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

import org.apache.maven.artifact.handler.DefaultArtifactHandler;

/**
 * @author <a href="mailto:marcelo.riss@gmail.com">Marcelo Riss</a>
 * @version 2018 Oct, 4
 */
public class DefaultArtifactHandlerStub
    extends DefaultArtifactHandler
{
    private String language;

    @Override
    public String getLanguage()
    {
        if ( language == null )
        {
            language = "java";
        }

        return language;
    }

    /**
     * @param language the language. Defaults to "java".
     */
    public void setLanguage( String language )
    {
        this.language = language;
    }
}
