package org.ibci.extension.communication;
import org.ibci.componentinstaller.model.util.CustomProcessBuilder;
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions;
import org.ibci.componentinstaller.util.RequestData;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;

public class Communicator {

    public String lastReply;

    public boolean sendRequest(String aRequest) {
        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:7878");
            // Send a request to the server
            socket.send(aRequest.getBytes(), 0);
            // Waiting for reply
            byte[] reply = socket.recv(0);
            this.lastReply = new String(reply);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean startWindowsWrapper(boolean asAdmin) {
        // Create a ProcessBuilder instance
        if (asAdmin) {
            CustomProcessBuilder tmpCustomProcessBuilder = new CustomProcessBuilder();
            String[] tmpArgs = new String[1];
            tmpArgs[0] = PathDefinitions.WINDOWS_TASKS_EXE;
            Process process = tmpCustomProcessBuilder.openCommand(tmpArgs, PathDefinitions.CMD_ELEVATOR_EXE);
        } else {
            try {
                ProcessBuilder processBuilder;
                processBuilder = new ProcessBuilder(PathDefinitions.WINDOWS_TASKS_EXE);
                Process process = processBuilder.start();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean sendRequestWithDebug(String aRequest, Boolean asAdmin) {
        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:7879");
            // Send a request to the server
            socket.send(aRequest.getBytes(), 0);
            // Waiting for reply
            byte[] reply = socket.recv(0);
            this.lastReply = new String(reply);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Communicator started & creating ZContext ...");

    }
}
