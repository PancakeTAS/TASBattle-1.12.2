package work.mgnet.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;

public class StatsUtils {
	
public static File statsFile;
	
	public static class Stats {
		
		public Stats(UUID uuid) {
			this.uuid = uuid;
		}
		
		// Some Vars
		public UUID uuid;
		public int kills = 0;
		public int deaths = 0;
		public int games = 0;
		public int gamesWon = 0;
		public int points = 1000;
		
		/**
		 * Serialize Stats
		 */
		@Override
		public String toString() {
			return uuid.toString() + ":" + kills + ":" + deaths + ":" + games + ":" + gamesWon + ":" + points;
		}
		
		/**
		 * Deserialize Stats
		 * @param Serialized Stats
		 * @return
		 */
		public static Stats fromString(String obj) {
			String[] segmentedObj = obj.split(":");
			Stats stats = new Stats(UUID.fromString(segmentedObj[0]));
			stats.kills = Integer.parseInt(segmentedObj[1]);
			stats.deaths = Integer.parseInt(segmentedObj[2]);
			stats.games = Integer.parseInt(segmentedObj[3]);
			stats.gamesWon = Integer.parseInt(segmentedObj[4]);
			stats.points = Integer.parseInt(segmentedObj[5]);
			return stats;
		}
		
		/**
		 * Check if Equal
		 */
		@Override
		public boolean equals(Object obj) {
			return obj instanceof Stats ? ((Stats) obj).uuid.equals(uuid) : false; 
		}
		
	}
	
	public List<Stats> stats = new ArrayList<>();
	
	/**
	 * Change Players Stats
	 * @param UUID Of the Player
	 * @param Kills to add
	 * @param Deaths to add
	 * @param Games to add
	 * @param GamesWon to add
	 */
	public void updateStats(Player uuid, int kills, int deaths, int games, int gamesWon) {
		updateStats(uuid.getUniqueId(), kills, deaths, games, gamesWon);
		
	}
	
	/**
	 * Get Stats of Player
	 * @param Player
	 * @return Stats of that Player
	 */
	public Stats getStats(Player uuid) {
		return getStats(uuid.getUniqueId());
	}
	
	/**
     * Get Stats of UUID
	 * @param UUID of Player
	 * @return Stats of that UUID
	 */
	public Stats getStats(UUID uuid) {
		for (Stats statO : stats) {
			if (statO.equals(new Stats(uuid))) return statO;
		}
		return null;
	}
	
	/**
	 * Change Rank of a Player
	 * @param Rank to give
	 * @param UUID of the Player
	 */
	// YEEEEEEEEEEET
	/*public void updateRank(String rank, UUID uuid) {
		Sponge.getServer().getPlayer(uuid).get().sendMessage(Text.of("§b» §aYou advanced to " + rank)); // Message them
		Sponge.getServer().getServerScoreboard().get().getTeam(rank).get().addMember(Sponge.getServer().getPlayer(uuid).get().getTeamRepresentation()); // Add them to the Team
		try {
			Sponge.getServer().getServerScoreboard().get().getTeam(rank).get().setPrefix(Text.of("§b" + Sponge.getServer().getServerScoreboard().get().getTeam(rank).get().getDisplayName().toPlain() + " §f")); // Psst..
		} catch (Exception e) {
			
		}
	}*/
	
	/**
	 * 
	 * @param UUID of the Player
	 * @param Kills
	 * @param Deaths
	 * @param Games
	 * @param GamesWon
	 */
	public void updateStats(UUID uuid, int kills, int deaths, int games, int gamesWon) {
		if (stats.contains(new Stats(uuid))) { // If the Player every playd
			
			// Try get them
			Stats stat = null;
			for (Stats statO : stats) {
				if (statO.equals(new Stats(uuid))) stat = statO;
			}
			// Add
			stat.kills += kills;
			stat.deaths += deaths;
			stat.games += games;
			stat.gamesWon += gamesWon;
			
			// Readd
			stats.remove(stat);
			stats.add(stat);
			
			// Ranking
			// We YEEEEEEEET the old Ranking
			/*if (stat.games == 2) {
				updateRank("beginner", uuid);
			} else if (stat.games == 20) {
				if (stat.kills > 4) {
					updateRank("fighter", uuid);
				} else {
					updateRank("noob", uuid);
				}
			} else if (stat.games == 50) {
				if (stat.kills > 35) {
					updateRank("advanced", uuid);
				} else {
					updateRank("intermediate", uuid);
				}
			} else if (stat.games == 100) {
				if (stat.kills > 150) {
					updateRank("taslegend", uuid);
				} else {
					updateRank("pro", uuid);
				}
			}*/
			
		} else {
			stats.add(new Stats(uuid)); // Add new Stats
			updateStats(uuid, kills, deaths, games, gamesWon); // Rerun
		}
		
		// Try to Save Stats
		try {
			saveStats();
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't save stats");
		}
		
	}
	
	/**
	 * Saves the Stats
	 * @throws FileNotFoundException
	 */
	public void saveStats() throws FileNotFoundException {
		// Serialize
		
		PrintWriter writer = new PrintWriter(new FileOutputStream(statsFile));
		writer.print("");
		writer.close();
		writer = new PrintWriter(new FileOutputStream(statsFile));
		String string = "";
		for (Stats stat : stats) {
			string = string + ";" + stat.toString();
		}
		string = string.replaceFirst(";", "");
		writer.write(string + "\r\n");
		writer.flush();
		writer.close();
		
	}
	
	/**
	 * Load the Stats
	 * @param configDir
	 * @throws IOException
	 */
	public void loadStats(File configDir) throws IOException {
		// Deserialize the Stats
		
		statsFile = new File(configDir, "stats.yml");
		if (statsFile.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(statsFile));
			String[] serializedStats;
			try {
				serializedStats = reader.readLine().split(";");
			}catch(NullPointerException e) {
				System.err.println("Stats file is empty");
				reader.close();
				return;
			}
			for (String serializedStat : serializedStats) {
				stats.add(Stats.fromString(serializedStat));
			}
			reader.close();
		} else {
			statsFile.createNewFile();
		}
	}

	public void updatePoints(Player killer, int points) {
		UUID uuid = killer.getUniqueId();
		if (stats.contains(new Stats(uuid))) { // If the Player every playd
			
			// Try get them
			Stats stat = null;
			for (Stats statO : stats) {
				if (statO.equals(new Stats(uuid))) stat = statO;
			}
			// Add
			stat.points += points;
			
			// Readd
			stats.remove(stat);
			stats.add(stat);
			
		} else {
			stats.add(new Stats(uuid)); // Add new Stats
			updatePoints(killer, points); // Rerun
		}
		
		// Try to Save Stats
		try {
			saveStats();
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't save stats");
		}
	}
	
}
