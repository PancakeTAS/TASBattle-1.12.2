package work.mgnet.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class CommandUtils {

	/**
	 * Runs a Command
	 * @param Command to Run
	 */
	public static void runCommand(String command) {
		String cmd = command.split(" ")[0]; // Get Command
		String parameters = command.split(" ", 2)[1]; // Get Parameters
		try {
			Sponge.getCommandManager().get(cmd).get().getCallable().process(Sponge.getServer().getConsole(), parameters); // Run Command
		} catch (CommandException e) {
			System.out.println("[CommandUtils] Error Couldn't run Command: " + e.getClass().getName());
		}
	}
	
	// @Scribble what the heck is this. you comment it
	
	public static List<String> getListOfStringsMatchingLastWord(String[] args, String... possibilities)
    {
        return getListOfStringsMatchingLastWord(args, Arrays.asList(possibilities));
    }
	
	public static List<String> getListOfStringsMatchingLastWord(String[] inputArgs, Collection<?> possibleCompletions)
    {
        String s = inputArgs[inputArgs.length - 1];
        List<String> list = Lists.<String>newArrayList();

        if (!possibleCompletions.isEmpty())
        {
            for (String s1 : Iterables.transform(possibleCompletions, Functions.toStringFunction()))
            {
                if (doesStringStartWith(s, s1))
                {
                    list.add(s1);
                }
            }
        }

        return list;
    }
	
	/**
     * Returns true if the given substring is exactly equal to the start of the given string (case insensitive).
     */
    public static boolean doesStringStartWith(String original, String region)
    {
        return region.regionMatches(true, 0, original, 0, original.length());
    }
}
