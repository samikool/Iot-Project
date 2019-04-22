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
    public Server(String port) throws IOException{
        this(Integer.valueOf(port));
    }

    public void start() throws IOException{
        serverSocket = new ServerSocket(port,100);

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
        private ObjectInputStream inputBuffer;
        private ObjectOutputStream outputBuffer;
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
                outputBuffer = new ObjectOutputStream(clientSocket.getOutputStream());
                outputBuffer.flush();

                //get input buffer initialized
                inputBuffer = new ObjectInputStream(clientSocket.getInputStream());

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
                    Thread.sleep(1000);
                    secondsActive += 1;
                }catch (Exception e){}

                if(secondsActive == 15){

                    /*try{
                        outputBuffer.write(1);
                    }catch (Exception e){
                        System.err.println(e);
                    }finally {
                        closeConnection();
                    }*/
                    closeConnection();
                    activeConnection = false;
                }else{
                    System.out.println("Connection: " + clientID + " active");
                }
            }
        }

        public void closeConnection(){
            System.out.println("Attempting to close connection with client: " + clientID);
            try{
                clientSocket.close();
                inputBuffer.close();
                outputBuffer.close();
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
            Server testServer = new Server("22300");
            testServer.start();


        }catch (Exception e){
            System.err.println(e);
        }



    }
}


