/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matheus
 */
public class Server {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private final ArrayList<Patient> patients;
    private final ArrayList<Socket> connections;
    private AtomicInteger sensors;
    private AtomicInteger doctors;
    
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newCachedThreadPool();
        patients = new ArrayList<>();
        connections = new ArrayList<>();
        sensors = new AtomicInteger(0);
        doctors = new AtomicInteger(0);
    }
    
    public void run() {
        while(true) {
            try {
                pool.execute(new RequestHandler(serverSocket.accept()));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static HashMap<String, Object> getHashMapFrom(Patient p) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", p.getName());
        data.put("heart_rate", (double) p.getHeartRate());
        double[] pressure = p.getPressure();
        data.put("pressure",  new String[] {String.valueOf(pressure[0]), String.valueOf(pressure[1])});
        data.put("movement", (boolean) p.getMovement());
        data.put("time", p.getTime());
        data.put("in_risk", p.getRisk());
        
        return data;
    }
    
    private class RequestHandler implements Runnable {
        private final Socket socket;
        
        public RequestHandler(Socket socket) {
            this.socket = socket;
        }
        
        public void run() {
            ObjectInputStream in = null;
            ObjectOutputStream out = null;
            HashMap<String, Object> message = null;
            connections.add(socket);
            
            System.out.println(socket.getInetAddress().getHostName() + "is connected");
            
            try {
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                message = (HashMap<String, Object>) in.readObject();
                
                processMessage(message, in, out);
                
                in.close();
                out.close();
            } catch (Exception ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }     
        }
        
        private Patient findPatient(int id) {
            for(Patient patient : patients) {
                if(patient.getId() == id) {
                    return patient;
                }
            }
            
            return null;
        }
        
        private void processMessage(HashMap<String, Object> message, ObjectInputStream in, ObjectOutputStream out) throws IOException {
            HashMap<String, Object> response = new HashMap<>();
            
            if(message.get("source").equals("sensor")) {
                if(message.get("action").equals("register")) {
                    int new_id = sensors.getAndIncrement();
                    
                    patients.add(new Patient(new_id, (String) message.get("name")));
                    
                    response.put("id", new_id);
                    response.put("message", "you are registered on server");
                } else if(message.get("action").equals("send")) {
                    int id = (int) message.get("id");
                    
                    Patient patient = findPatient(id);
                    
                    if(patient != null) {
                        patient.setPayload((HashMap<String, Object>) message.get("payload"));  
                        response.put("message", "data received");
                    } else {
                        response.put("message", "patient not found");
                    }
                    
                } else if(message.get("action").equals("disconnect")) {
                    sensors.decrementAndGet();
                    patients.remove(findPatient((int) message.get("id")));
                    response.put("message", "you are out");
                }
            } else if(message.get("source").equals("doctor")) {
                if(message.get("action").equals("register")) {
                    int new_id = doctors.getAndIncrement();
                    
                    response.put("id", new_id);
                    response.put("message", "you are registered on server");
                } else if(message.get("action").equals("get_general")) {
                    ArrayList<HashMap<String, Object>> list = new ArrayList<>();
                    
                    for(Patient patient : patients) {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("id", patient.getId());
                        data.put("name", patient.getName());
                        data.put("in_risk", patient.getRisk());
                        
                        list.add(data);
                    }
                    
                    response.put("payload", list);
                } else if(message.get("action").equals("get_specifics")) {
                    HashMap<String, Object> patient = getHashMapFrom(findPatient((int) message.get("id")));
                    System.out.println(patient);
                    
                    response.put("payload", patient);                          
                } else if(message.get("action").equals("disconnect")) {
                    doctors.decrementAndGet();
                    response.put("message", "you are out");
                }
            }
            
            System.out.println("received " + message.toString());
            System.out.println("sending " + response.toString());
            
            out.writeObject(response);
        }
    }
}
