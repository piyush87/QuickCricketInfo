package com.panduka.quickcricketinfo.utils;

import com.panduka.quickcricketinfo.datastructure.Batting;
import com.panduka.quickcricketinfo.datastructure.Bowling;
import com.panduka.quickcricketinfo.datastructure.CricketMatch;
import com.panduka.quickcricketinfo.datastructure.Dnb;
import com.panduka.quickcricketinfo.datastructure.Extra;
import com.panduka.quickcricketinfo.datastructure.Fow;
import com.panduka.quickcricketinfo.datastructure.Inning;
import com.panduka.quickcricketinfo.datastructure.InningSummery;
import com.panduka.quickcricketinfo.datastructure.MatchDetails;
import com.panduka.quickcricketinfo.datastructure.MatchSummery;
import com.panduka.quickcricketinfo.datastructure.Player;
import com.panduka.quickcricketinfo.datastructure.Team1;
import com.panduka.quickcricketinfo.datastructure.Team2;
import com.panduka.quickcricketinfo.datastructure.Total;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pandukadesilva on  2/22/16.
 */
public class DataResolver {

    private static final String MATCH_NOT_INIT = "Match Not Initialized";

    private List<CricketMatch> mCricketMatchList;
    private List<MatchDetails> mMatchDetailsList;
    private static volatile DataResolver instance = null;

    private DataResolver() {
    }

    // singleton
    public static synchronized DataResolver getInstance() {
        if (instance == null) {
            instance = new DataResolver();
        }
        return instance;
    }

    /**
     * packing up match object .
     *
     * @param json JSON String object
     */
    public void initialize(String json) throws JSONException {
        JSONObject items = new JSONObject(json);

        JSONArray jsonArray = items.getJSONArray("items");
        mCricketMatchList = new ArrayList<>();
        int j=0;
        for (int i = 0; i < jsonArray.length(); i++) {
            mCricketMatchList.add(createCricketMatch(jsonArray.getJSONObject(i)));
            if((mCricketMatchList.get(j).team1.teamName!=null && !mCricketMatchList.get(j).team1.teamName.isEmpty()) &&(mCricketMatchList.get(j).team2.teamName!=null && !mCricketMatchList.get(j).team2.teamName.isEmpty())){
                j++;
            }else{
                mCricketMatchList.remove(j);
            }
        }

        mMatchDetailsList =  new ArrayList<>();
    }

    /**
     * packing up match details object .
     *
     * @param json JSON String object
     * @param matchId int matchid
     */
    public void addMatchDetails(String json, int matchId, int position) throws JSONException {
        JSONObject matchDetailObject = new JSONObject(json);
        mMatchDetailsList.add(position,createMatchDetails(matchDetailObject, matchId));
        mCricketMatchList.get(position).matchDetails=mMatchDetailsList.get(position);

    }

    //extract data from JSON Object and pack to a Match object
    private CricketMatch createCricketMatch(JSONObject jsonObject) throws JSONException {
        CricketMatch cricMatch = new CricketMatch();

        cricMatch.title = jsonObject.getString("title").replace("&amp;","&");
        cricMatch.matchDescription = jsonObject.getString("matchDescription").replace("&amp;","&");
        cricMatch.matchId = jsonObject.getInt("matchId");

        cricMatch.team1 = createTeam1(jsonObject.getJSONObject("team1"));
        cricMatch.team2 = createTeam2(jsonObject.getJSONObject("team2"));

        return cricMatch;
    }

    private Team1 createTeam1(JSONObject jsonObject) throws JSONException {
        Team1 team1Match = new Team1();

        team1Match.teamName = jsonObject.getString("teamName");
        if (jsonObject.has("score")) {
            team1Match.score = jsonObject.getString("score");//service is inconsistant
        }
        if (jsonObject.has("score1")) {
            team1Match.score1 = jsonObject.getString("score1");//service is inconsistant
        }

        return team1Match;
    }

    private Team2 createTeam2(JSONObject jsonObject) throws JSONException {
        Team2 team2Match = new Team2();

        team2Match.teamName = jsonObject.getString("teamName");
        if (jsonObject.has("score")) {
            team2Match.score = jsonObject.getString("score");//service is inconsistant
        }
        if (jsonObject.has("score1")) {
            team2Match.score1 = jsonObject.getString("score1");//service is inconsistant
        }

        return team2Match;
    }

    private MatchDetails createMatchDetails(JSONObject jsonObject, int matchId) throws JSONException {
        MatchDetails matchDetails = new MatchDetails();

        matchDetails.matchid = matchId;
        matchDetails.matchSummery = createMatchSummery(jsonObject.getJSONObject("summary"));
        matchDetails.innings1 = createMatchInnings(jsonObject.getJSONObject("innings1"));
        matchDetails.innings1 = createMatchInnings(jsonObject.getJSONObject("innings2"));

        return matchDetails;
    }

    private MatchSummery createMatchSummery(JSONObject jsonObject) throws JSONException {
        MatchSummery matchSummery = new MatchSummery();

        matchSummery.ground=jsonObject.getString("ground");
        matchSummery.info=jsonObject.getString("info");
        matchSummery.matchStatus=jsonObject.getString("matchStatus");
        matchSummery.team1=jsonObject.getString("team1");
        matchSummery.team2=jsonObject.getString("team2");
        matchSummery.tournament=jsonObject.getString("tournament");

        return matchSummery;
    }

