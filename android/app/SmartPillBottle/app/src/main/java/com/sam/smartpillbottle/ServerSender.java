package com.sam.smartpillbottle;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.zip.DataFormatException;

public class ServerSender implements Runnable{
    private Connection connection;
    private ArrayBlockingQueue<Object> dataArray = new ArrayBlockingQueue<>(128);

    public ServerSender(Connection connection){
        this.connection = connection;
    }

    public void addData(Object data){
        dataArray.add(data);
    }

    @Override
    public void run() {
        while(!dataArray.isEmpty()){
            connection.sendData(dataArray.poll());
        }
    }
}