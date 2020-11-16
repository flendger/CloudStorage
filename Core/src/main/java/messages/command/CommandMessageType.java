package messages.command;

import java.util.Arrays;

public enum CommandMessageType {

    MSG_OK((short) 0, "ok"),
    MSG_ERR((short) 1, "error"),
    MSG_GET_LS((short) 2, "ls"),
    MSG_PUT_LS((short) 3, "put ls"),
    MSG_GET_FILE((short) 4, "get file"),
    MSG_PUT_FILE((short) 5, "put file"),
    MSG_GET_CD((short) 6, "cd"),
    MSG_PUT_CD((short) 7, "put cd"),
    MSG_GET_MD((short) 8, "mkdir"),
    MSG_PUT_MD((short) 9, "put mkdir");


    private final short id;
    private final String command;

    CommandMessageType(short id, String command) {
        this.id = id;
        this.command = command;
    }

    public short getId() {
        return id;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return command + " (" + id + ")";
    }

    public static CommandMessageType findById(short id) {
        return Arrays.stream(CommandMessageType.values())
                .filter(arg -> arg.id == id)
                .findFirst()
                .orElse(null);
    }

    public static CommandMessageType findByCommand(String command) {
        return Arrays.stream(CommandMessageType.values())
                .filter(arg -> arg.command.equals(command))
                .findFirst()
                .orElse(null);
    }
}