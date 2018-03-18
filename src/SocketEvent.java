import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SocketEvent extends java.net.Socket implements java.io.Serializable {
    private int eventNo;
    private String text;
    private int lamport;
    private Vector<Integer> vectorClock;
    private Map<Integer, Vector<Integer>> history; // If the process #s are random, how will we retrieve these values?

    public SocketEvent(int eventNo,String text, int lamport, Vector<Integer> vectorClock){
        this.eventNo = eventNo;
        this.text = text;
        this.lamport = lamport;
        this.vectorClock = vectorClock;
        this.history = new HashMap<>();
        this.history.put(this.eventNo, this.vectorClock);
    }

    // Increments clocks and changes text
    public SocketEvent update(String text, int lamport, Vector<Integer> vectorClock){
        return new SocketEvent((int)(Math.random()*100)
                + this.eventNo + 1, text, lamport, vectorClock);
    }

    // Serializes
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(this);
    }

    // Deserializes
    private Object readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        return in.readObject(); // This should be a SocketEvent object
    }

    private void readObjectNoData() throws ObjectStreamException {
        System.out.println("No data to process");
    }

    private void addHistory(SocketEvent e) {
        history.put(e.eventNo, e.vectorClock);
    }

    private String changeText(String text) {
        return text;
    }
}