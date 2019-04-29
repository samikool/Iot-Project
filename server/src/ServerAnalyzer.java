import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ServerAnalyzer implements Runnable{
    private static DatabaseReference firebaseDatabase;
    private ArrayList usernameSnapshotList;
    private ArrayList dateSnapshotList;
    private ArrayList dateList;

    @Override
    public void run() {
        usernameSnapshotList = new ArrayList<DataSnapshot>(128);
        dateSnapshotList = new ArrayList<DataSnapshot>(256);
        dateList = new ArrayList<DataSnapshot>(256);
        while(true){
            firebaseDatabase = Server.getFirebaseDatabase();
            firebaseDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

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
