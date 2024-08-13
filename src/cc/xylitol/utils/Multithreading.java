package cc.xylitol.utils;

import lombok.NonNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Multithreading {
    private static final String THREAD_NAME = "Multithreading Thread";

    private static final ScheduledExecutorService SCHEDULED_RUNNABLE_POOL = Executors.newScheduledThreadPool(3, new ThreadFactory() {
        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable, String.format("%s - %d", THREAD_NAME, this.counter.incrementAndGet()));
        }
    });

    public static ExecutorService ASYNC_RUNNABLE_POOL = Executors.newCachedThreadPool(new ThreadFactory() {
        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable, String.format("%s - %d", THREAD_NAME, this.counter.incrementAndGet()));
        }
    });

    public static void schedule(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        SCHEDULED_RUNNABLE_POOL.scheduleAtFixedRate(runnable, initialDelay, delay, unit);
    }

    public static ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
        return Multithreading.SCHEDULED_RUNNABLE_POOL.schedule(runnable, delay, unit);
    }

    public static void run(Runnable runnable) {
        ASYNC_RUNNABLE_POOL.execute(runnable);
    }

    public static int getAsyncRunnablePoolSize() {
        return ((ThreadPoolExecutor) Multithreading.ASYNC_RUNNABLE_POOL).getActiveCount();
    }
}
