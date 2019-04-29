import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sun.deploy.util.ArrayUtil;

import java.io.File;
import java.util.ArrayList;

public class ServerAnalyzer implements Runnable{
    private static DatabaseReference firebaseDatabase;
    private DataSnapshot bigDataSnapshot;
    private ArrayList usernameKeys;
    private ArrayList medicineSnapshotList;
    private ArrayList<int[]> dateList;
    private int clusters;
    private volatile boolean dataReady;

    @Override
    public void run() {

        usernameKeys = new ArrayList<String>(128);
        medicineSnapshotList = new ArrayList<String>(256);
        dateList = new ArrayList<int[]>(256);

        while(true){
            System.out.println("Starting analysis...");
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
            System.out.println(usernameKeys.get(i));
            System.out.println(bigDataSnapshot.child("/" + usernameKeys.get(i)).child("/medicine").getChildren());
            //going through user medicine
            int currentMedicine = 0;
            for(DataSnapshot medicineSnapshot : bigDataSnapshot.child("/" + usernameKeys.get(i)).child("/medicine").getChildren()) {
                System.out.println(medicineSnapshot.child("/taken/count").getValue());

                if(!(medicineSnapshot.child("/taken/count").getValue() == null)){
                    int dateCount = Math.toIntExact((long) medicineSnapshot.child("/taken/count").getValue());

                    for(int j=0; j<dateCount; j++){
                        String temp = (String) medicineSnapshot.child("/taken/" + j).getValue();
                        String[] tempStringArray = temp.split(",");
                        int[] tempIntArray = {Integer.parseInt(tempStringArray[3]), Integer.parseInt(tempStringArray[4])};
                        dateList.add(tempIntArray);
                    }

                    double[] doubles = new double[dateList.size()];
                    //File trainingData = new File("C:\\Users\\Sam-Laptop\\git\\IoT-Project\\server\\output\\jar\\TrainingData.csv");
                    File trainingData = new File("/home/sam/IoT-Project/server/output/jar/TrainingData.csv");
                    int[] times = new int[dateCount];
                    for(int k=0; k<dateCount; k++){
                        times[k] = MachineLearning.knearest(trainingData,  dateList.get(k), 2);
                    }

                    int max = maxRepeating(times, times.length, 23);
                    System.out.println("User: " + usernameKeys.get(i));
                    System.out.println("Medicine: " + medicineSnapshot.getKey());
                    System.out.println("Class: " + max);

                    String lastDose = (String) bigDataSnapshot.child((String) usernameKeys.get(i)).child(medicineSnapshot.getKey()).child("lastDose").getValue();
                    bigDataSnapshot.child((String) usernameKeys.get(i)).child(medicineSnapshot.getKey()).child("Next Dose: ");
                }else{

                }
            }

        }

            try{
                System.out.println("Analyzer Sleeping");
                Thread.sleep(60000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
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
