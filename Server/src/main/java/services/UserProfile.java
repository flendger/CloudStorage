package services;

public class UserProfile {

    public final int id;
    public final String user;
    public final String pass;
    public final String root;

    public UserProfile(int id, String user, String pass, String root) {
        this.id = id;
        this.user = user;
        this.pass = pass;
        this.root = root;
    }
}
