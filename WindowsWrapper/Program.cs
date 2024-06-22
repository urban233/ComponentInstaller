namespace WindowsWrapper;

class Program
{
    static void Main(string[] args)
    {
        Communicator communicator = new Communicator();
        communicator.CreateConnection();
    }
}