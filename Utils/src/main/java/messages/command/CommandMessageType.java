package messages.command;

import java.util.Arrays;

public enum CommandMessageType {

    MSG_OK((short) 0, "ok"),
    MSG_ERR((short) 1, "error"),
    MSG_GET_LS((short) 2, "ls"),
    MSG_GET_FILE((short) 3, "get file"),
    MSG_PUT_FILE((short) 4, "put file");


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