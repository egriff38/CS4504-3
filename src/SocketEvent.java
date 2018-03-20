import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SocketEvent extends java.net.Socket implements java.io.Serializable {
    private int eventNo;
    private String text;
    private int lamport;
    private Vector<Integer> vectorClock;
    private Map<Integer, Vector<Integer>> history; // If the process #s are random, how will we retrieve these values?
    public SocketEvent(){
        this(1,"I love you",0,new Vector<>(Arrays.asList(0,0,0)));
    }
    public SocketEvent(int eventNo,String text, int lamport, Vector<Integer> vectorClock){
        this.eventNo = eventNo;
        this.text = text;
        this.lamport = lamport;
        this.vectorClock = vectorClock;
        this.history = new HashMap<>();
    }

    public int getEventNo() {
        return eventNo;
    }

    public String getText() {
        return text;
    }

    public int getLamport() {
        return lamport;
    }

    public Vector<Integer> getVectorClock() {
        return vectorClock;
    }

    public Map<Integer, Vector<Integer>> getHistory() {
        return history;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SocketEvent update(String text, int lamport, Vector<Integer> vectorClock){
        SocketEvent sentSocket = new SocketEvent((int)(Math.random()*100)
                + this.eventNo + 1, text, lamport, vectorClock);
        sentSocket.setHistory(this.history);
        return sentSocket;
    }
    public void setHistory(Map<Integer, Vector<Integer>> history){
        this.history = history;
    }
    public void addHistory(SocketEvent e) {
        history.put(e.eventNo, e.vectorClock);
    }

    private String changeText(String text) {
        return text;
    }
}