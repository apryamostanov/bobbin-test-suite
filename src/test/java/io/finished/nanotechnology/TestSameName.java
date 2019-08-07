package io.finished.nanotechnology;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author olegzinovev
 * @since 2019-08-07
 **/
public class TestSameName {

    private static final Logger logger = LoggerFactory.getLogger(TestSameName.class);

    private static final AtomicInteger preLog = new AtomicInteger(0);
    private static final AtomicInteger postLog = new AtomicInteger(0);
    private static final AtomicInteger errors = new AtomicInteger(0);
    private static final int ITERATIONS = 1000;

    private static volatile String text;

    @Test
    public void testSameName() throws InterruptedException, IOException {
        // Необходимо чтобы размер строки превышал размер буфера по умолчанию в StreamEncoder (Используется внутри FileWriter).
        text = RandomStringUtils.randomAlphabetic(8192 + 1);


        List<Worker> workers = IntStream.range(0, Runtime.getRuntime().availableProcessors()).mapToObj(ignored -> new Worker())
                .collect(Collectors.toList());

        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).invokeAll(workers);

        int expected = ITERATIONS * Runtime.getRuntime().availableProcessors();
        Assert.assertEquals(expected, preLog.get());
        Assert.assertEquals(expected, postLog.get());
        Assert.assertEquals(0, errors.get());

        boolean correct = Files.lines(Paths.get("./target/application.log"))
                // Строка должна заканчиваться нашим текстом
                .allMatch(l -> {
                    boolean correctLine = l.isEmpty() || l.endsWith(text);
                    if (!correctLine) {
                        System.out.println(l);
                    }
                    return correctLine;
                });

        Assert.assertTrue(correct);
    }

    private static final class Worker implements Callable<Void> {

        @Override
        public Void call() {
            for (int i = 0; i < ITERATIONS; i++)
            try {
                preLog.incrementAndGet();
                logger.info(text);
                postLog.incrementAndGet();
            } catch (Exception e) {
                errors.incrementAndGet();
            }
            return null;
        }
    }

}
