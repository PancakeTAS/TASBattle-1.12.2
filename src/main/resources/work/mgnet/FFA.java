package work.mgnet;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.EnderCrystal;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;

import work.mgnet.commands.FFAConfigCommand;
import work.mgnet.commands.ForceendCommand;
import work.mgnet.commands.ForcestartCommand;
import work.mgnet.commands.ItemsCommand;
import work.mgnet.commands.ReadyCommand;
import work.mgnet.commands.ReloadmapCommand;
import work.mgnet.commands.SetItemsCommand;
import work.mgnet.commands.SetKitCommand;
import work.mgnet.commands.StatisticsCommand;
import work.mgnet.utils.CommandUtils;
import work.mgnet.utils.ConfigurationUtils;
import work.mgnet.utils.KitUtils;
import work.mgnet.utils.RankingUtils;
import work.mgnet.utils.SoundsUtils;
import work.mgnet.utils.StatsUtils;

@Plugin(id = "ffa", name = "FFA", version = "1.0", description = "Adds FFA")
public class FFA {

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path privateConfigDir; // Given Config Direction 

	public static String selectedKit = "default"; // Currently Selected Kit
	private static Path configDir; // Public Config Direction
	public static File mapFile; // Public Map Schematic File
	
	public static ConfigurationUtils configUtils; //Configuration Utils
	public static StatsUtils statsUtils; // Stats Utils
	
	public static HashMap<String, String> edit = new HashMap<String, String>(); // List for /setitems
	
	/**
	 * Get The Selected Map File
	 * @return Returns the Schematic File of the Currently Selected Map
	 */
	public static File getMapFile() {
		return mapFile;
	}
	
	/**
	 * Get the Config Directory Path
	 * @return Returns the Config Dir of the Plugin
	 */
	public static Path getConfigDir() {
		return configDir;
	}
	
	/**
	 * Load Configuration and register Commands when the Server gets started.
	 * @see ConfigurationUtils, StatsUtils
	 */
	@Listener
	public void onServer(GameStartedServerEvent e) {
		CommandUtils.runCommand("kill @e[type=EnderCrystal]");
		
		try {
			configUtils = new ConfigurationUtils(privateConfigDir.toFile()); // Set Configuration Utils
			
			 // Set Default Settings if they don't exist
			
			mapFile = new File(privateConfigDir.toFile(), configUtils.getString("map")); // Set Schematics File
			
			statsUtils = new StatsUtils(); // Set Stats Utils
			
			// Try to Load Stats
			try {
				statsUtils.loadStats(privateConfigDir.toFile());
			} catch (Exception nothinghappend) {
				System.out.println("Nothing happend lmao");
			}
		} catch (Exception e1) {
			System.out.println("[FFA] Couldn't load Configuration!");
		}
		
		configDir = privateConfigDir; // Make Config Public
		
		// Register Commands
		Sponge.getCommandManager().register(this, new ReadyCommand(), "ready");
		Sponge.getCommandManager().register(this, new ForceendCommand(), "forceend");
		Sponge.getCommandManager().register(this, new ForcestartCommand(), "forcestart"); 
		Sponge.getCommandManager().register(this, new ReloadmapCommand(), "reloadmap");
		Sponge.getCommandManager().register(this, new StatisticsCommand(), "statistics");
		Sponge.getCommandManager().register(this, new FFAConfigCommand(), "ffa");
		Sponge.getCommandManager().register(this, new ItemsCommand(), "items");
		Sponge.getCommandManager().register(this, new SetKitCommand(), "setkit");
		Sponge.getCommandManager().register(this, new SetItemsCommand(), "setitems");
	}
	
	/**
	 * When a Player join's add them to the current Game
	 * @see Game
	 */
	@Listener
	public void onLogin(ClientConnectionEvent.Join e) {
		e.setMessageCancelled(true); // Disable Join Message
		Game.playerJoin(e.getTargetEntity()); // Join Game
	}
	
