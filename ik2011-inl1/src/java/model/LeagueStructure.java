/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Jeff
 */
public enum LeagueStructure {
    ROUND_ROBIN(1),
    DOUBLE_ROUND_ROBIN(2);
    private final int value;
    private LeagueStructure(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
