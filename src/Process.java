import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

public class Process {
    private int processNumber;
    private Vector<Integer> vectorTime;
    private int scalarTime;
    private SocketEvent currentEvent;
    private Socket socket;
    private ObjectInputStream instream;
    private ObjectOutputStream ostream;

    public Process(int processNumber, String address, int portNo) throws IOException {
        currentEvent = null;
        this.processNumber = processNumber;
        vectorTime = new Vector<>(3);
        vectorTime.set(0, 0);
        vectorTime.set(1, 0);
        vectorTime.set(2,0);
        scalarTime = 0;
        socket = new Socket(address, portNo);
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

    public Socket getSocket() {
        return socket;
    }

    public void updateScalar(int time) {
        scalarTime++;
    }

    public void updateVector(Vector<Integer> time) {
        vectorTime.set(0, Math.max(vectorTime.get(0) , time.get(0)));
        vectorTime.set(1, Math.max(vectorTime.get(1) , time.get(1)));
        vectorTime.set(2, Math.max(vectorTime.get(2) , time.get(2)));
        vectorTime.set(processNumber,vectorTime.get(processNumber)+1);
    }

    // Socket Connection




    public SocketEvent action(SocketEvent event) {
        return event;
    }

    public static void main(String[] args) {
        if(args.length<2||args.length>3){
            System.out.println("Process [IP]:[PORT] [?processNumber]");
        }
        int processNo = 0;
        String IP;
        int port;
        if(args.length==3) processNo = Integer.parseInt(args[2]);
        try{
            String[] host = args[1].split(":");
            if(host.length!=2)throw new Error("Bad host argument");
            else{
                IP = host[0];
                port = Integer.parseInt(host[1]);
            }
            Process process = new Process(processNo,IP,port);
        }catch (Exception e){
            System.out.println("Process [IP]:[PORT] [?processNumber]");
        }



    }
}
