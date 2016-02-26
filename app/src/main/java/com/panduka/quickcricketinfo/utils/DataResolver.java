package com.panduka.quickcricketinfo.utils;

import com.panduka.quickcricketinfo.datastructure.CricketMatch;
import com.panduka.quickcricketinfo.datastructure.Team1;
import com.panduka.quickcricketinfo.datastructure.Team2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pandukadesilva on  2/22/16.
 */
public class DataResolver {

    private static final String MATCH_NOT_INIT = "Match Not Initialized";

    private List<CricketMatch> mCricketMatchList;
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
