package me.soldado.montaria;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Comandos implements CommandExecutor {

	public Main plugin;
	
	public Comandos(Main plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("sela")){
				if(p.hasPermission(plugin.cfg.permpegar)){
					if(args.length == 0){
						plugin.core.darSela(p);
						if(plugin.cfg.msgseladada) p.sendMessage(plugin.msg.seladada);
					}else if(args.length == 1){
						try{
							Player pl = Bukkit.getServer().getPlayer(args[0]);
							plugin.core.darSela(pl);
							if(plugin.cfg.msgseladada) p.sendMessage(plugin.msg.seladada);
						}catch(Exception e){
							p.sendMessage(plugin.msg.jogadoroff);
						}
					}
				}else p.sendMessage(plugin.msg.semperm);
				return true;
			}
		}
		return false;
	}

	public void iniciarComandos(){
		plugin.getCommand("sela").setExecutor(this);
	}
}
