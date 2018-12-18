package com.testnetdeve.test1;

public abstract class B implements A{
    @Override
    public void a() {
        //共有方法实现
        System.out.println(this.getClass().getName()+"-->"+"a()");
    }

    @Override
    public void b() {
        System.out.println(this.getClass().getName()+"-->"+"b()");
    }
}
