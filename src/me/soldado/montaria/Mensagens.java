package me.soldado.montaria;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Mensagens {

	public Main plugin;
	
	public Mensagens(Main plugin)
	{
		this.plugin = plugin;
	}
	
	File msgFile;
	FileConfiguration msgs;
	
	String chamando;
	String temporestante;
	String chegou;
	String semperm;
	String jogadoroff;
	String seladada;
	String cancelou;

	private void iniciarValores(){
		chamando = getString("ChamandoMontaria");
		temporestante = getString("TempoRestante");
		chegou = getString("MontariaChegou");
		semperm = getString("SemPermissao");
		jogadoroff = getString("JogadorOfflineInexistente");
		seladada = getString("SelaDada");
		cancelou = getString("MontariaCancelada");
	}
	
	public void iniciarMensagens(){

		if (msgFile == null) {
			msgFile = new File(plugin.getDataFolder(), "mensagens.yml");
		}
		if (!msgFile.exists()) {
			plugin.saveResource("mensagens.yml", false);
		}
		msgs = YamlConfiguration.loadConfiguration(msgFile);
		iniciarValores();
	}
	
	public String getString(String s){
		return msgs.getString(s).replace("&", "§");
	}
}
