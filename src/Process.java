import java.util.Vector;

public class Process {
    private int processNumber;
    private Vector<Integer> vectorTime;
    private int scalarTime;
    private SocketEvent currentEvent;

    public Process(int processNumber) {
        currentEvent = null;
        this.processNumber = processNumber;
        vectorTime = new Vector<>(3);
        vectorTime.set(0, 0);
        vectorTime.set(1, 0);
        vectorTime.set(2,0);
        scalarTime = 0;
    }

    public SocketEvent getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(SocketEvent currentEvent) {
        this.currentEvent = currentEvent;
    }

    public int getProcessNumber() {
        return processNumber;
    }

    public Vector<Integer> getVectorTime() {
        return vectorTime;
    }

    public int getScalarTime() {
        return scalarTime;
    }

    public void updateScalar(int time) {
        scalarTime++;
    }

    public void updateVector(Vector<Integer> time) {
        vectorTime.set(0, vectorTime.get(0) + time.get(0));
        vectorTime.set(1, vectorTime.get(1) + time.get(1));
        vectorTime.set(2, vectorTime.get(2) + time.get(2));
    }


    public SocketEvent action(SocketEvent event) {
        return event;
    }
}
