package com.testnetdeve.test2;

public class AStudent implements Student {
    @Override
    public void dealWithQuestion(Callback callback) {
        try{
            Thread.sleep(3000);
        }catch (Exception e){
            e.printStackTrace();
        }
        callback.tellAnswer(3);
    }
}
