public class Timer {

    public long interval;
    private long begin;
    private long desiredTime;

    private boolean isRunning;

    public Timer(long interval){
        this.interval = interval;
    }

    public void start(){
        begin = System.currentTimeMillis();
        calculateDesiredTime();
        isRunning = true;
    }

    public boolean isFinished(){

        if(isRunning){
            return System.currentTimeMillis() >= desiredTime;
        }

        return false;
    }

    private void calculateDesiredTime(){
        desiredTime = begin + interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
        calculateDesiredTime();
    }

    public void restart(){
        start();
    }

    public void stop(){
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
