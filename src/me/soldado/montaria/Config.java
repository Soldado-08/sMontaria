package me.soldado.montaria;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

	public Main plugin;
	
	public Config(Main plugin)
	{
		this.plugin = plugin;
	}
	
	File configFile;
	FileConfiguration config;
	
	String permchamar;
	String permpegar;
	String nome;
	String nomemontaria;
	int tempodelay;
	boolean delay;
	boolean mensagemsummon;
	boolean sumonar;
	boolean msgseladada;
	boolean andar;
	boolean remover;
	boolean danoqueda;
	boolean domar;

	private void iniciarValores(){
		permchamar = getString("SumonarMontaria");
		permpegar = getString("PegarSela");
		nome = getString("NomeDaSela");
		nomemontaria = getString("NomeMontaria");
		tempodelay = getInt("TempoDelay");
		delay = getBoolean("Delay");
		mensagemsummon = getBoolean("MensagemSummon");
		sumonar = getBoolean("SumonarMontaria");
		msgseladada = getBoolean("SelaEnviada");
		andar = getBoolean("AndarEnquantoChama");
		remover = getBoolean("RemoverMontariaDano");
		danoqueda = getBoolean("DanoDeQueda");
		domar = getBoolean("DomarCavalo");
	}
	
	public void iniciarConfig(){

		if (configFile == null) {
			configFile = new File(plugin.getDataFolder(), "config.yml");
		}
		if (!configFile.exists()) {
			plugin.saveResource("config.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(configFile);
		iniciarValores();
	}
	
	private String getString(String s){
		return config.getString(s).replace("&", "§");
	}
	
	private int getInt(String s){
		return config.getInt(s);
	}
	
	private boolean getBoolean(String s){
		return config.getBoolean(s);
	}
	
}
