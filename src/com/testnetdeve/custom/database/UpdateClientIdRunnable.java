package com.testnetdeve.custom.database;

import com.testnetdeve.custom.server.Observer;
import com.testnetdeve.custom.server.Subject;

import java.util.HashSet;

public class UpdateClientIdRunnable implements Runnable{

    private EndListener listener;
    private int count;

    @Override
    public void run() {
        try {
            int currentCount = 0;
            synchronized (UpdateClientIdRunnable.class) {
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


}
