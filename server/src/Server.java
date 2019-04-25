import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private ConnectionHandler[] connectionHandlers;
    private ExecutorService executor;
    private int clientsConnected;


    //main constructor actually used
    //TODO: error checking
    public Server(int port){
        this.connectionHandlers = new ConnectionHandler[100];
        this.executor = Executors.newCachedThreadPool();
        this.clientsConnected = 0;
        this.port = port;

    }

    //constructor to catch strings as input for port
    //TODO: error checking
    public Server(String port) {
        this(Integer.valueOf(port));
    }

    public void start() throws IOException{
        serverSocket = new ServerSocket(port,100);

        FileInputStream serviceAccount =
                new FileInputStream("~/IoT-Project/server/output/jar/iot-project-f9452-firebase-adminsdk-g3x98-15166cb812.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://iot-project-f9452.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);


        while(true){
            try{
                System.out.println("waiting for client: " + clientsConnected + " to connect...");
                connectionHandlers[clientsConnected] = new ConnectionHandler(clientsConnected);
                connectionHandlers[clientsConnected].waitForConnection();

                executor.execute(connectionHandlers[clientsConnected]);

                System.out.println("Connection: " + clientsConnected + " successfully made");
                clientsConnected++;
            }catch (Exception e){
                System.err.println("Error connecting to cleint " + clientsConnected);
                System.err.println(e);
            }
        }
    }

    private class ConnectionHandler implements Runnable{
        private Socket clientSocket;
        private int clientID;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private boolean activeConnection;

        public ConnectionHandler(int id){
            clientID = id;
            this.activeConnection = false;
        }

        public void waitForConnection() throws IOException{
            clientSocket = serverSocket.accept();
            activeConnection = true;
        }

        public void initializeBuffers(){
            try{
                //get output buffer initialized
                output = new ObjectOutputStream(clientSocket.getOutputStream());
                output.flush();

                //get input buffer initialized
                input = new ObjectInputStream(clientSocket.getInputStream());

                System.out.println("Buffers successfully initialized");
            }catch (IOException e){
                System.err.println("Error initializing buffers...");
                System.err.println(e);
                activeConnection = false;
            }

        }

        public void processConnection(){
            int secondsActive = 0;
            while (activeConnection){
                try{
                    String command = (String) input.readObject();
                    System.out.println(command);
                    output.writeObject("Yeaeun is a YepoDweigi");
                    output.flush();
                }catch (Exception e){}
            }
        }

        public void closeConnection(){
            System.out.println("Attempting to close connection with client: " + clientID);
            try{
                clientSocket.close();
                input.close();
                output.close();
                System.out.println("Client connection " + clientID + " closed." );
            }catch (IOException e){
                System.err.println("Error closing client connection");
                System.err.println(e);
            }

        }

        @Override
        public void run() {
            initializeBuffers();
            processConnection();
        }
    }

    public static void main(String[] args){
        try{
            Server testServer = new Server("4044");
            testServer.start();
        }catch (Exception e){
            System.err.println(e);
        }
    }
}