	/**
	 * Let no player go higher than y 220!
	 * @see Game
	 */
	@Listener
	public void onMove(MoveEntityEvent e) {
		if (Game.isRunning && e.getToTransform().getPosition().getY() > 220 && e.getTargetEntity().getType() == EntityTypes.PLAYER && ((Player) e.getTargetEntity()).get(Keys.GAME_MODE).get() == GameModes.SURVIVAL) {
			if (e.getTargetEntity().getType() == EntityTypes.ENDER_PEARL) {
				e.getTargetEntity().setVelocity(e.getTargetEntity().getVelocity().div(1, 10000, 1).sub(0, 1, 0)); // Make Ender Pearls descent
				return;
			}
			Location<World> loc = e.getTargetEntity().getLocation();
			Vector3d vec = loc.getPosition();
			loc.setPosition(new Vector3d(vec.getX(), 215, vec.getZ()));
		} else if (Game.isRunning && e.getToTransform().getPosition().getX() > 5 && e.getTargetEntity().getType() == EntityTypes.PLAYER && ((Player) e.getTargetEntity()).get(Keys.GAME_MODE).get() == GameModes.SURVIVAL) {
			Location<World> pvpLocation = FFA.configUtils.getLocation(FFA.mapFile.getName() + "_pvp");
			double spreadPlayerDistance = FFA.configUtils.getFloat(FFA.mapFile.getName() + "_spreadPlayerDistance"); 
			double spreadPlayerRadius = FFA.configUtils.getFloat(FFA.mapFile.getName() + "_spreadPlayerRadius");
			CommandUtils.runCommand("spreadplayers " + pvpLocation.getBlockX() + " " + pvpLocation.getBlockZ() + " "+spreadPlayerDistance+" " + spreadPlayerRadius + " false @a"); // Spread the Players around the map
			System.out.println("Invalid Spawn");
		}
	}
	
	/**
	 * When an Item gets dropped and the game is not running cancel the drop.
	 */
	@Listener
	public void onDrop(DropItemEvent e) {
		// Cancel Drop Event when the game isn't running
		e.getCause().first(Player.class).ifPresent((p) -> {
			if (!Game.isRunning && !p.hasPermission("mgw.bypasslobby")) e.setCancelled(true);
		});
	}
	
