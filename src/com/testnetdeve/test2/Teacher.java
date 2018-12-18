package com.testnetdeve.test2;

public class Teacher implements Callback {
    private Student student;

    public Teacher(Student student) {
        this.student = student;
    }

    public void askQuestion(){
        System.out.println("老师提问问题，等待学生回答...");
        student.dealWithQuestion(this);
    }

    @Override
    public void tellAnswer(int answer) {
        System.out.println("你的答案是："+answer);
    }


}
