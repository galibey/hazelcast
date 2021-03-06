/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.splitbrainprotection.atomic;

import com.hazelcast.cp.IAtomicReference;
import com.hazelcast.splitbrainprotection.AbstractSplitBrainProtectionTest;
import com.hazelcast.splitbrainprotection.SplitBrainProtectionException;
import com.hazelcast.splitbrainprotection.SplitBrainProtectionOn;
import com.hazelcast.test.HazelcastSerialParametersRunnerFactory;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelJVMTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

import static com.hazelcast.splitbrainprotection.AbstractSplitBrainProtectionTest.SplitBrainProtectionTestClass.object;
import static com.hazelcast.test.HazelcastTestSupport.smallInstanceConfig;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.isA;

@RunWith(Parameterized.class)
@UseParametersRunnerFactory(HazelcastSerialParametersRunnerFactory.class)
@Category({QuickTest.class, ParallelJVMTest.class})
public class AtomicReferenceSplitBrainProtectionReadTest extends AbstractSplitBrainProtectionTest {

    @Parameters(name = "splitBrainProtectionType:{0}")
    public static Iterable<Object[]> parameters() {
        return asList(new Object[][]{{SplitBrainProtectionOn.READ}, {SplitBrainProtectionOn.READ_WRITE}});
    }

    @Parameter
    public static SplitBrainProtectionOn splitBrainProtectionOn;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setUp() {
        initTestEnvironment(smallInstanceConfig(), new TestHazelcastInstanceFactory());
    }

    @AfterClass
    public static void tearDown() {
        shutdownTestEnvironment();
    }

    @Test
    public void get_splitBrainProtection() {
        aref(0).get();
    }

    @Test(expected = SplitBrainProtectionException.class)
    public void get_noSplitBrainProtection() {
        aref(3).get();
    }

    @Test
    public void getAsync_splitBrainProtection() throws Exception {
        aref(0).getAsync().get();
    }

    @Test
    public void getAsync_noSplitBrainProtection() throws Exception {
        expectedException.expectCause(isA(SplitBrainProtectionException.class));
        aref(3).getAsync().get();
    }

    @Test
    public void isNull_splitBrainProtection() {
        aref(0).isNull();
    }

    @Test(expected = SplitBrainProtectionException.class)
    public void isNull_noSplitBrainProtection() {
        aref(3).isNull();
    }

    @Test
    public void isNullAsync_splitBrainProtection() throws Exception {
        aref(0).isNullAsync().get();
    }

    @Test
    public void isNullAsync_noSplitBrainProtection() throws Exception {
        expectedException.expectCause(isA(SplitBrainProtectionException.class));
        aref(3).isNullAsync().get();
    }

    @Test
    public void contains_splitBrainProtection() {
        aref(0).contains(object());
    }

    @Test(expected = SplitBrainProtectionException.class)
    public void contains_noSplitBrainProtection() {
        aref(3).contains(object());
    }

    @Test
    public void containsAsync_splitBrainProtection() throws Exception {
        aref(0).containsAsync(object()).get();
    }

    @Test
    public void containsAsync_noSplitBrainProtection() throws Exception {
        expectedException.expectCause(isA(SplitBrainProtectionException.class));
        aref(3).containsAsync(object()).get();
    }

    private IAtomicReference<SplitBrainProtectionTestClass> aref(int index) {
        return aref(index, splitBrainProtectionOn);
    }
}
