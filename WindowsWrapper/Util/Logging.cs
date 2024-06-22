using NLog;
using NLog.Config;
using NLog.Targets;

namespace WindowsWrapper.Util;

public class Logging
{
    /// <summary>
    /// Program path of the installer.
    /// </summary>
    public static readonly string InstallerProgramToolsPath = @"C:\ProgramData\IBCI\PySSA-Installer\tools";
    /// <summary>
    /// Path of the installer log files.
    /// </summary>
    public static readonly string InstallerLogPath = $@"{InstallerProgramToolsPath}\logs";

    /// <summary>
    /// Sets up the logger configuration.
    /// </summary>
    /// <returns>
    /// Returns true if the logger configuration is successfully set up; otherwise, false.
    /// </returns>
    public static bool SetupLoggerConfig()
    {
        try
        {
            var config = new LoggingConfiguration();
            // Get the current date and time
            string currentDateTime = DateTime.Now.ToString("yyyy:MM:dd_HH:mm:ss");
            // Replace characters that are not allowed in file names
            currentDateTime = currentDateTime.Replace(":", "-");
            // Targets where to log to: File and Console
            if (!Directory.Exists(InstallerLogPath))
            {
                Directory.CreateDirectory(InstallerLogPath);
            }
            var logfile = new FileTarget("logfile")
            {
                FileName = $@"{InstallerLogPath}\{currentDateTime}.log"
            };
            var logconsole = new ConsoleTarget("logconsole");
            // Rules for mapping loggers to targets
            config.AddRule(LogLevel.Info, LogLevel.Fatal, logconsole);
            config.AddRule(LogLevel.Debug, LogLevel.Fatal, logfile);
            // Apply config
            LogManager.Configuration = config;
        }
        catch (Exception ex)
        {
            return false;
        }
        return true;
    }
}