	/**
	 * When the Game is not Running disable PVP.
	 * Or Update Statistics if the Game is running and this Damage Event will kill the Player
	 * @see StatsUtils
	 */
	@Listener
	public void onPvP(DamageEntityEvent e) {
		try {
			e.getCause().first(Player.class).ifPresent((killer) -> {
				
				if (Game.isRunning && e.getTargetEntity().getType() == EntityTypes.PLAYER) {
					if (Game.team1.contains(((Player) e.getTargetEntity()).getName()) && Game.team1.contains(killer.getName())) e.setBaseDamage(0);
					else if (Game.team2.contains(((Player) e.getTargetEntity()).getName()) && Game.team2.contains(killer.getName())) e.setBaseDamage(0);
				}
				if (Game.isRunning && e.willCauseDeath() && e.getTargetEntity().getType() == EntityTypes.PLAYER) { // When a Player dies and the game is running
					
					// Try to get the Player by using Dirty Code
					for (Player p : Sponge.getServer().getOnlinePlayers()) {
						if (e.getCause().getContext().toString().contains(p.getName())) {
							RankingUtils.onKill(p, (Player) e.getTargetEntity());
							FFA.statsUtils.updateStats(p.getUniqueId(), 1, 0, 0, 0); // Give the killer a Kill
							SoundsUtils.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, p);
							break; // We found him!
						}
					}
					
					SoundsUtils.playSound(SoundTypes.ENTITY_ELDER_GUARDIAN_CURSE, (Player) e.getTargetEntity());
					
					// Add one Death and one ran Game to the Player
					FFA.statsUtils.updateStats((Player) e.getTargetEntity(), 0, 1, 1, 0);
				}
			});
			Optional<DamageSource> source = e.getCause().first(DamageSource.class); // Try to get the Damage Source
			if (!Game.isRunning && source.get() != DamageSources.VOID) e.setCancelled(true); // If it's not void and the game isn't running cancel it
		} catch (Exception f) {
			//
		}
	}
	
	/**
	 * Remove the Player from the game if they disconnect
	 * @see Game
	 */
	@Listener
	public void onLeave(ClientConnectionEvent.Disconnect e) throws CommandException {
		e.setMessageCancelled(true);
		Game.playerOut(e.getTargetEntity()); // Remove the Player from the Game
	}
	
	/**
	 * Remove the Player from the game if they die
	 * @see Game
	 */
	@Listener
	public void onDeath(DestructEntityEvent.Death e) {
		if (e.getTargetEntity().getType() == EntityTypes.PLAYER) { // If a Player died
			if (configUtils.getString(mapFile.getName() + "_gamemode").equalsIgnoreCase("cores")) {
				Location<World> pvpLocation = FFA.configUtils.getLocation(mapFile.getName() + "_pvp");
				double spreadPlayerDistance = FFA.configUtils.getFloat(mapFile.getName() + "_spreadPlayerDistance"); 
				double spreadPlayerRadius = FFA.configUtils.getFloat(mapFile.getName() + "_spreadPlayerRadius");
				if (Game.team1.contains(((Player) e.getTargetEntity()).getName()) && Game.canRespawnTeam1 >= 1) {
					e.setKeepInventory(true);
					e.setCancelled(true);
					e.getTargetEntity().offer(Keys.HEALTH, 20.0);
					((Player) e.getTargetEntity()).sendMessage(Text.of("户7 You may still respawn because your Core is still alive!"));
					CommandUtils.runCommand("spreadplayers " + pvpLocation.getBlockX() + " " + pvpLocation.getBlockZ() + " "+spreadPlayerDistance+" " + spreadPlayerRadius + " false " + ((Player) e.getTargetEntity()).getName()); // Spread the Players around the map
					return;
				} else if (Game.team2.contains(((Player) e.getTargetEntity()).getName()) && Game.canRespawnTeam2 >= 1) {
					e.setKeepInventory(true);
					((Player) e.getTargetEntity()).sendMessage(Text.of("户 You may still respawn because your Core is still alive!"));
					e.setCancelled(true);
					e.getTargetEntity().offer(Keys.HEALTH, 20.0);
					CommandUtils.runCommand("spreadplayers " + pvpLocation.getBlockX() + " " + pvpLocation.getBlockZ() + " "+spreadPlayerDistance+" " + spreadPlayerRadius + " false " + ((Player) e.getTargetEntity()).getName()); // Spread the Players around the map
					return;
				}
			}
			Game.playerOut((Player) e.getTargetEntity()); // Remove the Player from the Game
		}
	}
	
	/**
	 * When a Player is closing their inventory and the invenory is a Kit Inventory
	 * @see KitUtils
	 */
	@Listener
	public void onInv(InteractInventoryEvent.Close e) throws Exception {
		if (edit.containsKey(((Player) e.getSource()).getName())) { // If the Player is in the /setitems list
			KitUtils.saveKit(edit.get(((Player) e.getSource()).getName()), e.getTargetInventory(), privateConfigDir); // Save the Inventory to the Kit
			edit.remove(((Player) e.getSource()).getName()); // Player is no longer editing
		}
	}
	
	/**
	 * Update the Map File
	 * @see Game
	 */
	public static void setMapFile(String map) {
		mapFile = new File(configDir.toFile(), map); // Set Schematics File
	}
	
	public long noTime = 0L;
	
	/**
	 * When a player dies, do stuff
	 * @see Game
	 */
	@Listener
	public void onPlayerDeath(DestructEntityEvent.Death event) {
		if (!(event.getTargetEntity() instanceof Player)) {
	        return;
		}
		Player eventplayer = (Player) event.getTargetEntity();
		for (Player player : Sponge.getServer().getOnlinePlayers()) {
			ParticleEffect effect = ParticleEffect.builder()
			        .type(ParticleTypes.FIREWORKS)
			        .build();
			player.spawnParticles(effect, eventplayer.getPosition().add(0, 1, 0));
			player.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, player.getPosition(), 1);
		}
    
	}
	
	@Listener
	public void onDamage(InteractEntityEvent.Primary e) {
		if (e.getTargetEntity().getType() == EntityTypes.ENDER_CRYSTAL && System.currentTimeMillis() > noTime && configUtils.getString(mapFile.getName() + "_gamemode").equalsIgnoreCase("cores")) {
			SoundsUtils.playSound(SoundTypes.ENTITY_WITHER_HURT, e.getCause().first(Player.class).get());
			e.setCancelled(true);
			noTime = System.currentTimeMillis() + 750L;
			if (e.getTargetEntity().getLocation().equals(Game.crystal1.getLocation()) && Game.team2.contains(e.getCause().first(Player.class).get().getName())) {
				Game.canRespawnTeam1--;
				for (Player p : Sponge.getServer().getOnlinePlayers()) {
					p.sendMessage(Text.of("户7 The Core of Team Blue is being attacked! " + (Game.canRespawnTeam1 + 1) + " HP"));
				}
				if (Game.canRespawnTeam1 <= 0) {
					((EnderCrystal) e.getTargetEntity()).detonate();
					SoundsUtils.playSound(SoundTypes.ENTITY_WITHER_DEATH);
					for (Player p : Sponge.getServer().getOnlinePlayers()) {
						p.sendMessage(Text.of("户7 The Core of Team Blue died"));
					}
				}
			} else if (e.getTargetEntity().getLocation().equals(Game.crystal2.getLocation()) && Game.team1.contains(e.getCause().first(Player.class).get().getName())) {
				Game.canRespawnTeam2--;
				for (Player p : Sponge.getServer().getOnlinePlayers()) {
					p.sendMessage(Text.of("户7 The Core of Team Red is being attacked! " + (Game.canRespawnTeam2 + 1) + " HP"));
				}
				if (Game.canRespawnTeam2 <= 0) {
					((EnderCrystal) e.getTargetEntity()).detonate();
					SoundsUtils.playSound(SoundTypes.ENTITY_WITHER_DEATH);
					for (Player p : Sponge.getServer().getOnlinePlayers()) {
						p.sendMessage(Text.of("户7 The Core of Team Red died"));
					}
				}
			} 
		}
	}
}
