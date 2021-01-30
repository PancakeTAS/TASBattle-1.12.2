package work.mgnet.utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;

public class SoundsUtils {
	
	public static void playSound(SoundType sound, double pitch) {
		for (Player p : Sponge.getServer().getOnlinePlayers()) {
			p.playSound(sound, p.getLocation().getPosition(), 1, pitch);
		}
	}
	
	public static void playSound(SoundType sound) {
		for (Player p : Sponge.getServer().getOnlinePlayers()) {
			p.playSound(sound, p.getLocation().getPosition(), 1);
		}
	}
	
	public static void playSound(SoundType sound, Player p) {
		p.playSound(sound, p.getLocation().getPosition(), 1);
	}
	
}
