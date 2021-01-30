package work.mgnet;

import java.util.ArrayList;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.EnderCrystal;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import work.mgnet.utils.CommandUtils;
import work.mgnet.utils.KitUtils;
import work.mgnet.utils.RankingUtils;
import work.mgnet.utils.SchematicUtils;
import work.mgnet.utils.SoundsUtils;

public class Game {

	public static ArrayList<String> players = new ArrayList<String>(); // List of playing Players
	public static boolean isRunning = false; // Is the Game Running
	public static ArrayList<String> team1 = new ArrayList<>();
	public static ArrayList<String> team2 = new ArrayList<>();
	public static int canRespawnTeam1;
	public static int canRespawnTeam2;
	public static EnderCrystal crystal1;
	public static EnderCrystal crystal2;
	
	private static void splitTeams() {
		// get size players the list 
        int size = players.size(); 
  
        // First size)/2 element copy into list 
        // first and rest second list 
        String team1str = "§b» §7Team Blue:";
        String team2str = "§b» §7Team Red:";
        for (int i = 0; i < size / 2; i++) {
            team1.add(players.get(i)); 
            ItemStack teambluehelmet = ItemStack.builder().itemType(ItemTypes.LEATHER_HELMET).quantity(1).build();
            teambluehelmet.offer(Keys.COLOR, Color.BLUE);
            Sponge.getServer().getPlayer(players.get(i)).get().setHelmet(teambluehelmet);
            team1str = team1str + " " + players.get(i);
        }
  
        // Second size)/2 element copy into list 
        // first and rest second list 
        for (int i = size / 2; i < size; i++) { 
            team2.add(players.get(i));
            ItemStack teamredhelmet = ItemStack.builder().itemType(ItemTypes.LEATHER_HELMET).quantity(1).build();
            teamredhelmet.offer(Keys.COLOR, Color.RED);
            Sponge.getServer().getPlayer(players.get(i)).get().setHelmet(teamredhelmet);
        	team2str = team2str + " " + players.get(i);
        }
        
		for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) {
			player.sendMessage(Text.of(team1str));
			player.sendMessage(Text.of(team2str));
		}
	}
	
	/**
	 * Starts the Game by setting a bunch of settings like tickrate difficulty etc..
	 * @see CommandUtils
	 */
	public static void startGame() {
		
		SchematicUtils.tryPasteSchematic(FFA.getMapFile()); // Load Map
		
		SoundsUtils.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP);
		
		if (FFA.configUtils.getString(FFA.mapFile.getName() + "_gamemode").equalsIgnoreCase("teamdeathmatch")) {
			splitTeams();
		}
		
		if (FFA.configUtils.getString(FFA.mapFile.getName() + "_gamemode").equalsIgnoreCase("cores")) {
			splitTeams();
	        
			canRespawnTeam1 = 50;
			canRespawnTeam2 = 50;
			
			World world = Sponge.getServer().getWorlds().iterator().next();
			
			crystal1 = (EnderCrystal) world.createEntity(EntityTypes.ENDER_CRYSTAL, FFA.configUtils.getLocation(FFA.mapFile.getName() + "_crystal1").getPosition());
			crystal1.offer(Keys.INVULNERABLE, true);
			world.spawnEntity(crystal1);
			
			crystal2 = (EnderCrystal) world.createEntity(EntityTypes.ENDER_CRYSTAL, FFA.configUtils.getLocation(FFA.mapFile.getName() + "_crystal2").getPosition());
			crystal2.offer(Keys.INVULNERABLE, true);
			world.spawnEntity(crystal2);
			
		}
		
		CommandUtils.runCommand("tickrate " + FFA.configUtils.getFloat(FFA.mapFile.getName() + "_tickrate")); // Change Tickrate
		CommandUtils.runCommand("difficulty 1"); // Change Difficulty
		CommandUtils.runCommand("effect @a clear"); // Clear Effects
		
		// Load Vars
		Location<World> pvpLocation = FFA.configUtils.getLocation(FFA.mapFile.getName() + "_pvp");
		double spreadPlayerDistance = FFA.configUtils.getFloat(FFA.mapFile.getName() + "_spreadPlayerDistance"); 
		double spreadPlayerRadius = FFA.configUtils.getFloat(FFA.mapFile.getName() + "_spreadPlayerRadius");
		
		CommandUtils.runCommand("spreadplayers " + pvpLocation.getBlockX() + " " + pvpLocation.getBlockZ() + " "+spreadPlayerDistance+" " + spreadPlayerRadius + " false @a"); // Spread the Players around the map
		
		for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) { // Every PLayer
			player.offer(Keys.GAME_MODE, GameModes.SURVIVAL); // Survival Mode
			player.offer(Keys.HEALTH, 20D); // Full HP
			player.offer(Keys.FOOD_LEVEL, 20); // Full Hunger
			player.offer(Keys.EXPERIENCE_LEVEL, 0); // No Levels
			player.sendMessage(Text.of("§b»§7 The Game has begun. Kill everyone to win")); // Send Message
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				isRunning = true; // Make it Running
				
			}
		}).start();
	}
	
	/**
	 * Add a Player to the Game
	 * @param Player that will join the Game
	 */
	public static void playerJoin(Player p) {
		
		
		
		p.getInventory().clear(); // Clear their Inventory
		p.setLocation(p.getWorld().getSpawnLocation()); // Teleport them to Spawn
		CommandUtils.runCommand("spawnpoint " + p.getName()); // Set their respawn Point to Spawn
		if (isRunning) { // If the Game is already Running
			p.offer(Keys.GAME_MODE, GameModes.SPECTATOR); // Set them to Spectator
			p.sendMessage(Text.of("§b»§7 A game is already running, after the round you will participate")); // Send them a Message
			CommandUtils.runCommand("tickrate " + FFA.configUtils.getDouble(FFA.mapFile.getName() + "_tickrate") + " " + p.getName()); // Change their Tickrate
		} else {
			p.offer(Keys.GAME_MODE, GameModes.ADVENTURE); // Set their GameMode to Adventure
			p.sendMessage(Text.of("§b»§7 Type §a/items §7to see all the items you can get. When you are ready, type §a/ready§7.")); // Send them the Message
			CommandUtils.runCommand("tickrate 20 " + p.getName()); // Set their tickrate back to t20
		}
	}
	
	/**
	 * Remove a Player from the Game.
	 * @param Player to remove
	 */
	public static void playerOut(Player p) {
		if (!players.contains(p.getName())) return; // If they aren't in the Game quit
		
		RankingUtils.onDeath(p);
		
		players.remove(p.getName()); // Remove them from the Game
		
		if (team1.contains(p.getName())) team1.remove(p.getName());
		if (team2.contains(p.getName())) team2.remove(p.getName());
		
		p.offer(Keys.GAME_MODE, GameModes.SPECTATOR); // Set them to Spectator
		if (FFA.configUtils.getString(FFA.mapFile.getName() + "_gamemode").equalsIgnoreCase("ffa") && players.size() == 1) { // If one Player remains
			Player winner = Sponge.getServer().getPlayer(players.get(0)).get(); // Get the Winner
			for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) {
				RankingUtils.onWin(player);
				player.sendTitle(Title.of(Text.of(winner.getName() + " won!"))); // Let everyone know!
			}
			FFA.statsUtils.updateStats(winner, 0, 0, 1, 1); // Give them a game and a win
			endGame(); // End The Game
		} else if (FFA.configUtils.getString(FFA.mapFile.getName() + "_gamemode").equalsIgnoreCase("teamdeathmatch") || FFA.configUtils.getString(FFA.mapFile.getName() + "_gamemode").equalsIgnoreCase("cores")) {
			if (team1.size() == 0) {
				for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) {
					player.sendTitle(Title.of(Text.of("Team Red won!"))); // Let everyone know!
				}
				for (String pl : team2) {
					Player player = Sponge.getServer().getPlayer(pl).get();
					FFA.statsUtils.updateStats(player, 0, 0, 1, 1);
				}
				endGame();
			} else if (team2.size() == 0) {
				for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) {
					player.sendTitle(Title.of(Text.of("Team Blue won!"))); // Let everyone know!
				}
				for (String pl : team1) {
					Player player = Sponge.getServer().getPlayer(pl).get();
					FFA.statsUtils.updateStats(player, 0, 0, 1, 1);
				}
				endGame();
			}
		}
	}
	
	/**
	 * End the Game
	 */
	public static void endGame() {
		if (!isRunning) return; // Cannot end the Game if it isn't running
		isRunning = false; // Set Game not running
		
		if (crystal1 != null) crystal1.remove();
		if (crystal2 != null) crystal2.remove();
		
		players.clear(); // Clear the Players
		team1.clear();
		team2.clear();
		KitUtils.inves.clear(); // Reset the Inventories
		for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) { // Every Player
			player.setLocation(player.getWorld().getSpawnLocation()); // Tp to Spawn
			player.getInventory().clear(); // Clear Inv
			player.offer(Keys.HEALTH, 20D); // Set HP
			player.offer(Keys.FOOD_LEVEL, 20); // Set Food
			player.offer(Keys.EXPERIENCE_LEVEL, 0); // Set XP
			
			// Send Messages
			player.sendMessage(Text.of("§b»§e The Game has ended"));
			player.sendMessage(Text.of("§b»§7 Type §a/items §7to see all the items you can get. When you are ready, type §a/ready§7."));
			
			// Set back to Adventure
			player.offer(Keys.GAME_MODE, GameModes.ADVENTURE);
		}
		
		// Reset Stuff
		CommandUtils.runCommand("difficulty 0");
		CommandUtils.runCommand("effect @a clear");
		CommandUtils.runCommand("tickrate 20");
		CommandUtils.runCommand("kill @e[type=!player]");
	
		SoundsUtils.playSound(SoundTypes.ENTITY_FIREWORK_BLAST_FAR);
		
	}
	
}
