import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;

class Client {

    private final Socket client;

    public Client(int port) throws UnknownHostException, IOException {
        client = new Socket("localhost", port);
        System.out.println("[INFO] Client connected");

        try (
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            InputStream in = client.getInputStream();
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        ) {

            byte[] buf = new byte[256];
            String line;
            while (!(line = stdin.readLine()).equals("quit")) {
                System.out.println("send: " + line);
                out.println(line);

                int n = in.read(buf);
                String recv = new String(buf, 0, n).strip();
                System.out.println("revc: " + recv);
            }
        } catch(IOException e) {
            System.err.println(e.getMessage());
        } finally {
            client.close();
        }

        System.out.println("[INFO] Client disconnected");
    }
}

public class JiveClient {
    public static void main(String[] args) {
        try {
            Client client = new Client(7007);
        } catch(IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
