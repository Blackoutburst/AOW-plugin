package com.blackout.aow.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.blackout.aow.main.Main;
import com.blackout.aow.nms.NMSParticle;
import com.blackout.aow.nms.NMSTitle;
import com.blackout.aow.npc.ShopNPCManager;
import com.blackout.aow.utils.Board;
import com.blackout.aow.utils.ScoreboardManager;
import com.blackout.aow.utils.Utils;
import com.blackout.aow.warrior.WarriorLogical;
import com.blackout.aow.warrior.WarriorManager;
import com.blackout.holoapi.core.Holo;
import com.blackout.holoapi.utils.HoloManager;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;

import net.minecraft.server.v1_8_R3.EnumParticle;

public class Core {

	public static Location spawn = null;
	public static Location spawnSpec = null;
	public static Location spawnP1 = null;
	public static Location spawnP2 = null;
	
	public static AowPlayer player1 = null;
	public static AowPlayer player2 = null;
	
	public static Base blueBase = null;
	public static Base redBase = null;
	
	public static Song song;
	public static RadioSongPlayer rsp;
	
	public static int gameTime = 0;
	public static boolean gameRunning = false;
	
	public static List<AowPlayer> aowplayers = new ArrayList<AowPlayer>();
	
	public static List<WarriorLogical> blueWarrior = new ArrayList<WarriorLogical>();
	public static List<WarriorLogical> redWarrior = new ArrayList<WarriorLogical>();
	
