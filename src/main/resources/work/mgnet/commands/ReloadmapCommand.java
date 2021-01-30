package work.mgnet.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import work.mgnet.FFA;
import work.mgnet.utils.SchematicUtils;

public class ReloadmapCommand implements CommandCallable {

	/**
	 * Runned when the Command gets called
	 */
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		// Permission Check
		if (!source.hasPermission("mgw.admin")) return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
		if (arguments.length() == 0) return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
		String[] args = arguments.split(" ");
		File newSchemFile = new File(FFA.getConfigDir().toFile(), args[0]);
		if (newSchemFile.exists()) {
			double tickrate = FFA.configUtils.getDouble(args[0] + "_tickrate");
			double spreadPlayerRadius = FFA.configUtils.getDouble(args[0] + "_spreadPlayerRadius");
			double spreadPlayerDistance = FFA.configUtils.getDouble(args[0] + "_spreadPlayerDistance");
			String gamemode = FFA.configUtils.getString(args[0] + "_gamemode");
			Location<World> location = FFA.configUtils.getLocation(args[0] + "_pvp");
			source.sendMessage(Text.of("§b» §7 Settings for " + args[0] + ":"));
			if (gamemode == null) {
				source.sendMessage(Text.of("§b» §7Couldn't load settings! Set with /ffa <mapname> <setting> <value>"));
				return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
			} else if (gamemode.equalsIgnoreCase("cores")) {
				Location<World> crystal1 = FFA.configUtils.getLocation(args[0] + "_crystal1");
				Location<World> crystal2 = FFA.configUtils.getLocation(args[0] + "_crystal2");
				
				source.sendMessage(Text.of("§b» §7Crystal 1 Location: " + crystal1.getBlockX() + " " + crystal1.getBlockY() + " " + crystal1.getBlockZ()));
				source.sendMessage(Text.of("§b» §7Crystal 2 Location: " + crystal2.getBlockX() + " " + crystal2.getBlockY() + " " + crystal2.getBlockZ()));
			}
			source.sendMessage(Text.of("§b» §7PVP Location: " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ()));
			source.sendMessage(Text.of("§b» §7Gamemode: " + gamemode));
			source.sendMessage(Text.of("§b» §7SpreadPlayerRadius: " + spreadPlayerRadius));
			source.sendMessage(Text.of("§b» §7SpreadPlayerDistance: " + spreadPlayerDistance));
			source.sendMessage(Text.of("§b» §7Tickrate: " + tickrate));
			try {
				FFA.configUtils.setString("map", args[0]);
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}
			source.sendMessage(Text.of("§b» §a" + args[0] + "§7 has been selected!"));
			FFA.setMapFile(args[0]);
			SchematicUtils.tryPasteSchematic(FFA.getMapFile()); // Load Map
		} else {
			source.sendMessage(Text.of("§b» §c" + args[0] + " doesn't exist"));
		}
		return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
	}

	// Stuff noone cares about
	
	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition)
			throws CommandException {
		List<String> liste= new ArrayList<String>();
		if(!arguments.contains(" ")) {
			for (File file : FFA.getConfigDir().toFile().listFiles()) {
				if (!file.getName().contains(".")) liste.add(file.getName());
			}
		}
		return liste;
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
