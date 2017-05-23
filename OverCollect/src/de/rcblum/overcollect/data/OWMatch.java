package de.rcblum.overcollect.data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rcblum.overcollect.utils.Helper;

public class OWMatch 
{

	private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String matchId = null;
	
	private String startTime = null;
	
	private String endTime = null;
	
	private Result result = Result.VICTORY;
	
	private String teamSr = null;
	
	private String enemySr = null;

	private String sr = null;
	
	private String map = null;
	
	private int stacksize = 1;
	
	private List<OWCharacterStats> characterStats = new LinkedList<>();
	
	public OWMatch(String matchId)
	{
		this.matchId = matchId;	
	}
	
	public String getMatchId() {
		return matchId;
	}
	
	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public Date getStartTime()
	{
		try {
			return SDF.parse(this.startTime);
		} catch (ParseException | NullPointerException e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setStartTime(String startTime) 
	{
		this.startTime = startTime;
	}
	
	public Date getEndTime()
	{
		try {
			return SDF.parse(this.endTime);
		} catch (ParseException | NullPointerException e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setEndTime(String endTime) 
	{
		this.endTime = endTime;
	}
	
	public void setStacksize(int stacksize) 
	{
		this.stacksize = stacksize;
	}
	
	public int getStacksize()
	{
		return stacksize;
	}

	public boolean isVictory() {
		return result == Result.VICTORY;
	}

	public boolean isDefeat() {
		return result == Result.DEFEAT;
	}

	public boolean isDraw() {
		return result == Result.DRAW;
	}

	public void setResult(Result result) {
		this.result = result;
	}
	
	public Result getResult() {
		return result;
	}
	
	public String getTeamSr() {
		return teamSr;
	}

	public void setTeamSr(String teamSr) {
		this.teamSr = teamSr;
	}

	public String getEnemySr() {
		return enemySr;
	}

	public void setEnemySr(String enemySr) {
		this.enemySr = enemySr;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public List<OWCharacterStats> getCharacterStats() {
		return characterStats;
	}

	public void setCharacterStats(List<OWCharacterStats> characterStats) {
		this.characterStats = characterStats;
	}

	public int getSr() {
		if (Helper.isInteger(this.sr))
			return Integer.valueOf(this.sr);
		return -1;
	}

	public void setSr(String sr) {
		this.sr = sr;
	}

	public void addCharacterStats(OWCharacterStats cStats)
	{
		this.characterStats.add(cStats);
	}

	public String toJson() 
	{
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		return g.toJson(this);
	}
	
	public static OWMatch fromJsonFile(File jFile) 
	{
		OWMatch match = null;
		Gson g = new Gson();
		try {
			String text = new String(Files.readAllBytes(jFile.toPath()), StandardCharsets.UTF_8);
			match = g.fromJson(text, OWMatch.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return match;
	}
	
	public static enum Result {
		VICTORY, DEFEAT, DRAW;
	}
}
