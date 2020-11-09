package messages;

import java.util.Arrays;

public enum MessageType {

    MSG_OK((short) 0, "ok", true),
    MSG_ERR((short) 1, "error", true),
    MSG_GET_LS((short) 2, "get ls", true),
    MSG_TAKE_LS((short) 3, "put ls", true),
    MSG_GET_FILE((short) 4, "get file", true),
    MSG_PUT_FILE((short) 5, "put file", true),
    MSG_TAKE_FILE((short) 6, "take file", false);


    private final short id;
    private final String command;
    private final boolean notFile;

    MessageType(short id, String command, boolean notFile) {
        this.id = id;
        this.command = command;
        this.notFile = notFile;
    }

    public short getId() {
        return id;
    }

    public String getCommand() {
        return command;
    }

    public boolean isNotFile() {
        return notFile;
    }

    @Override
    public String toString() {
        return command + " (" + id + ")";
    }

    public static MessageType findById(short id) {
        return Arrays.stream(MessageType.values())
                .filter(arg -> arg.id == id)
                .findFirst()
                .orElse(null);
    }
}