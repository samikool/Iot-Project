package com.sam.smartpillbottle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.concurrent.ExecutorService;

public class Client implements Runnable{
    private String ip;
    private int port;
    private Socket socket;
    private ObjectOutputStream outputBuffer;
    private ObjectInputStream inputBuffer;

    public Client(String ip, int port) throws IOException{
        this.ip = ip;
        this.port = port;

    }

    public Client(String ip, String port) throws IOException {
        this(ip, Integer.valueOf(port));
    }

    public void connect() {
        try{
            this.socket = new Socket(InetAddress.getByName(ip),port);
            System.out.println(socket.isConnected());
        }catch (Exception e){
            e.printStackTrace();
            System.err.println(e);
        }
    }

    public void initializeStreams(){
        try{
            //get output buffer initialized
            outputBuffer = new ObjectOutputStream(socket.getOutputStream());
            outputBuffer.flush();

            //get input buffer initialized
            inputBuffer = new ObjectInputStream(socket.getInputStream());

            System.out.println("Buffers successfully initialized");
        }catch (IOException e){
            System.err.println("Error initializing buffers...");
            System.err.println(e);
        }
    }

    public void stayConnected(){
        try{
            if(inputBuffer.readInt() == 1){
                closeConnection();
            }
        }catch (IOException e){
            System.err.println("Unable to read data...");
            System.err.println("Closing connection...");
            closeConnection();
        }
    }

    public void closeConnection(){
        System.out.println("Attempting to close connection with server");
        try{
            socket.close();
            inputBuffer.close();
            outputBuffer.close();
            System.out.println("Client connection closed" );

        }catch (IOException e){
            System.err.println("Error closing client connection");
            System.err.println(e);
        }

    }

    @Override
    public void run() {
        try{
            connect();
            initializeStreams();
            stayConnected();
        }catch (Exception e){
            System.err.println(e);
        }

        initializeStreams();
        stayConnected();
    }

    public static void main(String[] args){
        try{
            Client client = new Client("68.183.148.234", 4044);
            client.connect();
            client.initializeStreams();
            client.closeConnection();
        }catch (Exception e){
            System.err.println(e);
        }

    }
}
