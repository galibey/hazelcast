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

package com.hazelcast.cp.internal.datastructures.unsafe.atomicreference.operations;

import com.hazelcast.cp.internal.datastructures.unsafe.atomicreference.AtomicReferenceContainer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.operationservice.Operation;
import com.hazelcast.spi.impl.operationservice.MutatingOperation;

import java.io.IOException;

import static com.hazelcast.cp.internal.datastructures.unsafe.atomicreference.AtomicReferenceDataSerializerHook.GET_AND_SET;

public class GetAndSetOperation extends AtomicReferenceBackupAwareOperation implements MutatingOperation {

    private Data newValue;
    private Data returnValue;

    public GetAndSetOperation() {
    }

    public GetAndSetOperation(String name, Data newValue) {
        super(name);
        this.newValue = newValue;
    }

    @Override
    public void run() throws Exception {
        AtomicReferenceContainer container = getReferenceContainer();
        returnValue = container.getAndSet(newValue);
    }

    @Override
    public Object getResponse() {
        return returnValue;
    }

    @Override
    public Operation getBackupOperation() {
        return new SetBackupOperation(name, newValue);
    }

    @Override
    public int getClassId() {
        return GET_AND_SET;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(newValue);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        newValue = in.readData();
    }
}

