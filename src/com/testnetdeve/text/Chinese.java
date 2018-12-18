package com.testnetdeve.text;

public class Chinese implements Human {
    @Override
    public void say() {
        System.out.println("I can say Chinese");
    }

    @Override
    public void eat() {
        System.out.println("I like eat noddles");
    }

    @Override
    public boolean hasBrain() {
        return false;
    }

    public static void main(String[] args) {


        Chinese chinese =new Chinese();
        chinese.eat();
        chinese.say();
        System.out.println(chinese.hasBrain());


    }
}
