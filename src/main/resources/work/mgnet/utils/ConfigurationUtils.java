package work.mgnet.utils;

import java.io.File;
import java.io.IOException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class ConfigurationUtils {
	
	private ConfigurationNode node;
	private @NonNull HoconConfigurationLoader manager;
	
	public ConfigurationUtils(File configDir) throws IOException {
		if (!configDir.exists()) configDir.mkdir();
		File configFile = new File(configDir, "config.yml");
		if (!configFile.exists()) {
			configFile.createNewFile();
			manager = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
			node = manager.createEmptyNode();
		} else {
			manager = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
			node = manager.load();
		}
	}
	
	/*
	 * Returns the Given Type from the Config
	 */
	
	public String getString(String key) {
		return node.getNode(key).getString();
	}
	
	public int getInteger(String key) {
		return node.getNode(key).getInt();
	}
	
	public double getDouble(String key) {
		return node.getNode(key).getDouble();
	}
	
	public float getFloat(String key) {
		return node.getNode(key).getFloat();
	}
	
	public Location<World> getLocation(String key) {
		double x = node.getNode(key + "X").getDouble();
		double y = node.getNode(key + "Y").getDouble();
		double z = node.getNode(key + "Z").getDouble();
		return new Location<World>(Sponge.getServer().getWorlds().iterator().next(), x, y, z);
	}
	
	/*
	 * Change the Given type in the config with value
	 */
	
	public void setString(String key, String value) throws ObjectMappingException {
		node.getNode(key).setValue(TypeTokens.STRING_TOKEN, value);
		saveConfiguration();
	}
	
	public void setInteger(String key, int value) throws ObjectMappingException {
		node.getNode(key).setValue(TypeTokens.INTEGER_TOKEN, value);
		saveConfiguration();
	}
	
	public void setDouble(String key, double value) throws ObjectMappingException {
		node.getNode(key).setValue(TypeTokens.DOUBLE_TOKEN, value);
		saveConfiguration();
	}
	
	public void setFloat(String key, float value) throws ObjectMappingException {
		node.getNode(key).setValue(TypeTokens.FLOAT_TOKEN, value);
		saveConfiguration();
	}
	
	public void setLocation(String key, double x, double y, double z) throws ObjectMappingException {
		node.getNode(key + "X").setValue(TypeTokens.DOUBLE_TOKEN, x);
		node.getNode(key + "Y").setValue(TypeTokens.DOUBLE_TOKEN, y);
		node.getNode(key + "Z").setValue(TypeTokens.DOUBLE_TOKEN, z);
		saveConfiguration();
	}
	
	// Save the Configuration
	public void saveConfiguration() {
		try {
			manager.save(node);
		} catch (IOException e) {
			System.out.println("[ConfigurationManager] Couldn't save configuration!");
		}
	}
	
	// Reload the Configuration
	public void reloadConfiguration() {
		try {
			node = manager.load();
		} catch (IOException e) {
			System.out.println("[ConfigurationManager] Couldn't reload configuration!");
		}
	}
}
