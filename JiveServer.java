import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;

public class JiveServer {
    private static final int DEFAULT_PORT = 7007;

    private int port;
    private final ServerSocket server;
    private CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public JiveServer(int port) throws IOException {
        server = new ServerSocket(port);
        server.setReuseAddress(true);
        System.out.println("[INFO] Server started on port " + port);
    }

    public void start() throws IOException {
        while (true) {
            ClientHandler handler = new ClientHandler(server.accept(), this);
            clients.add(handler);
            handler.start();
            System.out.println("[INFO] Client connected");
            System.out.println("[INFO] Connected Clients: " + clients.size());
        }
    }

    public void remove(ClientHandler client) {
        clients.remove(client);
    }

    public int clientCount() {
        return clients.size();
    }

    public void broadcast(String msg, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client == sender) continue;
            client.sendMsg(msg);
        }
    }

    public static void main(String[] args) {
        try {
            JiveServer server = new JiveServer(DEFAULT_PORT);
            server.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}

class ClientHandler extends Thread {

    private final JiveServer server;
    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;

    public ClientHandler(Socket client, JiveServer server) throws IOException {
        this.server = server;
        this.client = client;

        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    public void run() {
        try {
            readLoop();
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
        server.remove(this);
        System.out.println("[INFO] Client disconnected");
        System.out.println("[INFO] Connected Clients: " + server.clientCount());
    }

    private void readLoop() throws IOException {
        try {

            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("[INFO] " + msg);
                server.broadcast(msg, this);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            in.close();
            out.close();
            client.close();
        }
    }

    public void sendMsg(String msg) {
        out.println(msg);
    }
}
