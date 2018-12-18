package com.testnetdeve.test1;

public class C extends B {
    @Override
    public void c() {
        System.out.println(this.getClass().getName()+"-->"+"b()");
    }


    public static void main(String[] args) {
        C c = new C();
        c.a();
        c.b();
        c.c();
    }
}
