package files;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

    @Test
    void getFilesList() {
        FileList filesList = FileUtils.getFilesList("./");
        Assertions.assertNotNull(filesList);
        System.out.println(filesList);
    }
}