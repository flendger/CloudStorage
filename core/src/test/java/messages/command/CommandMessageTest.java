package messages.command;

import files.FileList;
import files.FileUtils;
import messages.MessageUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandMessageTest {

    private CommandMessage testMsgWithData;
    private byte[] bytes;
    private CommandMessage testMsgWithoutData;
    private byte[] bytesWithoutData;
    private CommandMessage putLS;
    private byte[] lsBytes;
    private FileList fl;


    @BeforeEach
    void setUp() {
        testMsgWithData = new CommandMessage(CommandMessageType.MSG_GET_LS);
        testMsgWithData.setParameter("/dir1/dir2");
        testMsgWithData.setData(new byte[] {0, 1, 2});
        bytes = testMsgWithData.toBytes();

        testMsgWithoutData = new CommandMessage(CommandMessageType.MSG_GET_LS);
        testMsgWithoutData.setParameter("/dir1/dir2");
        bytesWithoutData = testMsgWithoutData.toBytes();

        putLS = new CommandMessage(CommandMessageType.MSG_PUT_LS);
        fl = FileUtils.getFilesList("./");
        putLS.setData(MessageUtils.ObjectToBytes(fl));
        lsBytes = putLS.toBytes();
    }

    @Test
    void toBytes() {
        System.out.println("Testing command msg...");
        System.out.println(testMsgWithData);
        System.out.println("Comparing result length...");
        Assertions.assertEquals((CommandMessage.HEADER_LENGTH + testMsgWithData.getParameter().getBytes().length + testMsgWithData.getData().length), bytes.length);

        System.out.println(testMsgWithoutData);
        System.out.println("Comparing result length...");
        Assertions.assertEquals((CommandMessage.HEADER_LENGTH + testMsgWithoutData.getParameter().getBytes().length + testMsgWithoutData.getData().length), bytesWithoutData.length);

        System.out.println(putLS);
        System.out.println("Comparing result length...");
        Assertions.assertEquals((CommandMessage.HEADER_LENGTH + putLS.getParameter().getBytes().length + putLS.getData().length), lsBytes.length);
    }

    @Test
    void fromBytes() {
        System.out.println("Testing command msg...");
        CommandMessage newMsg = new CommandMessage(bytes);
        System.out.println(testMsgWithData);
        System.out.println(newMsg);

        System.out.println("Comparing msg id...");
        Assertions.assertEquals(testMsgWithData.getMsgId(), newMsg.getMsgId());

        System.out.println("Comparing msg type...");
        Assertions.assertEquals(testMsgWithData.getCommand(), newMsg.getCommand());

        System.out.println("Comparing msg parameter...");
        Assertions.assertEquals(testMsgWithData.getParameter(), newMsg.getParameter());

        System.out.println("Comparing msg data...");
        Assertions.assertArrayEquals(testMsgWithData.getData(), newMsg.getData());


        newMsg = new CommandMessage(bytesWithoutData);
        System.out.println(testMsgWithoutData);
        System.out.println(newMsg);

        System.out.println("Comparing msg id...");
        Assertions.assertEquals(testMsgWithoutData.getMsgId(), newMsg.getMsgId());

        System.out.println("Comparing msg type...");
        Assertions.assertEquals(testMsgWithoutData.getCommand(), newMsg.getCommand());

        System.out.println("Comparing msg parameter...");
        Assertions.assertEquals(testMsgWithoutData.getParameter(), newMsg.getParameter());

        System.out.println("Comparing msg data...");
        Assertions.assertArrayEquals(testMsgWithoutData.getData(), newMsg.getData());


        newMsg = new CommandMessage(lsBytes);
        System.out.println(putLS);
        System.out.println(newMsg);

        System.out.println("Comparing msg id...");
        Assertions.assertEquals(putLS.getMsgId(), newMsg.getMsgId());

        System.out.println("Comparing msg type...");
        Assertions.assertEquals(putLS.getCommand(), newMsg.getCommand());

        System.out.println("Comparing msg parameter...");
        Assertions.assertEquals(putLS.getParameter(), newMsg.getParameter());

        System.out.println("Comparing msg data...");
        Assertions.assertArrayEquals(putLS.getData(), newMsg.getData());
        System.out.println(fl);
        FileList newFl = (FileList) MessageUtils.BytesToObject(newMsg.getData());
        System.out.println(newFl);
    }
}