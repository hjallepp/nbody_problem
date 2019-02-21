public class Bag extends Thread {
    private int task;

    public Bag(){
        this.task = 0;
    }

    public synchronized int getTask(){
        int t = task;
        task++;
        return t;
    }
    public synchronized void newBag(){
        task = 0;
    }
}
