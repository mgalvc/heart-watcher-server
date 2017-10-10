/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.HashMap;

/**
 *
 * @author matheus
 */
public class Patient implements Comparable<Patient> {
    private int id;
    private String name;
    private boolean movement;
    private double heartRate;
    private double[] pressure;
    private boolean inRisk;
    private String time;

    public Patient(int id, String name) {
        this.id = id;
        this.name = name;
        this.movement = false;
        this.heartRate = 0;
        this.pressure = new double[] {0, 0};
        this.inRisk = false;
    }

    public void setPayload(HashMap<String, Object> payload) {
        this.setHeartRate((double) payload.get("heart_rate"));
        this.setPressure((double[]) payload.get("pressure"));
        this.setMovement((boolean) payload.get("movement"));
        this.setTime(payload.get("time").toString());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getMovement() {
        return movement;
    }

    public void setMovement(boolean movement) {
        this.movement = movement;
    }

    public double getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(double heartRate) {
        this.heartRate = heartRate;
    }

    public double[] getPressure() {
        return pressure;
    }

    public void setPressure(double[] pressure) {
        this.pressure = pressure;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private boolean isInRisk() {
        if (this.heartRate > 100 && !this.getMovement()) {
            this.inRisk = true;
            return true;
        }

        if (this.pressure[0] < 120 && this.pressure[1] < 80) {
            this.inRisk = true;
            return true;
        }

        if ((this.pressure[0] > 120 && this.pressure[1] > 80) && !this.getMovement()) {
            this.inRisk = true;
            return true;
        }

        this.inRisk = false;
        return false;
    }

    public boolean getRisk() {
        return this.isInRisk();
    }

    @Override
    public int compareTo(Patient o) {        
        if (this.isInRisk() && !o.isInRisk()) {
            return 1;
        }

        if (!this.isInRisk() && o.isInRisk()) {
            return -1;
        }

        return 0;
    }
}

