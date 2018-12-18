package com.testnetdeve.threadtest;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallableDemo {

    static class TaskWithResult implements Callable<String>{

        private int id;

        public TaskWithResult(int id) {
            this.id = id;
        }

        @Override
        public String call() throws Exception {
            return String.valueOf(id);
        }

    }

    public static void main(String[] args) {
        ExecutorService exc = Executors.newCachedThreadPool();
        ArrayList<Future<String>> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            results.add(exc.submit(new TaskWithResult(i)));
        }
        for (Future<String> fs:results){
            try{
                System.out.println(fs.get());
            } catch (Exception e){
                e.printStackTrace();
            }finally {
                exc.shutdown();
            }
        }
    }
}
