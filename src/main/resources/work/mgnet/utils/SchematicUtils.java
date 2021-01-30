package work.mgnet.utils;

import java.io.File;
import java.io.IOException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;


public class SchematicUtils {

	/**
	 * Paste a Schematic
	 * @param Schematic File
	 * @throws IOException 
	 */
	public static void pasteSchematic(File schemFile) throws IOException {
		if (!schemFile.exists()) return; // Return if it doesn't exist
		
		for (Player p : Sponge.getServer().getOnlinePlayers()) {
			p.sendMessage(Text.of("§b»§7 Reloading the Map! This may take a while"));
		}
		ClipboardFormats.findByFile(schemFile).load(schemFile).paste(SpongeWorldEdit.inst().getWorld(Sponge.getServer().getWorlds().iterator().next()), new Vector(0, 0, 0));
	}
	
	/**
	 * Quick Method for Trying to Paste A Schematic
	 * @param Schematic File
	 */
	public static void tryPasteSchematic(File schemFile) {
		try {
			pasteSchematic(schemFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
