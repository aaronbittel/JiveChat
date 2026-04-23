import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;

class EchoServer {
    private int port;
    private Socket client;
    private final ServerSocket server;

    public EchoServer(int port) throws IOException {
        server = new ServerSocket(port);
        server.setReuseAddress(true);
        System.out.println("[INFO] Server started on port " + port);
    }

    public void start() throws IOException {
        client = server.accept();
        System.out.println("[INFO] Client connected");
        readLoop();
        System.out.println("[INFO] Server closed");
    }

    private void readLoop() throws IOException {
        int n = 0;
        try (
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
        ) {
            byte[] buf = new byte[256];

            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            client.close();
        }
        if (n == -1) System.out.println("[INFO] Client disconnected");
    }
}

public class JiveChat {

    private static final int DEFAULT_PORT = 7007;

    public static void main(String[] args) {
        try {
            EchoServer server = new EchoServer(DEFAULT_PORT);
            server.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}

