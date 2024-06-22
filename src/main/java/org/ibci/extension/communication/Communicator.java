package org.ibci.extension.communication;
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;

public class Communicator {

    public String lastReply;

    public boolean sendRequest(String aRequest) {
        if (!startWindowsWrapper()) {
            return false;
        }
        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            System.out.println("Creating REQ socket ...");
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

    private boolean startWindowsWrapper() {
        // Create a ProcessBuilder instance
        ProcessBuilder processBuilder = new ProcessBuilder(PathDefinitions.WINDOWS_WRAPPER_EXE);
        try {
            // Start the process
            Process process = processBuilder.start();
            // Optionally, you can wait for the process to complete later
            // int exitCode = process.waitFor();
            // System.out.println("Process exited with code: " + exitCode);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean sendRequestWithDebug(String aRequest) {
        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            System.out.println("Creating REQ socket ...");
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
