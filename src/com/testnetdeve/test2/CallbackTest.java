package com.testnetdeve.test2;

public class CallbackTest {

    public static void main(String[] args) {
        Student student = new AStudent();
        Teacher teacher = new Teacher(student);

        teacher.askQuestion();
        //System.out.println("这是最后了");

    }
}
