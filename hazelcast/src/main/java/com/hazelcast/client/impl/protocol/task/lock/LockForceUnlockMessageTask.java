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

package com.hazelcast.client.impl.protocol.task.lock;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.LockForceUnlockCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.cp.internal.datastructures.unsafe.lock.InternalLockNamespace;
import com.hazelcast.cp.internal.datastructures.unsafe.lock.LockService;
import com.hazelcast.cp.internal.datastructures.unsafe.lock.operations.UnlockOperation;
import com.hazelcast.instance.impl.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.security.permission.ActionConstants;
import com.hazelcast.security.permission.LockPermission;
import com.hazelcast.spi.impl.operationservice.Operation;

import java.security.Permission;

public class LockForceUnlockMessageTask
        extends AbstractPartitionMessageTask<LockForceUnlockCodec.RequestParameters> {

    public LockForceUnlockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        final Data key = serializationService.toData(parameters.name, StringPartitioningStrategy.INSTANCE);
        return new UnlockOperation(new InternalLockNamespace(parameters.name), key, -1, true, parameters.referenceId);
    }

    @Override
    protected LockForceUnlockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return LockForceUnlockCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return LockForceUnlockCodec.encodeResponse();
    }


    @Override
    public String getServiceName() {
        return LockService.SERVICE_NAME;
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(parameters.name, ActionConstants.ACTION_LOCK);
    }

    @Override
    public String getDistributedObjectName() {
        return parameters.name;
    }

    @Override
    public String getMethodName() {
        return "forceUnlock";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}
