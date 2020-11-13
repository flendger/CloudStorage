package messages;

import java.util.Arrays;

public enum MessageType {
    AUTH((short) 0),
    COMMAND((short) 1),
    DATA_TRANSFER((short) 2);

    private short id;

    MessageType(short id) {
        this.id = id;
    }

    public short getId() {
        return id;
    }

    public static MessageType getInstance(short id) {
        return Arrays.stream(MessageType.values())
                .filter(arg -> arg.id == id)
                .findFirst()
                .orElse(null);
    }
}
