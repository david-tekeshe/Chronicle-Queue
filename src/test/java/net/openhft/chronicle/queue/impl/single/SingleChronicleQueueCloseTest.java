/*
 * Copyright 2016-2020 chronicle.software
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

import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.queue.*;
import net.openhft.chronicle.wire.WireType;
import org.junit.Assert;
import org.junit.Test;

import static net.openhft.chronicle.queue.impl.single.ThreadLocalAppender.acquireThreadLocalAppender;

public class SingleChronicleQueueCloseTest extends QueueTestCommon {

    @Test
    public void testTailAfterClose() {
        try (final ChronicleQueue queue = SingleChronicleQueueBuilder.builder(getTmpDir(), WireType.BINARY).build()) {
            final ExcerptAppender appender = queue.createAppender();
            appender.writeDocument(w -> w.write(TestKey.test).int32(1));
            Closeable.closeQuietly(queue);
            try {
                appender.writeDocument(w -> w.write(TestKey.test).int32(2));
                Assert.fail();
            } catch (IllegalStateException e) {
                // ok
            }
        }
    }

    /**
     * NOTE: Still uses thread local appender as that is the intent of the test.
     */
    @Test
    public void reacquireAppenderAfterClose() {
        try (final ChronicleQueue queue = SingleChronicleQueueBuilder.builder(getTmpDir(), WireType.BINARY).build()) {
            final ExcerptAppender appender = acquireThreadLocalAppender(queue);
            appender.writeText("hello1");
            appender.close();

            final ExcerptAppender appender2 = acquireThreadLocalAppender(queue);
            appender2.writeText("hello2");
            appender.close();

            final ExcerptAppender appender3 = acquireThreadLocalAppender(queue);
            appender2.writeText("hello3");

            final ExcerptAppender appender4 = acquireThreadLocalAppender(queue);
            appender2.writeText("hello4");

            Assert.assertSame(appender3, appender4);

            final ExcerptTailer tailer = queue.createTailer();

            Assert.assertEquals("hello1", tailer.readText());
            Assert.assertEquals("hello2", tailer.readText());
            Assert.assertEquals("hello3", tailer.readText());
            Assert.assertEquals("hello4", tailer.readText());
        }
    }
}
