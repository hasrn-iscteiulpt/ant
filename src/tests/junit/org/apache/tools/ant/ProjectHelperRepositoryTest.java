/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.tools.ant;

import java.io.File;

import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.StringResource;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Testing around the management of the project helpers
 */
public class ProjectHelperRepositoryTest {

    public static class SomeHelper extends ProjectHelper {
        public boolean canParseBuildFile(Resource buildFile) {
            return buildFile instanceof FileResource
                    && buildFile.getName().endsWith(".myext");
        }

        public boolean canParseAntlibDescriptor(Resource r) {
            return r instanceof FileResource && r.getName().endsWith(".myext");
        }
    }

    @Test
    public void testFind() {
        ProjectHelperRepository repo = ProjectHelperRepository.getInstance();
        repo.registerProjectHelper(SomeHelper.class);

        Resource r = new FileResource(new File("test.xml"));
        ProjectHelper helper = repo.getProjectHelperForBuildFile(r);
        assertTrue(helper instanceof ProjectHelper2);
        helper = repo.getProjectHelperForAntlib(r);
        assertTrue(helper instanceof ProjectHelper2);

        r = new FileResource(new File("test.myext"));
        helper = repo.getProjectHelperForBuildFile(r);
        assertTrue(helper instanceof SomeHelper);
        helper = repo.getProjectHelperForAntlib(r);
        assertTrue(helper instanceof SomeHelper);

        r = new StringResource("test.myext");
        helper = repo.getProjectHelperForBuildFile(r);
        assertTrue(helper instanceof ProjectHelper2);
        helper = repo.getProjectHelperForAntlib(r);
        assertTrue(helper instanceof ProjectHelper2);

        r = new StringResource("test.other");
        helper = repo.getProjectHelperForBuildFile(r);
        assertTrue(helper instanceof ProjectHelper2);
        helper = repo.getProjectHelperForAntlib(r);
        assertTrue(helper instanceof ProjectHelper2);
    }

    @Test(expected = BuildException.class)
    public void testNoDefaultConstructor() {

        class IncorrectHelper extends ProjectHelper {
            // the default constructor is not visible to ant here
        }

        ProjectHelperRepository.getInstance().registerProjectHelper(IncorrectHelper.class);
        // TODO we should be asserting a value in here
    }

    @Test(expected = BuildException.class)
    public void testUnknownHelper() {
        ProjectHelperRepository.getInstance().registerProjectHelper("xxx.yyy.zzz.UnknownHelper");
        // TODO we should be asserting a value in here
    }
}
