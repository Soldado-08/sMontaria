package me.soldado.montaria;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Core implements Listener {

	public Main plugin;
	
	public Core(Main plugin)
	{
		this.plugin = plugin;
	}
	
	ArrayList<Horse> cavalos = new ArrayList<Horse>();
	ArrayList<Player> chamando = new ArrayList<Player>();
	ArrayList<Player> delay = new ArrayList<Player>();
	
	@EventHandler
	public void droparItem(PlayerDropItemEvent event){
		
		Player p = event.getPlayer();
		if(p.getVehicle() instanceof Horse || chamando.contains(p)){
			if(checkSela(event.getItemDrop().getItemStack())) event.setCancelled(true);
		}
		
		
	}
	
	@EventHandler
	public void domar(PlayerInteractEntityEvent event){
		if(event.getRightClicked() instanceof Horse){
			if(!plugin.cfg.domar) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void clickInventario(InventoryClickEvent event){
		HumanEntity entity = event.getWhoClicked();
		if ((entity == null) || (!(entity instanceof Player))) return;
		if (event.getClickedInventory() == null) return;
		if ((event.getClickedInventory() instanceof HorseInventory)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void morte(EntityDeathEvent event){
		if(event.getEntity() instanceof Horse){
			Horse h = (Horse) event.getEntity();
			if(cavalos.contains(h)){
				h.getInventory().setSaddle(null);
				h.getInventory().setArmor(null);
				h.getInventory().setContents(null);
				event.getDrops().clear();
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void danoqueda(EntityDamageEvent event){
		if(event.getEntity() instanceof Horse){
			Horse h = (Horse) event.getEntity();
			if(cavalos.contains(h)){
				if(!plugin.cfg.danoqueda) event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void dano(EntityDamageEvent event){
		if(event.getEntity() instanceof Horse){
			Horse h = (Horse) event.getEntity();
			if(cavalos.contains(h)){
				if(event.isCancelled()) return;
				if(!plugin.cfg.remover) return;
				cavalos.remove(h);
				h.remove();
			}
		}
	}

	//if(event.getCause().equals(DamageCause.FALL) && !plugin.cfg.danoqueda) return;
	@EventHandler
	public void sairDaMontaria(VehicleExitEvent event){
		if(event.getVehicle() instanceof Horse){
			Horse h = (Horse) event.getVehicle();
			if(cavalos.contains(h)){
				cavalos.remove(h);
				h.remove();
			}
		}
	}

	@EventHandler
	public void andar(PlayerMoveEvent event) {
		if(!plugin.cfg.andar){
			if (event.getTo().getBlockX() == event.getFrom().getBlockX() && 
					event.getTo().getBlockY() == event.getFrom().getBlockY() && 
					event.getTo().getBlockZ() == event.getFrom().getBlockZ()) return;
			Player p = event.getPlayer();
			if (chamando.contains(p)) {
				chamando.remove(p);
				p.sendMessage(plugin.msg.cancelou);
			}
		}
	}
	
	@EventHandler
	public void chamarMontaria(PlayerInteractEvent event){
		Action ac = event.getAction();
		if(!(ac.equals(Action.RIGHT_CLICK_AIR) || ac.equals(Action.RIGHT_CLICK_BLOCK))) return;
		Player p = event.getPlayer();
		ItemStack item = p.getItemInHand();
		if(item == null || item.getType() == Material.AIR || !(checkSela(item))) return;
		Location loc = p.getLocation();
		if(p.getVehicle() instanceof Horse) return;
		if(p.hasPermission(plugin.cfg.permchamar)){
			if(!delay.contains(p)){
				tempoMontaria(p, loc);
				delay.add(p);
				new BukkitRunnable(){
					public void run(){
						delay.remove(p);
					}
				}.runTaskLater(plugin, plugin.cfg.tempodelay * 20L);
			}
		}else p.sendMessage(plugin.msg.semperm);
	}
	
	private void tempoMontaria(Player p, Location loc){

		boolean delay = plugin.cfg.delay;
		int tempodelay = plugin.cfg.tempodelay;
		boolean mensagem = plugin.cfg.mensagemsummon;
		if(!delay){
			spawnarMontaria(p, loc);
		}else{
			chamando.add(p);
			p.sendMessage(plugin.msg.chamando);
			for(int i = 0; i <= tempodelay; i++){
				int tempo = i;
				new BukkitRunnable(){
					public void run(){
						if(p.isOnline() && p.getLocation().getWorld()
								.equals(loc.getWorld()) && chamando.contains(p)){
							int rest = tempodelay - tempo;
							if(mensagem) p.sendMessage(plugin.msg.temporestante.replace("%tempo%", rest+""));
							if(tempo == tempodelay) spawnarMontaria(p, loc);
						}
					}
				}.runTaskLater(plugin, i * 20L);
			}
		}
	}
	
	private void spawnarMontaria(Player p, Location loc){
		boolean mensagem = plugin.cfg.mensagemsummon;
		Horse h = (Horse) loc.getWorld().spawnEntity(loc, EntityType.HORSE);
		h.setCustomName(plugin.cfg.nomemontaria.replace("%jogador%", p.getName()));
		h.setAdult();
        h.setVariant(Variant.HORSE);
        h.setTamed(true);
        h.setOwner(p);
        h.setAgeLock(true);
        h.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        h.setPassenger(p);
        cavalos.add(h);
        chamando.remove(p);
        if(mensagem) p.sendMessage(plugin.msg.chegou);
	}
	
	public void darSela(Player p){
		ItemStack item = new ItemStack(Material.SADDLE);
		ItemMeta itemm = item.getItemMeta();
		itemm.setDisplayName(plugin.cfg.nome);
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.BLACK + "Item original"); //Sabe como é né. Melhor adicionar uma segurança =P
		itemm.setLore(lore); //Senão o cara poderia renomear uma sela normal, dependendo do servidor.
		item.setItemMeta(itemm);
		if(p.getInventory().firstEmpty() == -1){
			Location loc = p.getLocation();
			loc.getWorld().dropItem(loc, item);
		}else{
			p.getInventory().addItem(item);
			p.updateInventory();
		}
	}
	
	private boolean checkSela(ItemStack item){
		if(item == null || item.getType() == Material.AIR) return false;
		if(!(item.hasItemMeta() && item.getItemMeta().hasDisplayName())) return false;
		if(item.getItemMeta().getLore().size() != 1) return false;
		String nome = ChatColor.stripColor(item.getItemMeta().getDisplayName());
		String lore = ChatColor.stripColor(item.getItemMeta().getLore().get(0));
		if(nome.equals("Sela") && lore.equals("Item original")) return true;
		else return false;
	}
	
	public void removerCavalos(){
		for(Horse h : cavalos) h.remove();
	}
	
	public void registrar(){
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
}
