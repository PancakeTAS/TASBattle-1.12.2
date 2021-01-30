package work.mgnet.commands;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import work.mgnet.Game;
import work.mgnet.utils.SoundsUtils;

public class ReadyCommand implements CommandCallable {

	/**
	 * Runned when the Command gets called
	 */
	@Override
	public CommandResult process(CommandSource src, String arguments) throws CommandException {
		if (Sponge.getGame().getServer().getOnlinePlayers().size()==1) { // If 1 Player online
			// Warn everyone
			for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) player.sendMessage(Text.of("§b»§7 At least 2 players are required"));
			return CommandResult.builder().successCount(1).build();
		}
		if (Game.players.contains(src.getName()) || Game.isRunning==true) return CommandResult.builder().successCount(1).build(); // If the Game is running or they are already in it
		Game.players.add(src.getName()); // Add Player
		
		for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) player.sendMessage(Text.of("§b»§a " + src.getName() + "§7 is now ready!")); // Give everyone that message
		
		SoundsUtils.playSound(SoundTypes.BLOCK_NOTE_PLING, ((Game.players.size() / 10) * 2));
		
		if (Game.players.size() == Sponge.getGame().getServer().getOnlinePlayers().size()) { // If Everyone Ready
			Game.startGame(); // Start Game
		}
		return CommandResult.builder().successCount(1).build();
	}

	// Stuff noone cares about
	
	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition)
			throws CommandException {
		return null;
	}

	@Override
	public boolean testPermission(CommandSource source) {
		return true;
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return null;
	}

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return null;
	}

	@Override
	public Text getUsage(CommandSource source) {
		return null;
	}

	
}
