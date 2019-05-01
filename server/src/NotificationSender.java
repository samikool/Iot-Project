import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import java.util.concurrent.ExecutorService;

public class NotificationSender implements Runnable {
    private String token;
    private String title;
    private String medicine;
    private String message;

    public NotificationSender(String token, String title, String medicine,String message){
        this.token = token;
        this.title = title;
        this.medicine = medicine;
        this.message = message;
    }

    @Override
    public void run() {
        Message message = Message.builder()
                .putData("title", this.title)
                .putData("content", this.message)
                .putData("medicine", this.medicine)
                .setToken(token)
                .build();

        try{
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        }catch (FirebaseMessagingException e){
            e.printStackTrace();
        }

    }
}
