package utils;

import org.junit.jupiter.api.Assertions;

import java.sql.Connection;

class DatabaseUtilsTest {

    @org.junit.jupiter.api.Test
    void connectDB() {
        Connection conn = DatabaseUtils.connectDB();
        Assertions.assertNotNull(conn);
        DatabaseUtils.disconnectDB(conn);
    }
}