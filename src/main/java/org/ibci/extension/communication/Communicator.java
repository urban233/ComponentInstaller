package org.ibci.extension.communication;
import org.ibci.componentinstaller.model.util.CustomProcessBuilder;
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;

/**
 * Class for the communicator
 *
 */
public class Communicator {
    /**
     * Contains the last reply as string
     */
    public String lastReply;

    /**
     * Sends a request to the WindowsTasks.exe
     * @param aRequest Path to the json file to send to WindowsTasks.exe
     * @return True if request was successful, otherwise false.
     */
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

    /**
     * Starts the WindowsTasks.exe file
     *
     * @param asAdmin Flag if the executable should be run with admin privileges
     * @return True if executable started successfully, otherwise false
     */
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
    /**
     * Sends a request in a debug environment
     *
     * @param aRequest Path to json file
     * @return True if request was successful, otherwise false
     */
    public boolean sendRequestWithDebug(String aRequest) {
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
}
