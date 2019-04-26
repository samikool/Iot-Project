package com.sam.smartpillbottle;

import java.util.zip.DataFormatException;

public class ServerSender implements Runnable{
    private Connection connection;
    private Object data;
    private volatile boolean dataReady = false;

    public ServerSender(Connection connection){
        this.connection = connection;
        dataReady = false;
    }

    public ServerSender(Connection connection, Object data){
        this.connection = connection;
        this.data = data;
        dataReady = true;
    }

    public void setData(Object data) {
        if(data != null){
            this.data = data;
            dataReady = true;
        }
        else{
            dataReady = false;
        }
    }

    @Override
    public void run() {
        if(dataReady && data != null){
            connection.sendData(data);
            dataReady = false;
        }
    }
}