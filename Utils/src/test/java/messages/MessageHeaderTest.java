package messages;

import messages.command.CommandMessage;
import messages.command.CommandMessageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageHeaderTest {

    @Test
    void fromBytes() {
        CommandMessage testMsg = new CommandMessage(CommandMessageType.MSG_GET_LS);
        testMsg.setParameter("/dir1/dir2");
        byte[] bytes = testMsg.toBytes();

        MessageHeader header = new MessageHeader(bytes);

        System.out.println("Checking message type...");
        Assertions.assertEquals(MessageType.COMMAND, header.msgType);

        System.out.println("Checking id...");
        Assertions.assertEquals(testMsg.msgId, header.msgId);

        System.out.println("Checking length...");
        Assertions.assertEquals(bytes.length, header.msgLength);
    }
}