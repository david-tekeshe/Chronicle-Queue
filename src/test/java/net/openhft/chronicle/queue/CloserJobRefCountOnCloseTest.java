package net.openhft.chronicle.queue;

import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.junit.Test;

public class CloserJobRefCountOnCloseTest {

    /**
     * @see <a>https://github.com/OpenHFT/Chronicle-Queue/issues/664</a>
     */
    @Test
    public void test() {
        try (SingleChronicleQueue temp = SingleChronicleQueueBuilder.binary(DirectoryUtils.tempDir("temp")).build()) {
            temp.acquireAppender().writeText("hello");
            ExcerptTailer tailer = temp.createTailer();
            String s = tailer.readText();
            tailer.getCloserJob().run();
        }
    }
}
