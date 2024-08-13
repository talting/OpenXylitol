package cc.xylitol.ui.gui.splash.utils;

import cc.xylitol.Client;
import cc.xylitol.ui.gui.splash.SplashScreen;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjglx.Sys;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * loads gl content async-ly
 * @author ImXianyu
 * @since 2/4/2023 7:08 PM
 */
public class AsyncGLContentLoader {

//    static final Object contentLock = new Object();

    /**
     * the count of the loader threads
     */
    public static final int threadCount = 4;

    /**
     * threads list
     */
    public static final List<LoaderThread> threads = Collections.synchronizedList(new ArrayList<>());

    /**
     * a list to store tasks when the loader threads aren't loaded.
     */
    private static final List<Runnable> preLoadTasks = Collections.synchronizedList(new ArrayList<>());


    /**
     * loader thread.
     */
    public static class LoaderThread extends Thread {

        public final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

        public final long handle;
        public LoaderThread(long handle) {
            this.handle = handle;
        }

        @Override
        @SneakyThrows
        public void run() {
            GLFW.glfwMakeContextCurrent(this.handle);
            GL.createCapabilities();

            while (Minecraft.getMinecraft().running) {
                if (!tasks.isEmpty()) {
                    synchronized (SplashScreen.renderLock) {
                        // poll the task from the queue
                        Runnable runnable = tasks.poll();

                        try {
                            // this shouldn't happen but in case it happens we'll just assert it is not null
                            assert runnable != null;

                            // execute the task
                            runnable.run();

                            // sync gl commands all across the threads
                            GL11.glFlush();

                        } catch (Exception ignored) {
                            // the error is raised, but I don't give a fuck :) so just leave it alone
                            ignored.printStackTrace();
                        }
                    }
                } else {
                    synchronized (tasks) {
                        // let the thread waits if there aren't any tasks to load
                        tasks.wait();
                    }
                }
            }
        }
    }

    /**
     * initializes the thread.
     */
    public static void initLoader() {

        for (int i = 0; i < threadCount; i++) {
            long subWindow = AsyncContextUtils.createSubWindow();

            LoaderThread thread = new LoaderThread(subWindow);
            thread.setName("GL Content Loader " + i);
            thread.setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
            // set the thread to high priority to load tasks faster
            thread.setPriority(10);
            thread.start();
            threads.add(thread);
        }

        // if we have tasks preloaded
        if (!preLoadTasks.isEmpty()) {
            for (Runnable r : preLoadTasks) {
                loadGLContentAsync(r);
            }
        }

        AsyncGLContentLoader.startDaemonThread();

        System.out.print("Async Content Loader Started.");
    }

    private static void startDaemonThread() {
        new Thread(
                () -> {
                    while (true) {

                        if (threads.isEmpty())
                            continue;

                        for (LoaderThread thread : threads) {
                            if (!thread.tasks.isEmpty() && thread.getState() == Thread.State.WAITING)
                                synchronized (thread.tasks) {
                                    // change the thread's state (WAITING -> RUNNING) to continue loading tasks
                                    thread.tasks.notifyAll();
                                }
                        }

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                "AsyncGLContentLoader Daemon Thread"
        ).start();
    }

    @SneakyThrows
    public static void loadGLContentAsync(Runnable runnable) {

        if (SplashScreen.crashDetected && Client.instance.clientLoadFinished) {
            Minecraft.getMinecraft().addScheduledTask(runnable);
            return;
        }

        // the loaders aren't loaded yet
        if (threads.isEmpty()) {
            preLoadTasks.add(runnable);
            return;
        }

        LoaderThread least = threads.get(0);
        for (LoaderThread thread : threads) {
            if (thread.tasks.size() < least.tasks.size())
                least = thread;

            if (!thread.tasks.isEmpty() && thread.getState() == Thread.State.WAITING) {
                synchronized (thread.tasks) {
                    // change the thread's state (WAITING -> RUNNING) to continue loading tasks
                    thread.tasks.notifyAll();
                }
            }
        }

        if (runnable == null) {
            return;
        }

        if (least.getState() == Thread.State.WAITING)
            synchronized (least.tasks) {
                least.tasks.notifyAll();
            }

//        System.out.println("Task given to thread => " + least.getName());

        least.tasks.add(runnable);
    }

    public static boolean isAllTasksFinished() {
        for (LoaderThread thread : threads) {
            if (!thread.tasks.isEmpty())
                return false;
        }

        return true;
    }

    private static void getMaxTextureSize() {
        for (int i = 0x4000; i > 0; i >>= 1) {
            GL11.glTexImage2D(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_RGBA, i, i, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            if (GL11.glGetTexLevelParameteri(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH) != 0) {
                return;
            }
        }
    }
}
