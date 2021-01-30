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


public class FFAConfigCommand implements CommandCallable{

	/**
	 * Runned when the Command gets called
	 */
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		// Permission check
		if (!source.hasPermission("mgw.admin")) return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
		
		String[] args=arguments.split(" "); // Split Arguments
		
		if (args.length >= 3) {
		
			/*
		 	* Going trough every Option, and changing them
		 	*/
			if(args[1].equalsIgnoreCase("pvp")) {
				try {
					FFA.configUtils.setLocation(args[0] + "_pvp", Double.parseDouble((args[2])), Double.parseDouble((args[3])), Double.parseDouble((args[4])));
				}catch(NumberFormatException e) {
					throw new CommandException(Text.of("Couldn't parse coordinates"));
				} catch (ObjectMappingException e) {
					throw new CommandException(Text.of("Some unknown mapping error occured"));
				}
			}else if(args[1].equalsIgnoreCase("spawn")) {
				try {
					FFA.configUtils.setLocation(args[0] + "_spawn", Double.parseDouble((args[2])), Double.parseDouble((args[3])), Double.parseDouble((args[4])));
				}catch(NumberFormatException e) {
					throw new CommandException(Text.of("Couldn't parse coordinates"));
				} catch (ObjectMappingException e) {
					throw new CommandException(Text.of("Some unknown mapping error occured"));
				}
			}else if(args[1].equalsIgnoreCase("tickrate")) {
				try {
					FFA.configUtils.setFloat(args[0] + "_tickrate", Float.parseFloat(args[2]));
				} catch (NumberFormatException e) {
					throw new CommandException(Text.of("Couldn't parse tickrate"));
				} catch (ObjectMappingException e) {
					throw new CommandException(Text.of("Some unknown mapping error occured"));
				}
			}else if(args[1].equalsIgnoreCase("spreadPlayerRadius")){
				try {
					FFA.configUtils.setFloat(args[0] + "_spreadPlayerRadius", Float.parseFloat(args[2]));
				} catch (NumberFormatException e) {
					throw new CommandException(Text.of("Couldn't parse radius"));
				} catch (ObjectMappingException e) {
					throw new CommandException(Text.of("Some unknown mapping error occured"));
				}
			}else if(args[1].equalsIgnoreCase("spreadPlayerDistance")) {
				try {
					FFA.configUtils.setFloat(args[0] + "_spreadPlayerDistance", Float.parseFloat(args[2]));
				} catch (NumberFormatException e) {
					throw new CommandException(Text.of("Couldn't parse distance"));
				} catch (ObjectMappingException e) {
					throw new CommandException(Text.of("Some unknown mapping error occured"));
				}
			}else if(args[1].equalsIgnoreCase("gamemode")) {
				try {
					FFA.configUtils.setString(args[0] + "_gamemode", args[2]);
				} catch (ObjectMappingException e) {
					throw new CommandException(Text.of("Some unknown mapping error occured"));
				}
			}else if(args[1].equalsIgnoreCase("crystal1")) {
				try {
					FFA.configUtils.setLocation(args[0] + "_crystal1", Double.parseDouble((args[2])), Double.parseDouble((args[3])), Double.parseDouble((args[4])));
				}catch(NumberFormatException e) {
					throw new CommandException(Text.of("Couldn't parse coordinates"));
				} catch (ObjectMappingException e) {
					throw new CommandException(Text.of("Some unknown mapping error occured"));
				}
			}else if(args[1].equalsIgnoreCase("crystal2")) {
				try {
					FFA.configUtils.setLocation(args[0] + "_crystal2", Double.parseDouble((args[2])), Double.parseDouble((args[3])), Double.parseDouble((args[4])));
				}catch(NumberFormatException e) {
					throw new CommandException(Text.of("Couldn't parse coordinates"));
				} catch (ObjectMappingException e) {
					throw new CommandException(Text.of("Some unknown mapping error occured"));
				}
			}
			else { // If not in List of Arguments
				// Send Message of Arguments
				source.sendMessage(Text.of("§b» §7/ffa <mapname> pvp | tickrate | spreadPlayerRadius | spreadPlayerDistance | gamemode | crystal1 | crystal2")); 
				return CommandResult.builder().successCount(1).build();
			}
		} else {
			source.sendMessage(Text.of("§b» §7/ffa <mapname> pvp | tickrate | spreadPlayerRadius | spreadPlayerDistance | gamemode | crystal1 | crystal2")); 
			return CommandResult.builder().successCount(1).build();
		}
		FFA.configUtils.reloadConfiguration(); // Reload the Configuration
		source.sendMessage(Text.of("§b» §7Successfully changed config option "+args[0])); // Send them Message
		return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
	}
	
	// Stuff noone cares about
	
	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition)
			throws CommandException {
		List<String> liste= new ArrayList<String>();
		String[] args=arguments.split(" ");
		if(!arguments.contains(" ")) {
			for (File file : FFA.getConfigDir().toFile().listFiles()) {
				if (!file.getName().contains(".")) liste.add(file.getName());
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("map")) {
			liste.add("pvp");
			liste.add("spawn");
			liste.add("tickrate");
			liste.add("spreadPlayerRadius");
			liste.add("spreadPlayerDistance");
			liste.add("map");
			liste.add("gamemode");
			liste.add("crystal1");
			liste.add("crystal2");
		} else if (args.length == 2 && args[1].equalsIgnoreCase("gamemode")) {
			liste.add("ffa");
			liste.add("teamdeathmatch");
			liste.add("cores");
		}
		return liste;
	}

	@Override
	public boolean testPermission(CommandSource source) {
		return source.hasPermission("mgw.edit");
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return Optional.of(Text.of("Changes configuration"));
	}

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return Optional.of(Text.of("No help is available atm, please ask your questions after the beep.... *BEEP*"));
	}

	@Override
	public Text getUsage(CommandSource source) {
		return Text.of("/ffa <configname> <value>");
	}
}
