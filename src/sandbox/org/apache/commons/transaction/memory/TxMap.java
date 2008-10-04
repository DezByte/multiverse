/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.transaction.memory;

import java.util.Map;

import org.apache.commons.transaction.ManageableResourceManager;
import org.apache.commons.transaction.TransactionalResourceManager;

/**
 * Interface for a map that features transactional support.
 * 
 * <p>Start a transaction by calling {@link TransactionalResourceManager#startTransaction(long, java.util.concurrent.TimeUnit)}. Then perform the
 * normal actions on the map and finally either call
 * {@link TransactionalResourceManager#commitTransaction()} to make your changes permanent or
 * {@link TransactionalResourceManager#rollbackTransaction()} to undo them.
 * 
 * <p><em>Caution:</em> Do not modify values retrieved by {@link Map#get(Object)} as
 * this will circumvent the transactional mechanism. Rather clone the value or
 * copy it in a way you see fit and store it back using
 * {@link Map#put(Object, Object)}. <br>
 * 
 * @see BasicTxMap
 * @see OptimisticTxMap
 * @see PessimisticTxMap
 * 
 */
public interface TxMap<K, V> extends Map<K, V>, ManageableResourceManager {
    /**
     * Gets the underlying map that is wrapped by this transactional implementation.
     * 
     * @return the wrapped map
     */
    Map<K, V> getWrappedMap();
}
