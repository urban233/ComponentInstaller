using System.Diagnostics;
using System.Security.Principal;

namespace WindowsCmdElevator;

public class CmdElevator
{
    /// <summary>
    /// Checks if the current user has administrator privileges.
    /// </summary>
    /// <returns>True if the current user is an administrator; otherwise, false.</returns>
    public static bool IsAdministrator()
    {
        using (WindowsIdentity identity = WindowsIdentity.GetCurrent())
        {
            WindowsPrincipal principal = new WindowsPrincipal(identity);
            return principal.IsInRole(WindowsBuiltInRole.Administrator);
        }
    }

    /// <summary>
    /// Restarts the current application with elevated (administrator) privileges.
    /// </summary>
    /// <param name="args">The command-line arguments to pass to the application.</param>
    public static void RestartElevated(string[] args)
    {
        string arguments = string.Join(" ", args);

        ProcessStartInfo startInfo = new ProcessStartInfo
        {
            FileName = "cmd.exe", // or "powershell.exe" if you want to start PowerShell
            Arguments = $"/k {arguments}",
            Verb = "runas",
            UseShellExecute = true
            //CreateNoWindow = false
        };

        try
        {
            Process process = Process.Start(startInfo);
            process.WaitForExit();
        }
        catch (Exception ex)
        {
            Console.WriteLine("The process could not be started: " + ex.Message);
        }
    }

    /// <summary>
    /// Runs a command in the command prompt (cmd) and displays the output.
    /// </summary>
    /// <param name="args">The command-line arguments representing the command to run.</param>
    public static void RunCommand(string[] args)
    {
        if (args.Length == 0)
        {
            Console.WriteLine("Please provide a command to run.");
            return;
        }

        string command = string.Join(" ", args);

        ProcessStartInfo startInfo = new ProcessStartInfo
        {
            FileName = "cmd.exe", // or "powershell.exe" if you want to start PowerShell
            Arguments = $"/k {command}",
            RedirectStandardOutput = true,
            RedirectStandardError = true,
            UseShellExecute = false,
            CreateNoWindow = true
        };

        try
        {
            using (Process process = new Process())
            {
                process.StartInfo = startInfo;
                process.Start();

                string output = process.StandardOutput.ReadToEnd();
                string error = process.StandardError.ReadToEnd();

                process.WaitForExit();

                Console.WriteLine(output);
                if (!string.IsNullOrEmpty(error))
                {
                    Console.WriteLine("Error: " + error);
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine("The command could not be executed: " + ex.Message);
        }
    }
}
