package messages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

class MessageTest {
    private Message testMsg;
    private byte[] bytes;

    private Message notCommandMsg;
    private byte[] notComBytes;

    @BeforeEach
    void setUp() {
        testMsg = new Message(MessageType.MSG_GET_LS);
        testMsg.setParameter("/dir1/dir2");
        bytes = testMsg.toBytes();

        notCommandMsg = new Message(MessageType.MSG_TAKE_FILE);
        notComBytes = notCommandMsg.toBytes();
    }

    @org.junit.jupiter.api.Test
    void toBytes() {
        System.out.println("Testing command msg...");
        System.out.println("Comparing result length...");
        Assertions.assertEquals((15 + testMsg.getParameter().getBytes().length), bytes.length);

        System.out.println("Testing NOT command msg...");
        System.out.println("Comparing result length...");
        Assertions.assertEquals(15, notComBytes.length);
    }

    @org.junit.jupiter.api.Test
    void fromBytes() {
        System.out.println("Testing command msg...");
        Message newMsg = new Message();
        newMsg.fromBytes(bytes);
        System.out.println("Comparing msg id...");
        Assertions.assertEquals(testMsg.getMsgId(), newMsg.getMsgId());

        System.out.println("Comparing msg type...");
        Assertions.assertEquals(testMsg.getMsgType(), newMsg.getMsgType());

        System.out.println("Comparing msg isCommand...");
        Assertions.assertEquals(testMsg.isNotFile(), newMsg.isNotFile());

        System.out.println("Comparing msg parameter...");
        Assertions.assertEquals(testMsg.getParameter(), newMsg.getParameter());

        System.out.println("Testing NOT command msg...");
        Message newNotComMsg = new Message();
        newNotComMsg.fromBytes(notComBytes);
        System.out.println("Comparing msg id...");
        Assertions.assertEquals(notCommandMsg.getMsgId(), newNotComMsg.getMsgId());

        System.out.println("Comparing msg type...");
        Assertions.assertEquals(notCommandMsg.getMsgType(), newNotComMsg.getMsgType());

        System.out.println("Comparing msg isCommand...");
        Assertions.assertEquals(notCommandMsg.isNotFile(), newNotComMsg.isNotFile());

        System.out.println("Comparing msg parameter...");
        Assertions.assertEquals(notCommandMsg.getParameter(), newNotComMsg.getParameter());
    }

    @org.junit.jupiter.api.Test
    void testToString() {
        Assertions.assertEquals("error (1)", MessageType.MSG_ERR.toString());
    }
}