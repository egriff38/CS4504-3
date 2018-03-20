import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length < 2 || args.length > 3) {
                throw new IllegalArgumentException();
            }

            String[] host = args[1].split(":");

            int srcPort = Integer.parseInt(args[0]);
            String IP;
            int port;
            int processNo = 0; // For if process number is not provided

            if (args.length == 3) { // If process number is provided
                processNo = Integer.parseInt(args[2]);
            }

            if (host.length != 2) {    // Incorrect Argument Format
                throw new IllegalArgumentException();
            } else {
                IP = host[0];
                port = Integer.parseInt(host[1]);
            }

            Process process = new Process(processNo, srcPort);

            // Deals with getting all three processes connected
            if (processNo == 0) {
                process.firstProcessMain(IP, port);
            } else {
                process.otherProcessesMain(IP, port);
            }

            if (process.getProcessNumber() == 0) {
                process.setCurrentEvent(process.action(new SocketEvent()));
                process.send();
            }

            process.serverConnect(IP, port);

            process.listen();
            if (processNo == 0) {
                process.printClock();
            } else {
                process.send();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Process [SRC_PORT] [DEST_IP]:[DEST_PORT] [?processNumber]");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException f) {
            f.printStackTrace();
        }
    }
}