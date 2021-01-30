package work.mgnet.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.DataView.SafetyMode;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;

import com.google.common.io.Files;

public class KitUtils {
	
	// @Scribble Comment this 
	
	public static HashMap<String, Inventory> inves = new HashMap<String, Inventory>();
	
	public static void saveKit(String name, Inventory inventory, Path privateConfigDir) throws Exception {
		File kitFile = Paths.get(privateConfigDir.toString(), name + ".kit").toFile();
		if (!kitFile.exists())
			kitFile.createNewFile();
		ArrayList<DataView> items = serializeInventory(inventory);
		StringBuilder json = new StringBuilder();

		for (DataView dataView : items) {
			if (dataView != null) {
				json = json.append(DataFormats.JSON.write(dataView) + "\n");
			}
		}

		try {
			Files.write(json.toString().getBytes(), kitFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Inventory loadKit(String name, Path privateConfigDir) throws Exception {
		File kitFile = Paths.get(privateConfigDir.toString(), name + ".kit").toFile();
		if (!kitFile.exists()) {
			throw new Exception();
		}
		List<DataView> items = new ArrayList<DataView>();
		BufferedReader oos = new BufferedReader(new FileReader(kitFile));
		String json;
		while((json=oos.readLine())!=null) {
			items.add(DataFormats.JSON.read(json));
		}
		oos.close();
		Inventory inv = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(Sponge.getPluginManager().getPlugin("ffa").get());
		inv = deserializeInventory(items, inv);
		return inv;
	}

	static final DataQuery SLOT = DataQuery.of("slot");
	static final DataQuery STACK = DataQuery.of("stack");

	private static ArrayList<DataView> serializeInventory(Inventory inventory) {
		DataContainer container;
		ArrayList<DataView> slots = new ArrayList<DataView>();

		int i = 0;
		Optional<ItemStack> stack;

		for (Inventory inv : inventory.slots()) {
			stack = inv.peek();

			if (stack.isPresent()) {
				container = DataContainer.createNew(SafetyMode.ALL_DATA_CLONED);

				container.set(SLOT, i);
				container.set(STACK, serializeItemStack(stack.get()));
				
				
				
				slots.add(container);
			}

			i++;
		}

		return slots;
	}

	private static Inventory deserializeInventory(List<DataView> slots, Inventory inventory) {
		Map<Integer, ItemStack> stacks = new HashMap<>();
		int i;
		ItemStack stack;

		for (DataView slot : slots) {
			i = slot.getInt(SLOT).get();

			try {
				stack = deserializeItemStack(slot.getView(STACK).get());
				stacks.put(i, stack);
			} catch (NoSuchElementException e) {
				stacks.remove(i);
			}
		}

		i = 0;

		for (Inventory slot : inventory.slots()) {
			if (stacks.containsKey(i)) {
				try {
					slot.set(stacks.get(i));
				} catch (NoSuchElementException e) {
					slot.clear();
				}
			} else {
				slot.clear();
			}

			++i;
		}

		return inventory;
	}

	static DataView serializeItemStack(ItemStack item) {
		return item.toContainer();
	}

	static ItemStack deserializeItemStack(DataView data) {
		return ItemStack.builder().fromContainer(data).build();
	}
}
