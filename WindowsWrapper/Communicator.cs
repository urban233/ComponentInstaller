using NetMQ;
using NetMQ.Sockets;

namespace WindowsWrapper;

public class Communicator
{
    public void CreateConnection()
    {
        // Create a response socket
        using (var responseSocket = new ResponseSocket("@tcp://localhost:7878"))
        {
            Console.WriteLine("Waiting for request ...");
            // Receive a request from the client
            string message = responseSocket.ReceiveFrameString();
            Console.WriteLine("Received request: " + message);
            // Perform the operation (placeholder for actual logic)
            string result = "Operation Completed";
            // Send the result back to the client
            responseSocket.SendFrame(result);
            Console.WriteLine("Sent reply: " + result);
        }
    }
}