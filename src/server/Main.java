/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;

/**
 *
 * @author matheus
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Server s = new Server(Integer.valueOf(args[0]));
        s.run();
    }
}
