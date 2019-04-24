import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.DataFormatException;

public class Connection implements Runnable{
    private String ip;
    private int port;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean done = false;
    private volatile boolean connected = false;

    public Connection(String ip, int port) throws IOException{
        this.ip = ip;
        this.port = port;
    }

    public Connection(String ip, String port) throws IOException {
        this(ip, Integer.valueOf(port));
    }

    public void connect() {
        try{
            this.socket = new Socket(InetAddress.getByName(ip),port);
            System.out.println(socket.isConnected());
        }catch (Exception e){
            e.printStackTrace();
            System.err.println(e);
        }
    }

    public void initializeStreams(){
        try{
            //get output buffer initialized
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();

            //get input buffer initialized
            input = new ObjectInputStream(socket.getInputStream());

            System.out.println("Buffers successfully initialized");
        }catch (IOException e){
            System.err.println("Error initializing buffers...");
            System.err.println(e);
        }
    }

    public void processConnection(){
        connected = true;
        while(!done){

        }
    }

    public void closeConnection(){
        System.out.println("Attempting to close connection with server");
        try{
            socket.close();
            input.close();
            output.close();
            System.out.println("Connection connection closed" );

        }catch (IOException e){
            System.err.println("Error closing client connection");
            System.err.println(e);
        }
    }

    public void sendData(Object data){
        try{
            output.writeObject(data);
            output.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Object receiveData() throws DataFormatException {
        try{
            return input.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new DataFormatException("Data unsuccessfully read");
    }

    //Runnable interface
    @Override
    public void run() {
        try{
            connect();
            initializeStreams();
            processConnection();
        }catch (Exception e){
            System.err.println(e);
        }
    }

    public static void main(String[] args){
        try{
            Connection connection = new Connection("localhost", 4044);
            ExecutorService executor = Executors.newCachedThreadPool();
            executor.execute(connection);
            connection.sendData("test");
            System.out.println(connection.receiveData());
        }catch (Exception e){
            System.err.println(e);
        }

    }
}