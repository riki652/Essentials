package net.ess3.commands;

import static net.ess3.I18n._;
import net.ess3.api.IUser;
import net.ess3.api.server.CommandSender;
import net.ess3.api.server.Location;
import net.ess3.api.server.Player;
import net.ess3.api.server.World;
import net.ess3.permissions.Permissions;


public class Commandnear extends EssentialsCommand
{
	@Override
	protected void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		long radius = 200;

		IUser otherUser = null;

		if (args.length > 0)
		{
			try
			{
				otherUser = ess.getUserMap().matchUser(args[0], false, false);
			}
			catch (Exception ex)
			{
				try
				{
					radius = Long.parseLong(args[0]);
				}
				catch (NumberFormatException e)
				{
				}
			}
			if (args.length > 1 && otherUser != null)
			{
				try
				{
					radius = Long.parseLong(args[1]);
				}
				catch (NumberFormatException e)
				{
				}
			}
		}
		if (otherUser == null || Permissions.NEAR_OTHERS.isAuthorized(user))
		{
			user.sendMessage(_("nearbyPlayers", getLocal(otherUser == null ? user : otherUser, radius)));
		}
		else
		{
			user.sendMessage(_("noAccessCommand"));
		}
	}

	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length == 0)
		{
			throw new NotEnoughArgumentsException();
		}
		final IUser otherUser = ess.getUserMap().matchUser(args[0], false, false);
		long radius = 200;
		if (args.length > 1)
		{
			try
			{
				radius = Long.parseLong(args[1]);
			}
			catch (NumberFormatException e)
			{
			}
		}
		sender.sendMessage(_("nearbyPlayers", getLocal(otherUser, radius)));
	}

	private String getLocal(final IUser user, final long radius)
	{
		final Location loc = user.getLocation();
		final World world = loc.getWorld();
		final StringBuilder output = new StringBuilder();
		final long radiusSquared = radius * radius;

		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final IUser player = ess.getUserMap().getUser(onlinePlayer);
			if (!player.equals(user) && !player.isHidden())
			{
				final Location playerLoc = player.getLocation();
				if (playerLoc.getWorld() != world)
				{
					continue;
				}

				final long delta = (long)playerLoc.distanceSquared(loc);
				if (delta < radiusSquared)
				{
					if (output.length() > 0)
					{
						output.append(", ");
					}
					output.append(player.getDisplayName()).append("§f(§4").append((long)Math.sqrt(delta)).append("m§f)");
				}
			}
		}
		return output.length() > 1 ? output.toString() : _("none");
	}
}
