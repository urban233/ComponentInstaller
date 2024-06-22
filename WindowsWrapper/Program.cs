using WindowsWrapper.Util;

namespace WindowsWrapper;

class Program
{
    static void Main(string[] args)
    {
        Logging.SetupLoggerConfig();

        Communicator communicator = new Communicator();
        String tmpJsonFilepath = communicator.CreateConnection();
        RequestData tmpRequestData = communicator.GetRequestData(tmpJsonFilepath);

        String tmpResult;
        switch (tmpRequestData.type)
        {
            case OperationTypes.UnzipArchive:
                try
                {
                    if (FilesystemIo.UnzipArchive(aZipFilepath: tmpRequestData.data[0],
                            anExtractPath: tmpRequestData.data[1]))
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

            #region Create shortcuts
            case OperationTypes.CreateShortcuts:
                try
                {
                    string executablePath = tmpRequestData.data[0];
                    string shortcutName = tmpRequestData.data[1];
                    string iconPath = tmpRequestData.data[2];
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
                    string shortcutName = tmpRequestData.data[0];
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

            case OperationTypes.RunCmdCommand:
                try
                {
                    ConsoleRunner.RunCommandInCmd(tmpRequestData.data[0]);
                    tmpResult = "Command process ended successfully.";
                }
                catch (Exception ex)
                {
                    tmpResult = ex.Message;
                }
                break;

            #region Default
            default:
                tmpResult = "Requested operation not found.";
                break;
            #endregion
        }
        communicator.CloseConnection(tmpResult);
    }
}