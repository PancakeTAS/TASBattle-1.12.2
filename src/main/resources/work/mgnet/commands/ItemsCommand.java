package work.mgnet.commands;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import work.mgnet.FFA;
import work.mgnet.utils.KitUtils;

public class ItemsCommand implements CommandCallable {

	/**
	 * Runned when the Command gets called
	 */
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		
		Player p = (Player) source; // Cast Player
		if (KitUtils.inves.containsKey(p.getName())) { // If they opened it once
			p.openInventory(KitUtils.inves.get(p.getName())); // Open The Inventory
			return CommandResult.builder().successCount(1).affectedItems(0).build();
		}
		
		try {
			Inventory inv = KitUtils.loadKit(FFA.selectedKit, FFA.getConfigDir()); // Get Inventory
			KitUtils.inves.put(p.getName(), inv); // Save Inventory for Player
			p.openInventory(inv); // Open it
		} catch (Exception e) {
			source.sendMessage(Text.of("§b»§7 No Kit has been Selected!"));
		}
		return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
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
