/*
 * Copyright 2014-2024 chronicle.software
 *
 *       https://chronicle.software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.chronicle.queue.impl.single;

import net.openhft.chronicle.queue.impl.TableStore;
import net.openhft.chronicle.threads.TimingPauser;

import java.util.function.Supplier;

public class AppendLock extends TableStoreWriteLock {

    public AppendLock(TableStore<?> tableStore, Supplier<TimingPauser> pauser, Long timeoutMs) {
        super(tableStore, pauser, timeoutMs, TableStoreWriteLock.APPEND_LOCK_KEY);
    }

    @Override
    public boolean forceUnlockIfProcessIsDead() {
        return super.forceUnlockIfProcessIsDead(false);
    }
}
