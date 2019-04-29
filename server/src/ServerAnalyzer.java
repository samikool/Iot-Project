import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ServerAnalyzer implements Runnable{
    private static DatabaseReference firebaseDatabase;
    private ArrayList usernameKeys;
    private ArrayList dateSnapshotList;
    private ArrayList dateList;
    private int clusters;

    @Override
    public void run() {
        usernameKeys = new ArrayList<String>(128);
        dateSnapshotList = new ArrayList<String>(256);
        dateList = new ArrayList<String>(256);
        while(true){
            firebaseDatabase = Server.getFirebaseDatabase();
            firebaseDatabase.child("/users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                        usernameKeys.add(userSnapshot.getKey());
                    }
                    System.out.println();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            try{
                Thread.sleep(60000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }
}
