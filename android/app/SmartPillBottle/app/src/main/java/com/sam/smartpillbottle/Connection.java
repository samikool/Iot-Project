package com.sam.smartpillbottle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.zip.DataFormatException;

public class Connection implements Runnable {
    private String ip;
    private int port;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean done = false;
    private volatile boolean connected = false;


    public Connection(String ip, int port) throws IOException{
        this.ip = ip;
        this.port = port;
    }

    public Connection(String ip, String port) throws IOException {
        this(ip, Integer.valueOf(port));
    }

    public boolean isConnected(){
        return this.connected;
    }

    public void connect() {
        try{
            this.socket = new Socket(InetAddress.getByName(ip),port);
        }catch (Exception e){
            e.printStackTrace();
            System.err.println(e);
        }
    }

    public void initializeStreams(){
        try{
            //get output buffer initialized
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();

            //get input buffer initialized
            input = new ObjectInputStream(socket.getInputStream());
        }catch (IOException e){
            System.err.println("Error initializing buffers...");
            System.err.println(e);
        }
    }

    public void processConnection(){
        connected = true;
        while(!done){


        }
    }

    public void closeConnection(){
        System.out.println("Attempting to close connection with server");
        try{
            socket.close();
            input.close();
            output.close();
            System.out.println("Connection connection closed" );

        }catch (IOException e){
            System.err.println("Error closing client connection");
            System.err.println(e);
        }
    }

    public void sendData(Object data){
        try{
            output.writeObject(data);
            output.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Object receiveData() throws DataFormatException {
        try{
            return input.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new DataFormatException("Data unsuccessfully read");
    }

    //Runnable interface
    @Override
    public void run() {
        try{
            connect();
            initializeStreams();
            processConnection();
        }catch (Exception e){
            System.err.println(e);
        }
    }

    public static void main(String[] args){
        try{
            Connection connection = new Connection("172.17.52.78", 4044);
            connection.connect();
            connection.initializeStreams();
            connection.closeConnection();
        }catch (Exception e){
            System.err.println(e);
        }

    }
}
