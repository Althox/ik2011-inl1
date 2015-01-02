/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.upload;

import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import model.League;
import model.LeagueStructure;
import model.Match;
import model.Round;
import model.Team;

/**
 *
 * @author Jeff
 */
public abstract class MatchupGenerator {
    public static ArrayList<Match> generateSeasonMatchups(League league, LeagueStructure structure) {
        ArrayList<Match> matches = new ArrayList();
        ArrayList<Team> teams = league.getTeams();
        Collections.shuffle(teams);
        
        for (Team t : teams) {

            int currTeamIndex = teams.indexOf(t);

            boolean alternateHome = true; // Ett lag ska spela ungefär hälften av sina matcher hemma.
            for (int i = 0; i < teams.size(); i++) {
                if (i != currTeamIndex) { // Vi vill aldrig att ett lag ska kunna möta sig själva.
                    Match match;
                    if (alternateHome) {
                        match = createMatchup(t, teams.get(i));
                    } else {
                        match = createMatchup(teams.get(i), t);
                    }

                    if (!containsMatchup(matches, match, structure)) {
                        matches.add(match);
                        if (structure == LeagueStructure.DOUBLE_ROUND_ROBIN) { // reverse the matchup.
                            Match matchTwo;
                            if (alternateHome) {
                                matchTwo = createMatchup(teams.get(i), t);
                            } else {
                                matchTwo = createMatchup(t, teams.get(i));
                            }
                            
                            matches.add(matchTwo);
                        }
                    } else
                        continue;

                    alternateHome = !alternateHome;

                }
            }
        }
        matches = generateMatchupDates(matches, teams.size(), league.getStartDate());
        return matches;
    }
    
    private static Match createMatchup(Team home, Team away) {
        Match match = new Match();

        match.setHome(home);
        match.setAway(away);
        return match;
    }

    private static boolean containsMatchup(ArrayList<Match> matches, Match matchToTest, LeagueStructure structure) {
        
        if (structure == LeagueStructure.DOUBLE_ROUND_ROBIN) {
            for (Match match : matches) {
                if (match.equalsIgnoreReversed(matchToTest)) {
                    return true;
                }
            }
        } else {
            for (Match match : matches) {
                if (match.equals(matchToTest)) {
                    return true;
                }
            }
        }

        return false;
    }
    
    private static ArrayList<Match> generateMatchupDates(ArrayList<Match> matches, int numOfTeams, Date startDate) {
        
        int matchesPerRound, numOfRounds;
        
        // För en liga med udda mängd lag behövs en extra runda.
        if (numOfTeams % 2 == 0) {
            numOfRounds = numOfTeams-1;
        } else {
            numOfRounds = numOfTeams;
        }
        
        matchesPerRound = matches.size() / numOfRounds;
        
        ArrayList<Round> rounds = new ArrayList();
        
        Date currentDate = startDate;
        
        while (!matches.isEmpty()) {
            Round round = new Round(matchesPerRound);
            
            Iterator<Match> it = matches.iterator();
            while (it.hasNext()) {
                Match match = it.next();
                
                if (!round.containsTeam(match.getAway()) || !round.containsTeam(match.getHome())) {
                    try {
                        round.addMatch(match);
                        it.remove();
                    } catch (Round.RoundFullException rfe) {
                        break;
                    }
                }
            }
            round.setDate(currentDate);
            rounds.add(round);
            currentDate = addDaysToDate(currentDate, 14);
        }
        
        for (Round round : rounds) {
            matches.addAll(round.getMatches());
        }
        
        return matches;
    }
    
    private static ArrayList<Match> getMatches(ArrayList<Match> matches, Team team) {
        ArrayList<Match> foundMatches = new ArrayList();
        for (Match match : matches) {
            if (match.containsTeam(team))
                foundMatches.add(match);
        }
        
        return foundMatches;
    }
    
    public static Date addDaysToDate(final Date date, int noOfDays) {
        Date newDate = new Date(date.getTime());
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(newDate);
        calendar.add(Calendar.DATE, noOfDays);
        newDate.setTime(calendar.getTime().getTime());

        return newDate;
    }
}
