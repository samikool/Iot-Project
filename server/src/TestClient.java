import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TestClient {
    private String ip;
    private int port;
    private Socket socket;
    private ObjectOutputStream outputBuffer;
    private ObjectInputStream inputBuffer;

    public TestClient(String ip, int port) throws IOException{
        this.ip = ip;
        this.port = port;
        this.socket = new Socket(InetAddress.getByName(ip),port);
    }

    public TestClient(String ip, String port) throws IOException {
        this(ip, Integer.valueOf(port));
    }

    public void stayConnected(){
        try{
            if(inputBuffer.readInt() == 1){
                closeConnection();
            }
        }catch (IOException e){
            System.err.println("Unable to read data...");
            System.err.println("Closing connection...");
            closeConnection();
        }

    }

    public void intializeStreams(){
        try{
            //get output buffer initialized
            outputBuffer = new ObjectOutputStream(socket.getOutputStream());
            outputBuffer.flush();

            //get input buffer initialized
            inputBuffer = new ObjectInputStream(socket.getInputStream());

            System.out.println("Buffers successfully initialized");
        }catch (IOException e){
            System.err.println("Error initializing buffers...");
            System.err.println(e);
        }
    }

    public void closeConnection(){
        System.out.println("Attempting to close connection with server");
        try{
            socket.close();
            inputBuffer.close();
            outputBuffer.close();
            System.out.println("Client connection closed" );

        }catch (IOException e){
            System.err.println("Error closing client connection");
            System.err.println(e);
        }

    }

    public static void main(String[] args){
        try{
            TestClient client = new TestClient("localhost", 22300);
            client.intializeStreams();
            client.stayConnected();
        }catch (Exception e){
            System.err.println(e);
        }

    }
}
