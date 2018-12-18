package com.testnetdeve.threadtest;


import java.util.concurrent.*;

public class ThreadDemo {

        public static void main(String[] args) {
            MyRunnable command = new MyRunnable();
            long start = System.currentTimeMillis();
            int executeCount = 10;
            command.setEndListener(new MyRunnable.EndListener() {
                private int count = 0;

                @Override
                public void end() {
                    count++;
                    if (count == executeCount) {
                        long end = System.currentTimeMillis();
                        System.out.println("全部请求执行完毕，耗时:" + (end - start) + "毫秒");
                    }
                }
            });
            System.out.println("开始请求");

           // singleThread(command,executeCount);
            multiThread(command, executeCount, 200);
        }

        /**
         * 单线程运行
         *
         * @param runnable     run
         * @param executeCount 运行次数
         */
        public static void singleThread(MyRunnable runnable, int executeCount) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();//单一后台线程
            for (int i = 0; i < executeCount; i++) {
                executorService.execute(runnable);
            }
        }

        /**
         * 多线程运行
         *
         * @param runnable        run
         * @param executeCount    运行次数
         * @param coreThreadCount 核心线程数量
         */
        public static void multiThread(MyRunnable runnable, int executeCount, int coreThreadCount) {
            BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
            ThreadPoolExecutor threadPool = new ThreadPoolExecutor(coreThreadCount, 200, 2000, TimeUnit.SECONDS, queue);
            for (int i = 0; i < executeCount; i++) {
                threadPool.execute(runnable);
            }
        }

        public static class MyRunnable implements Runnable {
            private EndListener listener;
            private int count;

            @Override
            public void run() {
                try {
                    int currentCount = 0;
                    synchronized (MyRunnable.class) {
                        currentCount = count++;
                        System.out.println("线程：" + Thread.currentThread().getName() + "进行第" + currentCount + "次请求");
                    }
                    Thread.sleep(100);
                    System.out.println("线程：" + Thread.currentThread().getName() + "第" + currentCount + "次请求完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                listener.end();
            }

            public void setEndListener(EndListener listener) {
                this.listener = listener;
            }

            interface EndListener {
                void end();
            }
        }





}

