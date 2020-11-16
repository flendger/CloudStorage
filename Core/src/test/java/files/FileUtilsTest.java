package files;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void getFilesList() {
        FileList filesList = FileUtils.getFilesList("./");
        Assertions.assertNotNull(filesList);
        System.out.println(filesList);
    }
}