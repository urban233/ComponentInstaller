using System.Text.Json;
using NetMQ;
using NetMQ.Sockets;

namespace WindowsTasks;

public class Communicator
{
    private ResponseSocket _responseSocket;

    public void CreateConnection()
    {
        _responseSocket = new ResponseSocket("@tcp://localhost:7878"); // For debugging purposes change to 7879, else 7878
        Console.WriteLine("Waiting for request ...");
    }

    public string CheckForRequest()
    {
        string tmpJsonFilepath = _responseSocket.ReceiveFrameString();
        Console.WriteLine("Received request: " + tmpJsonFilepath);
        return tmpJsonFilepath;
    }

    public void SendReply(string aResult)
    {
        _responseSocket.SendFrame(aResult);
        Console.WriteLine("Sent reply: " + aResult);
    }

    public void CloseConnection()
    {
        _responseSocket.Close();
    }

    public RequestData GetRequestData(string aJsonFilepath)
    {
        string jsonString = File.ReadAllText(aJsonFilepath);
        Console.WriteLine($"{jsonString}");
        RequestData tmpRequestData = JsonSerializer.Deserialize<RequestData>(jsonString);
        return tmpRequestData;
    }
}