package com.blackout.aow.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.blackout.aow.core.AowPlayer;

public class Board {
	 
	private final Scoreboard scoreboard;
	private final Objective objective;
	private Team team;
 
	public Board(Player player, String prefix, String suffix) {
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.objective = scoreboard.registerNewObjective(player.getName(), "dummy");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.objective.setDisplayName(player.getDisplayName());
		player.setScoreboard(this.scoreboard);
		if (this.scoreboard.getTeam(player.getName()) == null) {
			this.scoreboard.registerNewTeam(player.getName());
		}
		team = this.scoreboard.getTeam(player.getName());
		team.setPrefix(prefix);
		team.setSuffix(suffix);
		team.setNameTagVisibility(NameTagVisibility.ALWAYS);
		team.addEntry(player.getName());
	}
 
	public void addTeam(AowPlayer newPlayer, boolean reset) {
		if (scoreboard.getTeam(newPlayer.getPlayer().getName()) == null) {
			scoreboard.registerNewTeam(newPlayer.getPlayer().getName());
		}
		
		team = scoreboard.getTeam(newPlayer.getPlayer().getName());
		if (reset) {
			team.setPrefix("");
		} else {
			team.setPrefix(newPlayer.getPlayerID() == 0 ? "�9" : "�4");
		}
		team.setNameTagVisibility(NameTagVisibility.ALWAYS);
		team.addEntry(newPlayer.getPlayer().getName());
	}
	
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
 
	public String getTitle() {
		return objective.getDisplayName();
	}
 
	public void setTitle(String name) {
		this.objective.setDisplayName(name);
	}
 
	public void set(int row, String text) {
		if(text.length() > 32) { text = text.substring(0, 32); }
		for(String entry : this.scoreboard.getEntries()) {
			if(this.objective.getScore(entry).getScore() == row) {
				this.scoreboard.resetScores(entry);
				break;
			}
		}
		this.objective.getScore(text).setScore(row);
	}
	
	public String get(int row) {
		for(String entry : this.scoreboard.getEntries()) {
			if(this.objective.getScore(entry).getScore() == row) {
				return (entry);
			}
		}
		return (null);
	}
 
	public void remove(int row) {
		for(String entry : this.scoreboard.getEntries()) {
			if(this.objective.getScore(entry).getScore() == row) {
				this.scoreboard.resetScores(entry);
				break;
			}
		}
	}
}