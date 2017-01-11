package me.soldado.montaria;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	Core core;
	Comandos cmd;
	Config cfg;
	Mensagens msg;
	
	public void onEnable(){
		core = new Core(this);
		cmd = new Comandos(this);
		cfg = new Config(this);
		msg = new Mensagens(this);
		
		cfg.iniciarConfig();
		msg.iniciarMensagens();
		core.registrar();
		cmd.iniciarComandos();

		this.getLogger().info("sMontaria ativado!!!");
		this.getLogger().info("Autor: Soldado_08");
	}
	
	public void onDisable(){
		this.getLogger().info("sMontaria desativado!!!");
		this.getLogger().info("Autor: Soldado_08");
		core.removerCavalos();
	}

}
