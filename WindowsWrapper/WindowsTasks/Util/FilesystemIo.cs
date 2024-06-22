using System.IO.Compression;

namespace WindowsTasks.Util;

public class FilesystemIo
{
    /// <summary>
    /// Unzips the windows_package.zip file to the specified directory.
    /// </summary>
    /// <returns>True if successful, false if any error occurs during extraction or file deletion.</returns>
    public static bool UnzipArchive(string aZipFilepath, string anExtractPath)
    {
        // Ensure the zip archive exists
        if (!File.Exists(aZipFilepath))
        {
            return false;
        }
        // Ensure the extract directory exists
        if (!Directory.Exists(anExtractPath))
        {
            Directory.CreateDirectory(anExtractPath);
        }

        // Unzip the archive
        try
        {
            ZipFile.ExtractToDirectory(aZipFilepath, anExtractPath, overwriteFiles: true);
            File.Delete(aZipFilepath);
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex.Message);
            return false;
        }

        return true;
    }
}