/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.upload;

import Util.Numbers;
import static Util.Numbers.getRandomInt;
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
        generateMatchupDates(matches, teams.size(), Integer.parseInt(league.getSeason()), structure);
        
        return matches;
    }
    
    private static Match createMatchup(Team home, Team away) {
        Match match = new Match();

        match.setHome(home);
        match.setAway(away);
        return match;
    }
    
    private static boolean containsMatchup(ArrayList<Match> matches, Match matchToTest, LeagueStructure structure) {
        
        for (Match match : matches) {
            if (structure == LeagueStructure.DOUBLE_ROUND_ROBIN) {
                if (match.equalsIgnoreReversed(matchToTest))
                    return true;
            } else {
                if (match.equals(matchToTest))
                    return true;
            }
        }

        return false;
    }
    
    /**
     * Genererar speldatum för alla matcher.
     * 
     * Notera att tiden mellan varje runda bestäms av vilken turneringsstruktur som används.
     * Används LeagueStructure.ROUND_ROBIN kommer speltiderna spridas ut över längre perioder, två veckor mellan varje runda
     * 
     * Används LeaguseStructure.DOUBLE_ROUND_ROBIN kommer speltiderna att spridas ut lite mer tätt inpå, 1 vecka mellan varje runda.
     * 
     * @param matches
     * @param numOfTeams
     * @param seasonYear
     * @param leagueStructure 
     */
    private static void generateMatchupDates(ArrayList<Match> matches, int numOfTeams, int seasonYear, LeagueStructure leagueStructure) {
        
        int matchesPerRound, numOfRounds;
        
        // För en liga med udda mängd lag behövs en extra runda.
        if (numOfTeams % 2 == 0) {
            numOfRounds = numOfTeams-1;
        } else {
            numOfRounds = numOfTeams;
        }
        
        matchesPerRound = matches.size() / numOfRounds;
        ArrayList<Round> rounds = new ArrayList();
        Date currentDate = getLeagueStartDate(seasonYear);
        
        // När en match tilldelats en runda kommer matchen att tas bort, därmed kommer loopen sluta när alla matcher tilldelats en runda.
        while (!matches.isEmpty()) {
            Round round = new Round(matchesPerRound);
            
            // Vi kan inte manipulera listor i en for-each loop, därför använder vi en iterator och tillhörande while-loop istället.
            Iterator<Match> it = matches.iterator();
            while (it.hasNext()) {
                Match match = it.next();
                
                if ((!round.containsTeam(match.getAway()) && !round.containsTeam(match.getHome())) && !inPreviousRounds(rounds, match)) {
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
            
            currentDate = addDaysToDate(currentDate, leagueStructure.getDaysBetweenMatches());
        }
        
        for (Round round : rounds) {
            matches.addAll(round.getMatches());
        }
    }
    
    /**
     * Kollar om ett lag redan spelat en match i en föregående runda.
     *
     * -1 eller -2 rundor tillbaka kommer kollas, baserat på följande regler:
     * 
     * > I turneringar med ett jämt antal lag kommet lag att få finnas varannan runda.
     * > I turneringar med ett ojämnt antal lag kommer ett lag att få finnas en gång var tredje runda.
     * 
     * @param rounds
     * @param match
     * @return 
     */
    private static boolean inPreviousRounds(ArrayList<Round> rounds, Match match) {
        if (rounds.size() < 2) // Ingen poäng att kolla om storleken är 0 eller 1, vi vet att ingen tidigare runda finns i båda fallen.
            return false;
        
        // Kollar föregående runda.
        boolean found = rounds.get(rounds.size()-1).containsTeam(match.getAway()) &&  rounds.get(rounds.size() -1).containsTeam(match.getHome());
        
        
        if (!found && rounds.size() > 1) {
            // I turneringar med jämnt antal lag får varje lag spela minst en match varannan runda.
            // I turneringar med ojämnt antal lag får varje lag spela minst en match var tredje runda.
            if (rounds.get(0).getTotalMatchups() % 2 != 0)
                found = rounds.get(rounds.size()-2).containsTeam(match.getAway()) && rounds.get(rounds.size() -2).containsTeam(match.getHome());
        }
        
        return found;
    }
    
    private static Date getLeagueStartDate(int year) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, 43);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cal.set(Calendar.HOUR_OF_DAY, Numbers.getRandomInt(15, 19));
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
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
        calendar.set(Calendar.HOUR_OF_DAY, Numbers.getRandomInt(15, 19));
        newDate.setTime(calendar.getTime().getTime());

        return newDate;
    }
    
    public static void generateRandomScores(Match match) {
        int scoreHome = getRandomInt(0, 9);
        int scoreAway = getRandomInt(0, 9);
        
        match.setHomeScore(scoreHome);
        match.setAwayScore(scoreAway);
    }
}