    private Inning createMatchInnings(JSONObject jsonObject) throws JSONException {
        Inning inning = new Inning();

        inning.battingDeailsList=createBattingDeatilList(jsonObject.getJSONArray("batting"));
        inning.bowlingDeailsList=createBowlingDeatilList(jsonObject.getJSONArray("bowling"));
        inning.dnbList=createDnbList(jsonObject.getJSONArray("dnb"));
        inning.fowList=createFowList(jsonObject.getJSONArray("fow"));
        inning.inningSummery=createInningSummery(jsonObject.getJSONObject("summary"));

        return inning;
    }

    private List<Batting> createBattingDeatilList(JSONArray jsonArray) throws JSONException {
        List<Batting> battingList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            Batting battingObj = new Batting();
            battingObj.balls=jsonArray.getJSONObject(i).getString("balls");
            battingObj.fours=jsonArray.getJSONObject(i).getString("fours");
            battingObj.minutes=jsonArray.getJSONObject(i).getString("minutes");

            Player player = new Player();
            player.playerId=jsonArray.getJSONObject(i).getJSONObject("player").getString("playerId");
            player.playerName=jsonArray.getJSONObject(i).getJSONObject("player").getString("playerName");

            battingObj.player=player;
            battingObj.runs=jsonArray.getJSONObject(i).getString("runs");
            battingObj.sixes=jsonArray.getJSONObject(i).getString("sixes");
            battingObj.status=jsonArray.getJSONObject(i).getString("status");
            battingObj.strikeRate=jsonArray.getJSONObject(i).getString("strikeRate");

            battingList.add(battingObj);
        }

        return battingList;
    }

    private List<Bowling> createBowlingDeatilList(JSONArray jsonArray) throws JSONException {
        List<Bowling> bowlingList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            Bowling bowlingObj = new Bowling();

            bowlingObj.dots=jsonArray.getJSONObject(i).getString("dots");
            bowlingObj.economy=jsonArray.getJSONObject(i).getString("economy");
            bowlingObj.extras=jsonArray.getJSONObject(i).getString("extras");
            bowlingObj.fours=jsonArray.getJSONObject(i).getString("fours");
            bowlingObj.maidens=jsonArray.getJSONObject(i).getString("maidens");
            bowlingObj.overs=jsonArray.getJSONObject(i).getString("overs");
            bowlingObj.runs=jsonArray.getJSONObject(i).getString("runs");
            bowlingObj.sixes=jsonArray.getJSONObject(i).getString("sixes");
            bowlingObj.wickets=jsonArray.getJSONObject(i).getString("wickets");

            Player player = new Player();
            player.playerId=jsonArray.getJSONObject(i).getJSONObject("player").getString("playerId");
            player.playerName=jsonArray.getJSONObject(i).getJSONObject("player").getString("playerName");

            bowlingObj.player = player;

            bowlingList.add(bowlingObj);
        }

        return bowlingList;
    }

    private List<Dnb> createDnbList(JSONArray jsonArray) throws JSONException {
        List<Dnb> dnbList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            Dnb dnbObj = new Dnb();


            Player player = new Player();
            player.playerId=jsonArray.getJSONObject(i).getString("playerId");
            player.playerName=jsonArray.getJSONObject(i).getString("playerName");

            dnbObj.player = player;

            dnbList.add(dnbObj);
        }

        return dnbList;
    }

    private List<Fow> createFowList(JSONArray jsonArray) throws JSONException {
        List<Fow> fowList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            Fow fowObj = new Fow();

            Player player = new Player();
            player.playerName=jsonArray.getJSONObject(i).getString("player");

            fowObj.player = player;
            fowObj.over=jsonArray.getJSONObject(i).getString("over");
            fowObj.score=jsonArray.getJSONObject(i).getString("score");

            fowList.add(fowObj);
        }

        return fowList;
    }

    private InningSummery createInningSummery(JSONObject jsonObject) throws JSONException {

        InningSummery inningSummeryObj = new InningSummery();

        Extra extra = new Extra();
        extra.total = jsonObject.getJSONObject("extra").getString("total");
        extra.details = jsonObject.getJSONObject("extra").getString("details");

        Total total = new Total();
        total.overs = jsonObject.getJSONObject("total").getString("overs");
        total.score = jsonObject.getJSONObject("total").getString("score");
        total.wickets = jsonObject.getJSONObject("total").getString("wickets");

        inningSummeryObj.extra=extra;
        inningSummeryObj.total=total;

        return inningSummeryObj;
    }

    //getters
    public List<CricketMatch> getCricketMatchList() {
        if (mCricketMatchList == null) {
            throw new IllegalStateException(MATCH_NOT_INIT);
        }
        return mCricketMatchList;
    }

    public Team1 getMatchTeam1(int index) {
        if (mCricketMatchList == null) {
            throw new IllegalStateException(MATCH_NOT_INIT);
        }
        return mCricketMatchList.get(index).team1;
    }

    public Team2 getMatchTeam2(int index) {
        if (mCricketMatchList == null) {
            throw new IllegalStateException(MATCH_NOT_INIT);
        }
        return mCricketMatchList.get(index).team2;
    }

    public boolean isDataLoaded() {
        return mCricketMatchList != null;
    }

}
