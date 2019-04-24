package com.sam.smartpillbottle;

import java.util.zip.DataFormatException;

public class ServerRequester implements Runnable{
    private Connection connection;
    private Object data;
    private volatile boolean dataReady;

    public ServerRequester(Connection connection){
        this.connection = connection;
        this.data = null;
        dataReady = false;
    }

    public Object getData() {
        while(!dataReady){
            try{
                wait();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        dataReady = false;
        return data;
    }

    @Override
    public void run() {
        try{
            this.data = connection.receiveData();
            dataReady = true;
            
        }catch (DataFormatException e){
            e.printStackTrace();
        }
    }
}