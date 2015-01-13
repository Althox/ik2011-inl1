/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.upload;

import utils.Numbers;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import model.League;
import model.LeagueStructure;
import model.Match;
import model.Round;
import model.Team;
import static utils.Numbers.getRandomInt;

/**
 *
 * @author Jeff
 */
public abstract class MatchupGenerator {

    public static ArrayList<Match> generateSeasonMatchups(League league, LeagueStructure structure) throws Exception {

        ArrayList<Round> rounds = getRounds(league.getTeams());
        
        generateDates(rounds, Integer.parseInt(league.getSeason()), structure);
        ArrayList matches = new ArrayList();

        for (Round round : rounds) {
            matches.addAll(round.getMatches());
        }

        return matches;
    }

    public static void generateDates(ArrayList<Round> rounds, int seasonYear, LeagueStructure leagueStructure) {
        Date currentDate = getLeagueStartDate(seasonYear);
        for (Round round : rounds) {
            round.setDate(currentDate);
            currentDate = addDaysToDate(currentDate, leagueStructure.getDaysBetweenMatches());
        }
    }

     

    private static Match createMatchup(Team home, Team away) {
        Match match = new Match();

        match.setHome(home);
        match.setAway(away);
        return match;
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
    
    public static Date addDaysToDate(final Date date, int noOfDays) {
        Date newDate = new Date(date.getTime());

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(newDate);
        calendar.add(Calendar.DATE, noOfDays);
        calendar.set(Calendar.HOUR_OF_DAY, Numbers.getRandomInt(15, 19));
        newDate.setTime(calendar.getTime().getTime());

        return newDate;
    }

    public static void generatMatchResults(ArrayList<Match> matches) {
        Iterator<Match> it = matches.iterator();
        Date today = new Date();
        while (it.hasNext()) {
            Match match = it.next();
            if (match.getDate().before(today)) {
                MatchupGenerator.generateRandomScores(match);
            } else {
                it.remove(); // Vi vill inte ha match-resultat för matcher som inte har passerat
            }
        }
    }

    public static void generateRandomScores(Match match) {
        int scoreHome = getRandomInt(0, 9);
        int scoreAway = getRandomInt(0, 9);

        match.setHomeScore(scoreHome);
        match.setAwayScore(scoreAway);
    }

    public static ArrayList<Round> getRounds(ArrayList<Team> originalTeamList) {
        Collections.shuffle(originalTeamList);
        
        final String BYE_TEAM_NAME = "<Bye>";
        
        boolean addedByeTeam = false;
        if (originalTeamList.size() % 2 != 0) {
            Team byeTeam = new Team(BYE_TEAM_NAME);
            originalTeamList.add(0, byeTeam);
            addedByeTeam = true;
        }

        int numTeams = originalTeamList.size();
        int numRounds = (numTeams - 1);
        int halfSize = numTeams / 2;
        ArrayList<Team> teams = new ArrayList();

        teams.addAll(originalTeamList);
        teams.remove(0); // Tar bort vårt statiska lag, detta för att det inte ska kunna möta sig självt.

        int teamsSize = teams.size();
        ArrayList<Round> rounds = new ArrayList();

        for (int day = 0; day < numRounds; day++) {
            Round round = new Round();

            int teamIdx = day % teamsSize;

            if (!addedByeTeam) { // Bye kommer alltid vara vårt statiska lag om vi har det med, därför kan vi ignorera att lägga till denna match,
                Match matchup = new Match();
                matchup.setHome(teams.get(teamIdx));
                matchup.setAway(originalTeamList.get(0)); // Vårt statiska lag får alltid en match först.
                round.addMatch(matchup);
            }

            for (int idx = 1; idx < halfSize; idx++) {

                int homeTeamIndez = (day + idx) % teamsSize;
                int awayTeamIndex = (day + teamsSize - idx) % teamsSize;
                Team homeTeam = teams.get(homeTeamIndez);
                Team awayTeam = teams.get(awayTeamIndex);

                round.addMatch(createMatchup(homeTeam, awayTeam));
            }
            rounds.add(round);
        }
        
        if (originalTeamList.get(0).getName().equals(BYE_TEAM_NAME)) {
            originalTeamList.remove(0);
        }
        
        return rounds;
    }
}
