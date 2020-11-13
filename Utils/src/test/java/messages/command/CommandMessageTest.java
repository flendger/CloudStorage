package messages.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandMessageTest {
    private CommandMessage testMsg;
    private byte[] bytes;


    @BeforeEach
    void setUp() {
        testMsg = new CommandMessage(CommandMessageType.MSG_GET_LS);
        testMsg.setParameter("/dir1/dir2");
        bytes = testMsg.toBytes();
    }

    @Test
    void toBytes() {
        System.out.println("Testing command msg...");
        System.out.println("Comparing result length...");
        Assertions.assertEquals((CommandMessage.HEADER_LENGTH + testMsg.getParameter().getBytes().length), bytes.length);
    }

    @Test
    void fromBytes() {
        System.out.println("Testing command msg...");
        CommandMessage newMsg = new CommandMessage(bytes);

        System.out.println("Comparing msg id...");
        Assertions.assertEquals(testMsg.getMsgId(), newMsg.getMsgId());

        System.out.println("Comparing msg type...");
        Assertions.assertEquals(testMsg.getCommand(), newMsg.getCommand());

        System.out.println("Comparing msg parameter...");
        Assertions.assertEquals(testMsg.getParameter(), newMsg.getParameter());
    }
}