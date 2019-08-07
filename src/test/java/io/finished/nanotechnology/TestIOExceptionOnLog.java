package io.finished.nanotechnology;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author olegzinovev
 * @since 2019-08-07
 **/
public class TestIOExceptionOnLog {
    private static final Logger logger = LoggerFactory.getLogger(TestIOExceptionOnLog.class);

    @Test
    public void test() {
        boolean work = false;
        try {
            logger.info("try to write this");
            work = true;
        } catch (Exception e) {
            // do nothing
        }

        Assert.assertTrue(work);
    }
}
