package test;

public class ThreadTest implements Runnable {
    int num;
    String name;

    @Override
    public void run() {
        setName("Nominho");
        setNum(10);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
