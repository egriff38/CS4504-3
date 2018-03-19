import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

public class Process {
    private int processNumber;
    private Vector<Integer> vectorTime;
    private int scalarTime;
    private SocketEvent currentEvent;
    final private Socket socket;
    final private ObjectInputStream instream;
    final private ObjectOutputStream ostream;

    public Process(int processNumber, int portNo) throws IOException {
        currentEvent = null;
        this.processNumber = processNumber;
        vectorTime = new Vector<>(Arrays.asList(0,0,0));
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
        updateVector(currentEvent.getVectorClock());
        updateScalar(currentEvent.getLamport());

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
        scalarTime = Math.max(scalarTime,time)+1;
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

    private void listen() {
        try {
            setCurrentEvent((SocketEvent)deserializeReceive());
            action(this.currentEvent);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("toast");
        }

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

    public void printClock() {
        System.out.println("Pairs\tRelations");
        for (Map.Entry<Integer, Vector<Integer>> entry1 : currentEvent.getHistory().entrySet()) {
            for(Map.Entry<Integer, Vector<Integer>> entry2 : currentEvent.getHistory().entrySet()) {
                if((!entry1.getKey().equals(entry2.getKey()))) {
                    char x = timeRelationVector(entry1.getValue(), entry2.getValue());
                    System.out.println(entry1.getValue() + ", " + entry2.getValue() + "\t" + x + "\n");
                }
            }
        }
    }

    public char timeRelationLamport(int a, int b) {
        if(a > b) {
            return 'n';
        } else if (a < b) {
            return 'h';
        } else {
            return 'c';
        }
    }

    public char timeRelationVector(Vector<Integer> aArr, Vector<Integer> bArr) {
        if((aArr.get(0) >= bArr.get(0)) && (aArr.get(1) >= bArr.get(1)) && (aArr.get(2) >= bArr.get(2))){
            return 'n';
        } else if((aArr.get(0) <= bArr.get(0)) && (aArr.get(1) <= bArr.get(1)) && (aArr.get(2) <= bArr.get(2))){
            return 'h';
        } else {
            return 'c';
        }
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
        try {
            String[] host = args[2].split(":");
            if(host.length!=2)throw new Error("Bad host argument");
            else {
                IP = host[0];
                port = Integer.parseInt(host[1]);
            }
            Process process = new Process(processNo,port);
            boolean isConnected = false;
            do{
                isConnected = process.destConnect(IP,port);
            } while (!isConnected);
            if(process.processNumber==0){
                process.action(new SocketEvent());
            }
            process.listen();

        } catch (Exception e){
            System.out.println("Process [SRC_PORT] [DEST_IP]:[DEST_PORT] [?processNumber]");
            e.printStackTrace();
        }
    }
}