	public static void update() {
		new BukkitRunnable(){
			@Override
			public void run(){
				if (Core.gameRunning) {
					Core.gameTime++;
					
					if (player1.gold > player2.gold)
						player2.gold += 5 * (player1.getAge().ordinal() + 1);
					else if (player1.gold < player2.gold)
						player1.gold += 5 * (player1.getAge().ordinal() + 1);
					
					if (player1.ultimeDelay > 0)
						player1.ultimeDelay--;
					
					if (player2.ultimeDelay > 0)
						player2.ultimeDelay--;
						
					Utils.giveItems(player1);
					Utils.giveItems(player2);
					
					for (AowPlayer p : Core.aowplayers) {
						ScoreboardManager.update(p);
					}
				}
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 0L, 20L);
	}
	
	public static void loadLocations() {
		spawn = new Location(Bukkit.getWorld("world"), 958.5f, 56, 1326.5f, -90, 0);
		spawnP1 = new Location(Bukkit.getWorld("world"), 970.5f, 55, 1311.5f, -90, 0);
		spawnP2 = new Location(Bukkit.getWorld("world"), 970.5f, 55, 1341.5f, -90, 0);
		spawnSpec = new Location(Bukkit.getWorld("world"), 992.5f, 55, 1326.5f, 90, 0);
	}
	
	private static void setPlayers() {
		aowplayers.add(player1);
		aowplayers.add(player2);
		blueBase = new Base(player1, 5000, new Location(Bukkit.getWorld("world"), 983.5f, 54, 1307.5f, 0, 0));
		redBase = new Base(player2, 5000, new Location(Bukkit.getWorld("world"), 983.5f, 54, 1345.5f, 180, 0));
		
		rsp = new RadioSongPlayer(song);
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			rsp.addPlayer(p);
			if (p != player1.getPlayer() && p != player2.getPlayer()) {
				Board b = new Board(p, "", "");
				AowPlayer spec = new AowPlayer(-1, p, b);
				p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0, false, false));
				p.setDisplayName("§7§o"+p.getName());
				p.setPlayerListName("§7§o"+p.getName());
				p.teleport(spawnSpec);
				spec.getBoard().addTeam(player1, false);
				spec.getBoard().addTeam(player2, false);
				aowplayers.add(spec);
			}
		}
		rsp.setRepeatMode(RepeatMode.ONE);
		rsp.setPlaying(true);
		player1.getPlayer().teleport(spawnP1);
		player2.getPlayer().teleport(spawnP2);
		player1.getBoard().addTeam(player2, false);
		player2.getBoard().addTeam(player1, false);
		player1.getPlayer().setDisplayName("§9"+player1.getPlayer().getName());
		player2.getPlayer().setDisplayName("§4"+player2.getPlayer().getName());
		player1.getPlayer().setPlayerListName("§9"+player1.getPlayer().getName());
		player2.getPlayer().setPlayerListName("§4"+player2.getPlayer().getName());
	}
	
	private static void resetNameColor() {
		for (AowPlayer p : aowplayers) {
			p.getBoard().addTeam(player1, true);
			p.getBoard().addTeam(player2, true);
			p.getPlayer().setDisplayName(p.getPlayer().getName());
			p.getPlayer().setPlayerListName(p.getPlayer().getName());
		}
	}
	
	private static void setBaseLife() {
		for (AowPlayer p : aowplayers) {
			Holo blueLife = new Holo(UUID.randomUUID(), "§7[§a████████████████████§7]")
			        .setLocation(new Location(Bukkit.getWorld("world"), 977.5f, 55, 1307.5f));
			HoloManager.spawnHolo(blueLife, p.getPlayer());
			Holo redLife = new Holo(UUID.randomUUID(), "§7[§a████████████████████§7]")
			        .setLocation(new Location(Bukkit.getWorld("world"), 977.5f, 55, 1345.5f));
			HoloManager.spawnHolo(redLife, p.getPlayer());
			p.setBlueBaseLife(blueLife);
			p.setRedBaseLife(redLife);
			
			String blue = "§9Blue base health points";
			String red = "§4Red base health points";
			
			if (p == Core.player1) {
				blue = "§9Your base health points";
				red = "§4Opponent base health points";
			}
			
			if (p == Core.player2) {
				blue = "§9Opponent base health points";
				red = "§4Your base health points";
			}
			
			Holo blueName = new Holo(UUID.randomUUID(), blue)
			        .setLocation(new Location(Bukkit.getWorld("world"), 977.5f, 55.3f, 1307.5f));
			HoloManager.spawnHolo(blueName, p.getPlayer());
			Holo redName = new Holo(UUID.randomUUID(), red)
			        .setLocation(new Location(Bukkit.getWorld("world"), 977.5f, 55.3f, 1345.5f));
			HoloManager.spawnHolo(redName, p.getPlayer());
			p.setBlueBaseName(blueName);
			p.setRedBaseName(redName);
		}
	}
	
	public static void startGame() {
		setPlayers();
		setBaseLife();
		gameRunning = true;
		gameTime = 0;
		ShopNPCManager.addNPC(player1);
		ShopNPCManager.addNPC(player2);
		Utils.loadBase("blue_cave");
		Utils.loadBase("red_cave");
		Utils.giveItems(player1);
		Utils.giveItems(player2);
		for (AowPlayer p : aowplayers)
			ScoreboardManager.init(p);
	}
	
	public static void endGame() {
		gameRunning = false;
		
		
		if (blueBase.getLife() != redBase.getLife()) {
			
			AowPlayer loser = (blueBase.getLife() > redBase.getLife()) ? player2 : player1;
			String baseName = (loser.getPlayer().getUniqueId().equals(player1.getPlayer().getUniqueId())) ? "blue" : "red";
			Location l = (loser.getPlayer().getUniqueId().equals(player1.getPlayer().getUniqueId())) ? new Location(loser.getPlayer().getLocation().getWorld(), 983, 57, 1302) : new Location(loser.getPlayer().getLocation().getWorld(), 983, 57, 1350); 
			
			for (int i = 0; i < 5; i++) {
				new BukkitRunnable(){
					@Override
					public void run() {
						for (AowPlayer p : aowplayers) {
							NMSParticle.spawnParticle(p.getPlayer(), EnumParticle.EXPLOSION_HUGE, (float) l.getX() + (((new Random().nextFloat() * 2) - 1) * 5), (float) l.getY() + (((new Random().nextFloat() * 2) - 1) * 5), (float) l.getZ() + (((new Random().nextFloat() * 2) - 1) * 5));
							p.getPlayer().playSound(l, Sound.EXPLODE, 4, 1);
						}
					}
				}.runTaskLater(Main.getPlugin(Main.class), 5L * (i + 1));
			}
			
			for (int i = 0; i < 10; i++) {
				
				new BukkitRunnable(){
					@Override
					public void run() {
						
						Location loc = new Location(loser.getPlayer().getLocation().getWorld(), 983, 57, (loser.getPlayer().getUniqueId().equals(player1.getPlayer().getUniqueId())) ? 1345 : 1302);
						
						loc.setX(loc.getX() + (((new Random().nextFloat() * 2) - 1) * 10));
						loc.setZ(loc.getZ() + (((new Random().nextFloat() * 2) - 1) * 10));
						loc.setY(60 + new Random().nextInt(10));
						
						Color color = Color.WHITE;
						
						if ((loser.getPlayer().getUniqueId().equals(player1.getPlayer().getUniqueId()))) {
							color = (new Random().nextInt(2) == 0) ? Color.RED : Color.ORANGE;
						} else {
							color = (new Random().nextInt(2) == 0) ? Color.BLUE : Color.AQUA;
						}
						
						Utils.endingFireworks(loc, color);
					}
				}.runTaskLater(Main.getPlugin(Main.class), 20L * (i + 1));
			}
			
			
			switch(loser.getAge()) {
				case PREHISTORIC: baseName += "_cave_broke"; break;
				case MEDIEVAL: baseName += "_castle_broke"; break;
				case RENAISSANCE: baseName += "_church_broke"; break;
				case MODERN: baseName += "_base_broke"; break;
				case FUTURISTIC: baseName += "_space_broke"; break;
				default: baseName += "_cave_broke"; break;
			}
			final String base = baseName;
			
			new BukkitRunnable(){
				@Override
				public void run() {
					Utils.loadBase(base);
				}
			}.runTaskLater(Main.getPlugin(Main.class), 20L);
		}
		
		for (AowPlayer p : Core.aowplayers) {
			ScoreboardManager.update(p);
		}
		
		for (AowPlayer p : aowplayers) {
			p.getPlayer().getInventory().clear();
			if (blueBase.getLife() > redBase.getLife()) {
				if (p == player1) {
					NMSTitle.sendTitle(p.getPlayer(), "§6Game over", "§aYou won the game!", 0, 140, 20);
				} else if (p == player2) {
					NMSTitle.sendTitle(p.getPlayer(), "§6Game over", "§cYou lost the game!", 0, 140, 20);
				} else {
					NMSTitle.sendTitle(p.getPlayer(), "§6Game over", player1.getPlayer().getDisplayName()+"§a won the game!", 0, 140, 20);
				}
			} else if (blueBase.getLife() < redBase.getLife()){
				if (p == player2) {
					NMSTitle.sendTitle(p.getPlayer(), "§6Game over", "§aYou won the game!", 0, 140, 20);
				} else if (p == player1) {
					NMSTitle.sendTitle(p.getPlayer(), "§6Game over", "§cYou lost!", 0, 140, 20);
				} else {
					NMSTitle.sendTitle(p.getPlayer(), "§6Game over", player2.getPlayer().getDisplayName()+"§a won the game!", 0, 140, 20);
				}
			} else {
				NMSTitle.sendTitle(p.getPlayer(), "§6Game over", "§eDraw!", 0, 140, 20);
			}
			ShopNPCManager.removeNPC(p.getLeftShop(), p.getPlayer());
			ShopNPCManager.removeNPC(p.getRightShop(), p.getPlayer());
		}
		
		
		new BukkitRunnable(){
			@Override
			public void run() {
				resetNameColor();
				rsp.setPlaying(false);
				rsp.destroy();
				
				for (AowPlayer p : aowplayers) {
					p.getPlayer().teleport(spawn);
					p.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
					
					WarriorManager.clearWarrior(p);
					HoloManager.deleteHolo(p.getPlayer(), p.getBlueBaseLife());
					HoloManager.deleteHolo(p.getPlayer(), p.getRedBaseLife());
					HoloManager.deleteHolo(p.getPlayer(), p.getBlueBaseName());
					HoloManager.deleteHolo(p.getPlayer(), p.getRedBaseName());
					ScoreboardManager.clear(p);
				}
				player1 = null;
				player2 = null;
				blueBase = null;
				redBase = null;
				aowplayers.clear();
			}
		}.runTaskLater(Main.getPlugin(Main.class), 200L);
		
	}
}
