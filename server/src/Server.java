import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private ConnectionHandler[] connectionHandlers;
    private ExecutorService executor;
    private int clientsConnected;
    private ServerAnalyzer serverAnalyzer;
    private static DatabaseReference firebaseDatabase;
    private NotificationSender notificationSender;

    //main constructor actually used
    //TODO: error checking
    public Server(int port){
        this.connectionHandlers = new ConnectionHandler[512];
        this.executor = Executors.newCachedThreadPool();
        this.clientsConnected = 0;
        this.port = port;
    }


    public static DatabaseReference getFirebaseDatabase() {
        return firebaseDatabase;
    }

    //constructor to catch strings as input for port
    //TODO: error checking
    public Server(String port) {
        this(Integer.valueOf(port));
    }

    public void start() throws IOException{
        serverSocket = new ServerSocket(port,512);
        serverAnalyzer = new ServerAnalyzer();

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

        executor.execute(serverAnalyzer);

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
        private DataSnapshot bigSnapshot;

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
                FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        bigSnapshot = dataSnapshot;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
                                                    if(tempToken.equals(token)){
                                                        System.out.println("Token not found");
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
                            System.out.println("bottle detected");
                            String combinedData = input.readLine();
                            System.out.println(combinedData);
                            String[] data = combinedData.split(",");
                            for(int i=0; i<data.length; i++){
                                System.out.println(data[i]);
                            }
                            //deal with database
                            System.out.println(bigSnapshot.child("/claimed/").child(data[12]).getValue());
                            if(!bigSnapshot.child("/claimed/").hasChild(data[12])){
                                firebaseDatabase.child("/claimed/").child(data[12]).setValue(false, null);
                            }
                            else if((boolean) bigSnapshot.child("/claimed/").child(data[12]).getValue()){
                                for(DataSnapshot users : bigSnapshot.child("/users/").getChildren()){
                                    System.out.println(users.getKey());
                                    for(DataSnapshot medicine: users.child("/medicine/").getChildren()){
                                        System.out.println(medicine.getKey());
                                        System.out.println(medicine.getKey().equals(data[12]));
                                        if(medicine.getKey().equals(data[12])){
                                            String latitude = convertLocation(data[3], data[4]);
                                            String longitude = convertLocation(data[3], data[4]);
                                            System.out.println("/users/" + users.getKey() + "/" + medicine.getKey() + "/latitude");
                                            firebaseDatabase.child("/users/" + users.getKey() + "/medicine/" + medicine.getKey() + "/latitude").setValue(latitude, null);
                                            firebaseDatabase.child("/users/" + users.getKey() + "/medicine/" + medicine.getKey() + "/longitude").setValue(longitude, null);
                                            firebaseDatabase.child("/users/" + users.getKey() + "/medicine/" + medicine.getKey() + "/temperature").setValue(data[13], null);

                                            //get remaining days of doses
                                            int remainingPills = (int) bigSnapshot.child("/users/" + users.getKey() + "/medicine/" + medicine.getKey() + "/remaining").getValue();
                                            int dosesPerDay = (int) bigSnapshot.child("/users/" + users.getKey() + "/medicine/" + medicine.getKey() + "/dosesPerDay").getValue();
                                            remainingPills -= dosesPerDay;
                                            int remainingDays = remainingPills/dosesPerDay;
                                            firebaseDatabase.child("/users/" + users.getKey() + "/medicine/" + medicine.getKey() + "/remainingDays").setValue(remainingDays, null);
                                            firebaseDatabase.child("/users/" + users.getKey() + "/medicine/" + medicine.getKey() + "/remainingPills").setValue(remainingPills, null);

                                            //get current time
                                            Calendar today = Calendar.getInstance();
                                            String lastDose = today.get(Calendar.YEAR) + "," + today.get(Calendar.MONTH) + "," + today.get(Calendar.DAY_OF_MONTH) + ",";
                                            LocalTime time = LocalTime.now();
                                            int hour = time.getHour();
                                            int minute = time.getMinute();
                                            lastDose += hour + "," + minute;
                                            firebaseDatabase.child("/users/" + users.getKey() + "/medicine/" + medicine.getKey() + "/lastDose").setValue(lastDose, null);

                                            //update count

                                        }
                                    }
                                }
                            }



                            //close connection
                            closeConnection();
                            activeConnection = false;
                        }
                        else{
                            System.err.println("Problem finding type");
                        }
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

    public static String convertLocation(String location, String d){
        String coordinate;
        int multiplier = 0;
        if(d.equals("N") || d.equals("E")){
            multiplier = 1;
        }
        else if(d.equals("S") || d.equals("W")){
            multiplier = -1;
        }
        String DD = String.valueOf((int) Double.parseDouble(location)/100);
        String MM = String.valueOf((Double.parseDouble(location) - Double.parseDouble(DD)*100));
        MM = String.valueOf(Double.parseDouble(MM)/60);



        coordinate = String.valueOf(Double.parseDouble(DD) + Double.parseDouble(MM));


        return coordinate;
    }
    public static void main(){

    }
}


