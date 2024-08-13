/*    */
package cc.xylitol.utils;

import java.util.Random;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class StopWatch
        /*    */ {
    /*    */   private long millis;

    /*    */
    /*    */
    public void setMillis(long millis) {
        /* 12 */
        this.millis = millis;
        /*    */
    }

    /*    */
    /*    */
    public static long randomDelay(final int minDelay, final int maxDelay) {
        return nextInt(minDelay, maxDelay);
    }

    public static int nextInt(final int startInclusive, final int endExclusive) {
        return (endExclusive - startInclusive <= 0) ? startInclusive : startInclusive + new Random().nextInt(endExclusive - startInclusive);
    }

    /*    */
    public StopWatch() {
        /* 17 */
        reset();
        /*    */
    }

    /*    */
    /*    */
    public boolean finished(long delay) {
        /* 21 */
        return (System.currentTimeMillis() - delay >= this.millis);
        /*    */
    }

    /*    */
    /*    */
    public void reset() {
        /* 25 */
        this.millis = System.currentTimeMillis();
        /*    */
    }

    /*    */
    /*    */
    public long getMillis() {
        /* 29 */
        return this.millis;
        /*    */
    }

    /*    */
    /*    */
    public long getElapsedTime() {
        /* 33 */
        return System.currentTimeMillis() - this.millis;
        /*    */
    }
    /*    */
}


/* Location:              F:\QQ\1446679699\FileRecv\Rise.jar\\util\time\StopWatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */