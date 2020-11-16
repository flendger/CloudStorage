package services;

import java.io.Serializable;
import java.nio.file.Path;

public class UserProfile implements Serializable {

    public final int id;
    public final String user;
    public final String pass;
    public final String root;
    public String curDir;

    public UserProfile(int id, String user, String pass, String root) {
        this.id = id;
        this.user = user;
        this.pass = pass;
        this.root = root;
        this.curDir = root;
    }

    public String getAbsoluteRoot(String rootDir) {
        return Path.of(rootDir, root).toString();
    }
}
