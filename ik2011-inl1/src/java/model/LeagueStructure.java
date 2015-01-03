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
    ROUND_ROBIN(1, 14),
    DOUBLE_ROUND_ROBIN(2, 7);
    private final int value;
    private final int daysBetweenMatches;
    private LeagueStructure(int value, int daysBetweenMatches) {
        this.value = value;
        this.daysBetweenMatches = daysBetweenMatches;
    }
    
    public int getValue() {
        return value;
    }

    public int getDaysBetweenMatches() {
        return daysBetweenMatches;
    }
}
