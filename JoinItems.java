package me.SrBigote.lobbymg;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class JoinItems implements Listener{

	public static void giveItems(Player jugador) {
		
		ItemStack selector = new ItemStack(Material.COMPASS);
		ItemMeta selectormeta = selector.getItemMeta();
		selectormeta.setDisplayName(ChatColor.GREEN + "Servidores " + ChatColor.GRAY + "(Click Derecho)");
		ArrayList<String> lore = new ArrayList<>();
		lore.add("line 1");
		lore.add("line 2");
		selectormeta.setLore(lore);
		selector.setItemMeta(selectormeta);
		
		Inventory inventory = jugador.getInventory();
		inventory.clear();
		inventory.setItem(0, selector);
	}
	
}
