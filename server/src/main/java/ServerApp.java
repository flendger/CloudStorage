public class ServerApp {
    public static void main(String[] args) {
        try {
            new CloudServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
