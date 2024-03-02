package de.sivery.speedyboats;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;

public class BoatListener implements Listener {
    private final SpeedyBoats plugin;

    public BoatListener(SpeedyBoats plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVehicleDrive(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        List<Entity> passengers = event.getVehicle().getPassengers();
        if (passengers.isEmpty()) {
            return;
        }
        
        Entity passenger = passengers.get(0);
        if (vehicle instanceof Boat boat && passenger instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getItemMeta() == null) {
                return;
            }

            // Get Key
            ItemMeta meta = item.getItemMeta();
            String key = meta.getPersistentDataContainer().get(
                    new NamespacedKey(plugin, "EngineKey"),
                    PersistentDataType.STRING
            );

            // Find Section
            ConfigurationSection section = plugin.config.getConfigurationSection("engines." + key);
            if (section == null) return;
            
            // Update Velocity
            double multiplier = section.getDouble("multiplier");
            Vector direction = boat.getLocation().getDirection();
            
            boat.setVelocity(new Vector(
                    direction.multiply(multiplier).getX(),
                    0.0,
                    direction.multiply(multiplier).getZ()
            ));
        }
    }
}
