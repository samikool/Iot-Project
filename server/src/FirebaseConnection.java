import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseConnection {
    private FirebaseDatabase firebaseDatabase;
    private volatile Object data;

     public FirebaseConnection(){
           firebaseDatabase = FirebaseDatabase.getInstance();
     }

     public void sendData(String reference, Object data){
         firebaseDatabase.getReference(reference).setValue(data, null);
     }

     public Object getData(String reference, String child){
        data = null;
        firebaseDatabase.getReference(reference + "/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.child(child).getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        while (data == null){

        }

        return data;
    }
}
