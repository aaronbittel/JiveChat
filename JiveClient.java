import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class JiveClient {

    private final Socket client;
    private final String name;

    public JiveClient(int port) throws UnknownHostException, IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        name = readUsername(stdin);

        client = new Socket("localhost", port);
        System.out.println("[INFO] Connected to server");

        Thread readThread = new Thread(new MsgReader(client));
        readThread.start();

        try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
            String msg;
            while (!(msg = stdin.readLine()).equals("quit")) {
                msg = name + ": " + msg;
                out.println(msg);
            }
        } catch(IOException e) {
            System.err.println(e.getMessage());
        } finally {
            readThread.interrupt();
            client.close();
        }

        System.out.println("[INFO] Client disconnected");
    }

    private String readUsername(BufferedReader stdin) throws IOException {
        System.out.print("Enter username: ");
        return stdin.readLine();
    }
    public static void main(String[] args) {
        try {
            JiveClient client = new JiveClient(7007);
        } catch(IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}

class MsgReader implements Runnable {

    private final Socket client;
    private final BufferedReader in;

    public MsgReader(Socket client) throws IOException {
        this.client = client;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    public void run() {
        String recv;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                recv = in.readLine();
                if (recv == null) {
                    System.out.println("[INFO] Server closed");
                    break;
                } else {
                    System.out.println(recv);
                }
            } catch(IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
