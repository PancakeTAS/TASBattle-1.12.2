package work.mgnet.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import work.mgnet.FFA;
import work.mgnet.utils.CommandUtils;
import work.mgnet.utils.KitUtils;

public class SetItemsCommand implements CommandCallable {

	/**
	 * Runned when the Command gets called
	 */
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		// Permission Check
		if (!source.hasPermission("mgw.admin")) return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
		if (arguments.isEmpty()) { // Is Argument?
			source.sendMessage(Text.of("§b»§7 You need to specify the Kit")); // Send Message
			return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
		}
		Player p = (Player) source; // Cast Player
		try {
			p.openInventory(KitUtils.loadKit(arguments.toLowerCase(), FFA.getConfigDir())); // Open Kit
		} catch (Exception e) {
			p.openInventory(Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(Sponge.getPluginManager().getPlugin("ffa").get())); // Create new Kit
			source.sendMessage(Text.of("§b»§7 A new Kit has been created")); // Message them
		}
		
		FFA.edit.put(p.getName(), arguments.toLowerCase()); // Put them to editing List
		return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
	}

	// Stuff noone cares about
	
	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition)
			throws CommandException {
		Collection<String> liste= new ArrayList<String>();
		String[] args=arguments.split(" ");
		if(args.length==1) {
			for (File file : FFA.getConfigDir().toFile().listFiles()) {
				if (file.getName().endsWith(".kit")) liste.add(file.getName().split(".kit")[0]);
			}
		}
		return CommandUtils.getListOfStringsMatchingLastWord(args, liste);
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
