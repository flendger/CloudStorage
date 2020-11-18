package messages.command;

import java.util.Arrays;

public enum CommandMessageType {

    MSG_OK((short) 0, "ok"),
    MSG_ERR((short) 1, "error"),
    MSG_GET_LS((short) 2, "ls"),
    MSG_PUT_LS((short) 3, "put ls"),
    MSG_GET_CD((short) 4, "cd"),
    MSG_PUT_CD((short) 5, "put cd"),
    MSG_GET_MD((short) 6, "mkdir"),
    MSG_PUT_MD((short) 7, "put mkdir"),
    MSG_FILE_DOWNLOAD_ERR((short) 8, "download err"),
    MSG_FILE_UPLOAD_ERR((short) 9, "upload err");



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