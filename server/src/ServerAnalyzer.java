import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.sun.deploy.util.ArrayUtil;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

public class ServerAnalyzer implements Runnable{
    private static DatabaseReference firebaseDatabase;
    private DataSnapshot bigDataSnapshot;
    private ArrayList usernameKeys;
    private ArrayList medicineSnapshotList;
    private ArrayList<int[]> dateList;
    private int clusters;
    private volatile boolean dataReady;
    private boolean sent = false;

    @Override
    public void run() {

        usernameKeys = new ArrayList<String>(128);
        medicineSnapshotList = new ArrayList<String>(256);
        dateList = new ArrayList<int[]>(256);

        while(true){
            //System.out.println("Starting analysis...");
            dataReady = false;
            firebaseDatabase = Server.getFirebaseDatabase();
            firebaseDatabase.child("/users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    bigDataSnapshot = dataSnapshot;
                    dataReady = true;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            while(!dataReady){

            }

        for(DataSnapshot userSnapshot: bigDataSnapshot.getChildren()){
            usernameKeys.add(userSnapshot.getKey());
        }

        //going through users
        for(int i=0; i<usernameKeys.size(); i++){
            //going through user medicine
            int currentMedicine = 0;
            for(DataSnapshot medicineSnapshot : bigDataSnapshot.child("/" + usernameKeys.get(i)).child("/medicine").getChildren()) {
                if(!(medicineSnapshot.child("/taken/count").getValue() == null) && (long) medicineSnapshot.child("/taken/count").getValue() > 5){
                    int dateCount = Math.toIntExact((long) medicineSnapshot.child("/taken/count").getValue());

                    for(int j=0; j<dateCount; j++){
                        String temp = (String) medicineSnapshot.child("/taken/" + j).getValue();
                        String[] tempStringArray = temp.split(",");
                        int[] tempIntArray = {Integer.parseInt(tempStringArray[3]), Integer.parseInt(tempStringArray[4])};
                        dateList.add(tempIntArray);
                    }

                    //File trainingData = new File("C:\\Users\\Sam-Laptop\\git\\IoT-Project\\server\\output\\jar\\TrainingData.csv");
                    File trainingData = new File("/home/sam/IoT-Project/server/output/jar/TrainingData.csv");
                    int[] times = new int[dateCount];
                    for(int k=0; k<dateCount; k++){
                        times[k] = MachineLearning.knearest(trainingData,  dateList.get(k), 2);
                    }

                    int max = 1;
                    if(times.length > 0){
                        max = maxRepeating(times, times.length, 23);
                    }

                    //System.out.println("User: " + usernameKeys.get(i));
                    //System.out.println("Medicine: " + medicineSnapshot.getKey());
                    //System.out.println("Class: " + max);

                    String lastDose = (String) bigDataSnapshot.child("/" + usernameKeys.get(i)).child("/medicine/").child(medicineSnapshot.getKey()).child("/lastDose").getValue();
                    String nextDose = (String) bigDataSnapshot.child("/" + usernameKeys.get(i)).child("/medicine/").child(medicineSnapshot.getKey()).child("/nextDose").getValue();
                    String[] lastDoseData = lastDose.split(",");
                    String[] nextDoseData = nextDose.split(",");
                    int hour = max;
                    int minute = 0;
                    if(lastDoseData[2].equals(nextDoseData[2])){
                        nextDose = "";
                        for(int z=0; z<nextDoseData.length; z++){
                            if(z == 2){
                                nextDose += String.valueOf(Integer.valueOf(nextDoseData[z]) + 1);
                            }
                            else if(z == 3){
                                nextDose += max;
                            }
                            else{
                                nextDose += nextDoseData[z];
                            }

                            if(z != nextDoseData.length-1){
                                nextDose += ",";
                            }
                        }
                    }else{
                        Calendar today = Calendar.getInstance();
                        String year = String.valueOf(today.get(Calendar.YEAR));
                        nextDose = year.substring(2) + "," + (today.get(Calendar.MONTH)+1) + "," + today.get(Calendar.DAY_OF_MONTH) + ",";
                        LocalTime time = LocalTime.now();
                        nextDose += hour + "," + minute;
                    }
                    firebaseDatabase.child("/users/" + (String) usernameKeys.get(i)).child("/medicine/").child(medicineSnapshot.getKey()).child("/nextDose")
                            .setValue(nextDose, null);


                }else{

                }


                String remainingDays = (String) bigDataSnapshot.child("/" + usernameKeys.get(i)).child("/medicine/").child(medicineSnapshot.getKey()).child("/remainingDays").getValue();

                System.out.println(remainingDays);
                if(!sent && Integer.valueOf(remainingDays) < 15){
                    String medicineName = (String) bigDataSnapshot.child("/" + usernameKeys.get(i)).child("/medicine/").child(medicineSnapshot.getKey()).child("/name").getValue();

                    for(DataSnapshot tokenSnap : bigDataSnapshot.child("/" + usernameKeys.get(i)).child("/tokens").getChildren()){
                        System.out.println(tokenSnap.child("/count").getValue());

                           String token = (String) tokenSnap.child(String.valueOf(0)).getValue();
                           System.out.println(tokenSnap);

                           Message message = Message.builder()
                                   .putData("title", "Two Weeks of Doses Left")
                                   .putData("content", "Medicine" + medicineName + "has two weeks of doeses remaining.")
                                   .setToken(token)
                                   .build();

                           try{
                               String response = FirebaseMessaging.getInstance().send(message);
                               System.out.println("User Notified" + response);
                               Thread.sleep(2000);

                           }catch (Exception e){
                               e.printStackTrace();
                           }

                    }
                }
            }
        }

            try{
                //System.out.println("Analyzer Sleeping");
                Thread.sleep(500);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            dataReady = false;
        }

    }

    static int maxRepeating(int arr[], int n, int k)
    {
        // Iterate though input array, for every element
        // arr[i], increment arr[arr[i]%k] by k
        for (int i = 0; i< n; i++)
            arr[(arr[i]%k)] += k;

        // Find index of the maximum repeating element
        int max = arr[0], result = 0;
        for (int i = 1; i < n; i++)
        {
            if (arr[i] > max)
            {
                max = arr[i];
                result = i;
            }
        }

        /* Uncomment this code to get the original array back
        for (int i = 0; i< n; i++)
          arr[i] = arr[i]%k; */

        // Return index of the maximum element
        return result;
    }
}
