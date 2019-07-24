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

package com.hazelcast.client.spi;

import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.map.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelJVMTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.test.HazelcastTestSupport.randomMapName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(HazelcastParallelClassRunner.class)
@Category({QuickTest.class, ParallelJVMTest.class})
public class ClientProxyDestroyTest {

    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    @After
    public void tearDown() {
        hazelcastFactory.terminateAll();
    }

    @Before
    public void setup() {
        hazelcastFactory.newHazelcastInstance();
        client = hazelcastFactory.newHazelcastClient();
    }


    @Test
    public void testUsageAfterDestroy() {
        IAtomicLong proxy = newClientProxy();
        proxy.destroy();
        proxy.get();
    }

    @Test
    public void testMultipleDestroyCalls() {
        IAtomicLong proxy = newClientProxy();
        proxy.destroy();
        proxy.destroy();
    }

    private IAtomicLong newClientProxy() {
        return client.getAtomicLong(HazelcastTestSupport.randomString());
    }

    @Test
    public void testOperationAfterDestroy() throws Exception {
        final String mapName = randomMapName();
        final IMap<Object, Object> clientMap = client.getMap(mapName);
        clientMap.destroy();
        assertFalse(client.getDistributedObjects().contains(clientMap));
        clientMap.put(1, 1);
        assertEquals(1, clientMap.get(1));
    }
}
