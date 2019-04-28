import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import java.io.*;
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
    private DatabaseReference firebaseDatabase;
    private NotificationSender notificationSender;

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

        //linux server
        FileInputStream serviceAccount = new FileInputStream("/home/sam/IoT-Project/server/output/jar/iot-project-f9452-firebase-adminsdk-g3x98-15166cb812.json");
        //computer at home
        //FileInputStream serviceAccount = new FileInputStream("D:\\git\\IoT-Project\\Server\\output\\jar\\iot-project-f9452-firebase-adminsdk-g3x98-15166cb812.json");
        //laptop
        //FileInputStream serviceAccount = new FileInputStream("C:\\Users\\Sam-Laptop\\git\\IoT-Project\\server\\output\\jar\\iot-project-f9452-firebase-adminsdk-g3x98-15166cb812.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://iot-project-f9452.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

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
        //private ObjectInputStream input;
        //private ObjectOutputStream output;
        private BufferedWriter output;
        private BufferedReader input;
        private boolean activeConnection;
        private String token;
        private String tokenCount;
        private boolean newToken;

        public ConnectionHandler(int id){
            clientID = id;
            this.activeConnection = false;
        }

        public void waitForConnection() throws IOException{
            clientSocket = serverSocket.accept();

            activeConnection = true;
        }

        public void initializeBuffers(){
            /*try{
                //get input buffer initialized
                input = new ObjectInputStream(clientSocket.getInputStream());

                //get output buffer initialized
                output = new ObjectOutputStream(clientSocket.getOutputStream());
                output.flush();
                System.out.println("Buffers successfully initialized");
            }catch (IOException e){
                System.err.println("Error initializing buffers...");
                System.err.println(e);
                e.printStackTrace();
                activeConnection = false;
            }*/

            try{
                output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                output.flush();

                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println("Buffers successfully setup");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void processConnection(){
            int secondsActive = 0;
            while (activeConnection){
                try{
                    String type = input.readLine(); //these were all changed from readObject (String) to this
                    System.out.println(type);
                        if(type.equals("client")){
                            String userID = input.readLine();
                            System.out.println(userID);
                            token = input.readLine();
                            System.out.println("Token Part1: " + token);
                            token += input.readLine();
                            System.out.println("Token Full: " + token);

                            try{

                                firebaseDatabase.child("/users/" + userID + "/tokens/count")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            tokenCount = String.valueOf(dataSnapshot.getValue());
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                Thread.sleep(100);

                                firebaseDatabase.child("/users/" + userID + "/tokens")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                int intTokenCount = Integer.parseInt(tokenCount);
                                                for(int i=0; i<intTokenCount; i++){
                                                    String tempToken = (String) dataSnapshot.child(String.valueOf(i)).getValue();
                                                    System.out.println(tempToken);
                                                    if(tempToken.matches(token)){
                                                        newToken = false;
                                                        break;
                                                    }
                                                    else{
                                                        newToken = true;
                                                    }
                                                }
                                                if(intTokenCount == 0){
                                                    newToken = true;
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                Thread.sleep(100);


                                if(newToken){
                                    firebaseDatabase.child("/users/" + userID + "/tokens/" + tokenCount).setValue(token, null);
                                    String newCount = String.valueOf(Integer.parseInt(tokenCount) + 1);
                                    firebaseDatabase.child("/users/" + userID + "/tokens/count").setValue(Integer.valueOf(newCount), null);
                                    newToken = false;
                                }






                                Message message = Message.builder()
                                        .putData("title", "UserID: " + userID)
                                        .putData("content", "Token: " + token)
                                        .setToken(token)
                                        .build();

                                String response = FirebaseMessaging.getInstance().send(message);
                                System.out.println("Successfully sent message: " + response);
                                System.out.println("closing connection with client: " + clientID);
                                closeConnection();
                                activeConnection=false;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        else if(type.equals("bottle")){
                            String message = input.readLine();
                            if(message.equals("gps")){
                                System.out.println("bottle detected");
                                String combinedData = input.readLine();
                                String[] data = combinedData.split(",");
                                //deal with database

                            }
                            else if(message.equals("open")){
                                System.out.println("open detected");
                                String combinedData = input.readLine();
                                System.out.println(combinedData);
                                String data[] = combinedData.split(",");
                                System.out.println(data);
                                //deal with database
                            }
                            closeConnection();
                            activeConnection = false;
                        }
                        else{
                            System.err.println("Problem finding type");
                        }
                    System.out.println(type);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            try{
                Thread.sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
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


