import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class Process {
    private int processNumber;
    private Vector<Integer> vectorTime;
    private int scalarTime;
    private SocketEvent currentEvent;
    private Socket nextSocket;
    private Socket previousSocket;
    private ServerSocket welcomeSocket;
    private ObjectInputStream instream;
    private ObjectOutputStream ostream;

    public Process(int processNumber) throws IOException {
        currentEvent = null;
        this.processNumber = processNumber;
        vectorTime = new Vector<>(Arrays.asList(0,0,0));
        scalarTime = 0;
        nextSocket = new Socket();
    }

    public SocketEvent getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(SocketEvent currentEvent) {
        this.currentEvent = currentEvent;
        updateVector(currentEvent.getVectorClock());
        updateScalar(currentEvent.getLamport());
        this.currentEvent.addHistory(currentEvent);

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

    public Socket getNextSocket() {
        return nextSocket;
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

    // Serialization
    public void serializedSend(Object o) throws IOException {
        ostream.writeObject(o);
    }

    public void listen() {
        try {
            SocketEvent recievedEvent = (SocketEvent)deserializeReceive();
            setCurrentEvent(recievedEvent);
            System.out.println("received event: "+currentEvent.getEventNo());
            SocketEvent sentEvent = recievedEvent.update(action(recievedEvent).getText(),this.scalarTime,this.vectorTime);
            setCurrentEvent(sentEvent);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public void send() throws IOException {
        this.serializedSend(currentEvent);
        System.out.println("Sent event: "+currentEvent.getEventNo());
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
        System.out.println("Pairs\t\t\t\t\tRelations");
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

    public void client(String ip, int portNo) throws IOException {
        Scanner scanner = new Scanner(System.in);
        char n = 'y';
        if(processNumber==0) {
            System.out.println("Connect? Enter y");
            n = scanner.next().charAt(0);
        }
        if(n == 'y') {
            nextSocket.connect(new InetSocketAddress(InetAddress.getByName(ip), portNo));
            System.out.println("Connected to neighbor process");
            ostream = new ObjectOutputStream(nextSocket.getOutputStream());

        } else {
            System.out.println("Cancelled");
            System.exit(5);
        }
    }

    public void server(String ip, int srcPort, int destPort) throws IOException {
        welcomeSocket = new ServerSocket(srcPort);
        System.out.println("Bound successfully");
        previousSocket = welcomeSocket.accept();
        instream = new ObjectInputStream(previousSocket.getInputStream());
    }
}
