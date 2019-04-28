package com.sam.smartpillbottle;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.zip.DataFormatException;

public class ServerSender implements Runnable{
    private Connection connection;
    private ArrayBlockingQueue<String> dataArray = new ArrayBlockingQueue<>(128);

    public ServerSender(Connection connection){
        this.connection = connection;
    }

    public void addData(String data){
        dataArray.add(data);
    }

    @Override
    public void run() {
        while(!dataArray.isEmpty()){
            connection.sendData(dataArray.poll());
        }
    }
}