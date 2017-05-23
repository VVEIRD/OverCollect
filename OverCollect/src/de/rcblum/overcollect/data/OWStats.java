package de.rcblum.overcollect.data;

public class OWStats
{
	protected int eliminations = -1;

	protected int objectiveKills = -1;

	protected String objectiveTime = null;
	
	protected int damageDone = -1;
	
	protected int healingDone = -1;

	protected int deaths = -1;


	public int getEliminations()
	{
		return eliminations;
	}

	public void setEliminations(int eliminations) {
		this.eliminations = eliminations;
	}
	
	public int getDeaths() 
	{
		return deaths;
	}

	public int getObjectiveKills()
	{
		return objectiveKills;
	}

	public void setObjectiveKills(int objectiveKills)
	{
		this.objectiveKills = objectiveKills;
	}

	public String getObjectiveTime() 
	{
		return objectiveTime;
	}

	public void setObjectiveTime(String objectiveTime)
	{
		this.objectiveTime = objectiveTime.length() == 4 ? objectiveTime.substring(0, 2) + ":" + objectiveTime.substring(2, 4) : objectiveTime;
	}

	public int getDamageDone() 
	{
		return damageDone;
	}

	public void setDamageDone(int damageDone) 
	{
		this.damageDone = damageDone;
	}

	public int getHealingDone() 
	{
		return healingDone;
	}

	public void setHealingDone(int healingDone)
	{
		this.healingDone = healingDone;
	}

	public void setDeaths(int deaths)
	{
		this.deaths = deaths;
	}
	
}
