package test;

import myThreads.ReceiveSocket;

public class TestClass {
    public static void main(String[] args) {
        ThreadTest tt = new ThreadTest();
        Thread mThread = new Thread(tt);
        mThread.start();

        int i = 0;
        while (i < 3) {
            System.out.println(tt.getName());
            i++;
        }
    }
}

