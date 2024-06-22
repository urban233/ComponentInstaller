using System.Text.Json;
using NetMQ;
using NetMQ.Sockets;

namespace WindowsTasks;

public class Communicator
{
    private ResponseSocket _responseSocket;

    public String CreateConnection()
    {
        _responseSocket = new ResponseSocket("@tcp://localhost:7879"); // For debugging purposes change to 7879, else 7878
        Console.WriteLine("Waiting for request ...");
        string tmpJsonFilepath = _responseSocket.ReceiveFrameString();
        Console.WriteLine("Received request: " + tmpJsonFilepath);
        return tmpJsonFilepath;
    }

    public void CloseConnection(String aResult)
    {
        // Send the result back to the client
        _responseSocket.SendFrame(aResult);
        Console.WriteLine("Sent reply: " + aResult);
    }

    public RequestData GetRequestData(string aJsonFilepath)
    {
        string jsonString = File.ReadAllText(aJsonFilepath);
        Console.WriteLine($"{jsonString}");
        RequestData tmpRequestData = JsonSerializer.Deserialize<RequestData>(jsonString);
        return tmpRequestData;
    }
}