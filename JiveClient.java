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
    private final String name;

    public Client(int port) throws UnknownHostException, IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        name = readUsername(stdin);

        client = new Socket("localhost", port);
        System.out.println("[INFO] Connected to server");

        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        ) {

            String msg;
            while (!(msg = stdin.readLine()).equals("quit")) {
                msg = name + ": " + msg;
                System.out.println(msg);
                out.println(msg);

                String recv = in.readLine();
                if (recv == null) {
                    System.out.println("[INFO] Server closed");
                    break;
                } else {
                    System.out.println(recv);
                }
            }
        } catch(IOException e) {
            System.err.println(e.getMessage());
        } finally {
            client.close();
        }

        System.out.println("[INFO] Client disconnected");
    }

    private String readUsername(BufferedReader stdin) throws IOException {
        System.out.print("Enter username: ");
        return stdin.readLine();
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
