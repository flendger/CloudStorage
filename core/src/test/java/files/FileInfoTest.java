package files;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FileInfoTest {

    @Test
    void of() {
        FileInfo fi = FileInfo.of("src/main/resources/test.db");
        System.out.println(fi);
        Assertions.assertNotNull(fi);
    }
}