/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Random;

/**
 *
 * @author Jeff
 */
public class Numbers {
    public static int getRandomInt(int min, int max) {
        
        Random random = new Random();
        
        int randomNum = random.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
