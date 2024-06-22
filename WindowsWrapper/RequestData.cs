using WindowsWrapper.Util;

namespace WindowsWrapper;

public class RequestData
{
    public OperationTypes type { get; set; }
    public string[] data { get; set; }
}