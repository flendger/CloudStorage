package messages.command;

import java.util.Arrays;

public enum CommandMessageType {

    MSG_OK((short) 0, "ok"),
    MSG_ERR((short) 1, "error"),
    MSG_GET_LS((short) 2, "ls"),
    MSG_PUT_LS((short) 3, "put_ls"),
    MSG_GET_CD((short) 4, "cd"),
    MSG_PUT_CD((short) 5, "put_cd"),
    MSG_GET_MD((short) 6, "mkdir"),
    MSG_PUT_MD((short) 7, "put_mkdir"),
    MSG_FILE_DOWNLOAD_ERR((short) 8, "download_err"),
    MSG_FILE_UPLOAD_ERR((short) 9, "upload_err"),
    MSG_GET_FILE((short) 10, "download"),
    MSG_AUTH_OK((short) 11, "auth ok"),
    MSG_AUTH_ERROR((short) 12, "auth error"),
    MSG_REG_OK((short) 13, "reg ok"),
    MSG_REG_ERROR((short) 14, "reg error");


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