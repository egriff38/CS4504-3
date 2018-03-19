import java.io.*;
import java.net.*;
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
        socket = new Socket("localhost", portNo);
        instream = new ObjectInputStream(socket.getInputStream());
        ostream = new ObjectOutputStream(socket.getOutputStream());
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
    public boolean destConnect(String ip, int portNo) throws IOException {
        InetSocketAddress destination = new InetSocketAddress(ip, portNo);

        socket.connect(destination, 3);
        return socket.isConnected();
    }

    // Serialization
    public void serializedSend(Object o) throws IOException {
        ostream.writeObject(o);
    }

    //Deserialization
    public Object deserializeReceive() throws IOException, ClassNotFoundException {
        return instream.readObject();
    }

    public SocketEvent action(SocketEvent event) {
        String str = event.getText();

        if (event.getEventNo() % 2 == 0) {
            str = str.toUpperCase();
        } else if (event.getEventNo() % 3 == 0) {
            String str2 = "";
            for (int i = str.length(); i > 0; i--) { //Reverses the text
                str2.concat(Character.toString(str.charAt(i - 1)));
            }
            str = str2;
        } else {
            str = str.concat("Toast");
        }
        event.setText(str);
        return event;
    }

    public static void main(String[] args) {
        if(args.length<3||args.length>4){
            System.out.println("Process [SRC_PORT] [DEST_IP]:[DEST_PORT] [?processNumber]");
        }
        int processNo = 0;
        String IP;
        int port;
        int srcPort = Integer.parseInt(args[1]);
        if(args.length==4) processNo = Integer.parseInt(args[3]);
        try{
            String[] host = args[2].split(":");
            if(host.length!=2)throw new Error("Bad host argument");
            else{
                IP = host[0];
                port = Integer.parseInt(host[1]);
            }
            Process process = new Process(processNo,IP,port);
        }catch (Exception e){
            System.out.println("Process [SRC_PORT] [DEST_IP]:[DEST_PORT] [?processNumber]");
        }
    }
}
