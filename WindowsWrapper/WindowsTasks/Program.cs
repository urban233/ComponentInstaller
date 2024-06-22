using WindowsTasks.Util;

namespace WindowsTasks;

class Program
{
    static void Main(string[] args)
    {
        Logging.SetupLoggerConfig();

        Communicator communicator = new Communicator();
        string tmpJsonFilepath = communicator.CreateConnection();
        RequestData tmpRequestData = communicator.GetRequestData(tmpJsonFilepath);
        communicator.CloseConnection(ExecuteOperation(tmpRequestData));
        // if (args[0] == "--admin" && args[1] == "--keep-checking")
        // {
        //     bool tmpKeepChecking = true;
        //     string tmpResult= "";
        //     while (tmpKeepChecking)
        //     {
        //         tmpResult = ExecuteOperation(tmpRequestData);
        //         if (tmpResult == "Close")
        //         {
        //             tmpKeepChecking = false;
        //         }
        //     }
        //     communicator.CloseConnection(tmpResult);
        // }
        // else
        // {
        //     communicator.CloseConnection(ExecuteOperation(tmpRequestData));
        // }
    }

    static string ExecuteOperation(RequestData theRequestData)
    {
        String tmpResult;
        switch (theRequestData.type)
        {
            #region Unzip archive
            case OperationTypes.UnzipArchive:
                try
                {
                    if (FilesystemIo.UnzipArchive(aZipFilepath: theRequestData.data[0],
                            anExtractPath: theRequestData.data[1]))
                    {
                        tmpResult = "Unzip process was successful.";
                    }
                    else
                    {
                        tmpResult = "Unzip process failed";
                    }
                }
                catch (Exception ex)
                {
                    tmpResult = ex.Message;
                }
                break;
            #endregion

            #region Create shortcuts
            case OperationTypes.CreateShortcuts:
                try
                {
                    string executablePath = theRequestData.data[0];
                    string shortcutName = theRequestData.data[1];
                    string iconPath = theRequestData.data[2];
                    // Create desktop icon
                    ShortcutManager.CreateDesktopShortcut(executablePath, shortcutName, iconPath);
                    // Create start menu entry
                    ShortcutManager.CreateStartMenuShortcut(executablePath, shortcutName, iconPath);
                    tmpResult = "Shortcuts created successfully.";
                }
                catch (Exception ex)
                {
                    tmpResult = ex.Message;
                }
                break;
            #endregion

            #region Remove shortcuts
            case OperationTypes.RemoveShortcuts:
                try
                {
                    string shortcutName = theRequestData.data[0];
                    ShortcutManager.RemoveShortcut(Environment.SpecialFolder.DesktopDirectory, shortcutName);
                    ShortcutManager.RemoveShortcut(Environment.SpecialFolder.StartMenu, shortcutName);
                    tmpResult = "Shortcuts removed successfully.";
                }
                catch (Exception ex)
                {
                    tmpResult = ex.Message;
                }
                break;
            #endregion

            #region Run cmd command
            case OperationTypes.RunCmdCommand:
                try
                {
                    ConsoleRunner.RunCommandInCmd(theRequestData.data[0]);
                    tmpResult = "Command process ended successfully.";
                }
                catch (Exception ex)
                {
                    tmpResult = ex.Message;
                }
                break;
            #endregion

            #region Default
            default:
                tmpResult = "Requested operation not found.";
                break;
            #endregion
        }

        return tmpResult;
    }
}