using System;
using System.Diagnostics;
using System.Security.Principal;

namespace CmdElevator
{
    /// <summary>
    /// Entry point class containing the Main method
    /// </summary>
    class Program
    {
        /// <summary>
        /// Entry point method
        /// </summary>
        static void Main(string[] args)
        {
            if (IsAdministrator())
            {
                // If the current process is already elevated, run the desired command
                RunCommand(args);
            }
            else
            {
                // If not elevated, restart the process with elevated rights
                RestartElevated(args);
            }
        }

        /// <summary>
        /// Checks if the current user has administrator privileges.
        /// </summary>
        /// <returns>True if the current user is an administrator; otherwise, false.</returns>
        static bool IsAdministrator()
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
        static void RestartElevated(string[] args)
        {
            string arguments = string.Join(" ", args);

            ProcessStartInfo startInfo = new ProcessStartInfo
            {
                FileName = "cmd.exe",  // or "powershell.exe" if you want to start PowerShell
                Arguments = $"/c {arguments}",
                Verb = "runas",
                UseShellExecute = true
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
        static void RunCommand(string[] args)
        {
            if (args.Length == 0)
            {
                Console.WriteLine("Please provide a command to run.");
                return;
            }

            string command = string.Join(" ", args);

            ProcessStartInfo startInfo = new ProcessStartInfo
            {
                FileName = "cmd.exe",  // or "powershell.exe" if you want to start PowerShell
                Arguments = $"/c {command}",
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
}
