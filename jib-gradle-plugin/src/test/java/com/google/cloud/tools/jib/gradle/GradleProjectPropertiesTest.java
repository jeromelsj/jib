/*
 * Copyright 2018 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.tools.jib.gradle;

import com.google.cloud.tools.jib.builder.SourceFilesConfiguration;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import org.gradle.api.Project;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.java.archives.Manifest;
import org.gradle.api.java.archives.internal.DefaultManifest;
import org.gradle.jvm.tasks.Jar;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/** Test for {@link GradleProjectProperties}. */
@RunWith(MockitoJUnitRunner.class)
public class GradleProjectPropertiesTest {

  @Mock private FileResolver mockFileResolver;
  @Mock private Jar mockJar;
  @Mock private Jar mockJar2;
  @Mock private Project mockProject;
  @Mock private GradleBuildLogger mockGradleBuildLogger;
  @Mock private SourceFilesConfiguration mockSourceFilesConfiguration;

  private Manifest manifest;
  private GradleProjectProperties gradleProjectProperties;

  @Before
  public void setup() {
    manifest = new DefaultManifest(mockFileResolver);
    Mockito.when(mockJar.getManifest()).thenReturn(manifest);

    gradleProjectProperties =
        new GradleProjectProperties(
            mockProject, mockGradleBuildLogger, mockSourceFilesConfiguration);
  }

  @Test
  public void testGetMainClassFromJar_success() {
    manifest.attributes(ImmutableMap.of("Main-Class", "some.main.class"));
    Mockito.when(mockProject.getTasksByName("jar", false)).thenReturn(ImmutableSet.of(mockJar));
    Assert.assertEquals("some.main.class", gradleProjectProperties.getMainClassFromJar());
  }

  @Test
  public void testGetMainClassFromJar_missing() {
    Mockito.when(mockProject.getTasksByName("jar", false)).thenReturn(Collections.emptySet());
    Assert.assertEquals(null, gradleProjectProperties.getMainClassFromJar());
  }

  @Test
  public void testGetMainClassFromJar_multiple() {
    manifest.attributes(ImmutableMap.of("Main-Class", "some.main.class"));
    Mockito.when(mockProject.getTasksByName("jar", false))
        .thenReturn(ImmutableSet.of(mockJar, mockJar2));
    Assert.assertEquals(null, gradleProjectProperties.getMainClassFromJar());
  }
}
