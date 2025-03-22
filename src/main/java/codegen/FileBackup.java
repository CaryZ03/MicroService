package codegen;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileBackup {
    public static void backup(Path file) throws IOException {
        Path backupPath = Paths.get(file + ".bak");
        Files.copy(file, backupPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void restore(Path file) throws IOException {
        Path backupPath = Paths.get(file + ".bak");
        if (Files.exists(backupPath)) {
            Files.copy(backupPath, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}