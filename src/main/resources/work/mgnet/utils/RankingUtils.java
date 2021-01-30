package work.mgnet.utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import work.mgnet.FFA;
import work.mgnet.utils.StatsUtils.Stats;

public class RankingUtils {
	
	public static void onDeath(Player p) {
		p.sendMessage(Text.of("§b» §e[-10 Points] You died"));
		FFA.statsUtils.updatePoints(p, -10);
	}
	
	/* Noob - less than 500 = 0
	* Beginner - 500 -> 2000 = 1
	* Fighter - 2000 -> 5000 = 2
	* Advanced 5000 -> 10000 = 3
	* Pro 10000 -> 50000 = 4
	* TASLegend 50000 = 5
	* 
	* If a TASLegend kills a Pro (>=) they gain 25 points
	* If a TASLegend kills a TASLegend (==) they gain 1 Points
	* If a TASLegend gets killed by a Pro (<=) they lose 1 points
	* If a TASLegend gets killed by a Advanced (<<) they lose 25 points
	* If a TASLegend gets killed by a Fighter (<<<) they lose 250 points
	* If a TASLegend gets killed by a Beginner (<<<<) they lose 2500 points
	* If a TASLegend gets killed by a Noob (<<<<<) they lose 2500 points (wow gl)
	*/
	
	public static int getRank(Player pl) {
		Stats s = FFA.statsUtils.getStats(pl);
		int p = s.points;
		if (p < 500) return 0;
		else if (p < 2000) return 1;
		else if (p < 5000) return 2;
		else if (p < 10000) return 3;
		else if (p < 50000) return 4;
		else return 5;
	}
	
 	public static void onKill(Player killer, Player p) {
		int killerRank = getRank(killer);
		int myRank = getRank(p);
		
		int points = 0;
		
		if (killerRank == myRank) points = 1;
		else if ((killerRank + 1) == myRank) points = 1;
		else if ((killerRank + 2) == myRank) points = 25;
		else if ((killerRank + 3) == myRank) points = 250;
		else if ((killerRank + 4) == myRank) points = 2500;
		else if ((killerRank + 5) == myRank) points = 2500;
		
		killer.sendMessage(Text.of("§b» §e[+" + points + " Points] You killed a player"));
		p.sendMessage(Text.of("§b» §e[+" + -points + " Points] You were killed by a player"));
		
		FFA.statsUtils.updatePoints(killer, points);
		FFA.statsUtils.updatePoints(p, -points);
		
	}
	
	public static void onWin(Player winner) {
		winner.sendMessage(Text.of("§b» §e[+25 Points] You won a Game"));
		FFA.statsUtils.updatePoints(winner, 25);	
	}
	
	public static void updateRank(Player p) {
		
	}
	
}
