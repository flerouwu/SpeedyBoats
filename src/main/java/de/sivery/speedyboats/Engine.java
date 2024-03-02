package de.sivery.speedyboats;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class Engine {
    public static final ArrayList<Engine> REGISTERED = new ArrayList<>();
    private static final List<Component> LORE = Collections.singletonList(
            Component.text("Hold the engine in your main hand so the engine accelerates you and your boat.", NamedTextColor.GRAY)
    );
    public final String key;
    private final ItemStack item;
    private final @Nullable CraftingRecipe recipe;

    public Engine(String key, ItemStack item, @Nullable CraftingRecipe recipe) {
        this.key = key;
        this.item = item;
        this.recipe = recipe;
    }

    public static @Nullable Engine FromConfig(SpeedyBoats plugin, String key, ConfigurationSection section) {
        if (!ValidateSection(section)) {
            Logger.getAnonymousLogger().info("Section wasn't valid!");
            return null;
        }

        // ItemStack Creation
        ItemStack item;
        {
            // Get Section Values
            String rawName = section.getString("name");
            String rawMaterial = section.getString("material");
            assert rawName != null && rawMaterial != null;

            // Parse into Valid Types
            Component name = MiniMessage.miniMessage().deserialize(rawName);
            Material material = Material.matchMaterial(rawMaterial);

            if (material == null) {
                Logger.getAnonymousLogger().info("Material was null (ItemStack)!");
                return null;
            }

            // Construct Item
            item = new ItemStack(material);
            item.addUnsafeEnchantment(Enchantment.LUCK, 42);

            // Update Meta
            ItemMeta meta = item.getItemMeta();
            meta.displayName(name);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.lore(LORE);

            // Store Engine Key
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "EngineKey"),
                    PersistentDataType.STRING,
                    key
            );

            item.setItemMeta(meta);
        }

        // Recipe Creation
        ShapedRecipe recipe;
        {
            List<String> rawRecipe = (List<String>) section.getList("recipe");
            assert rawRecipe != null;

            NamespacedKey recipeKey = new NamespacedKey(plugin, "speedyboats_" + key);
            recipe = new ShapedRecipe(recipeKey, item);
            recipe.shape(rawRecipe.get(0), rawRecipe.get(1), rawRecipe.get(2));

            // Ingredients
            ConfigurationSection ingredients = section.getConfigurationSection("ingredients");
            assert ingredients != null;

            ingredients.getKeys(false).forEach(ingredientKey -> {
                String value = ingredients.getString(ingredientKey);
                if (value == null) return;

                if (value.startsWith("speedyboats:")) {
                    String engineId = value.replaceFirst("speedyboats:", "");
                    Optional<Engine> engine = REGISTERED.stream()
                            .filter(eng -> eng.key.equals(engineId))
                            .findFirst();

                    if (engine.isEmpty()) {
                        return;
                    }

                    RecipeChoice choice = new RecipeChoice.ExactChoice(engine.get().item);
                    recipe.setIngredient(ingredientKey.charAt(0), choice);
                } else {
                    Material material = Material.matchMaterial(value);
                    if (material == null) return;

                    recipe.setIngredient(ingredientKey.charAt(0), material);
                }
            });

            // Add Recipe
            Bukkit.addRecipe(recipe);
        }

        Engine engine = new Engine(key, item, recipe);
        REGISTERED.add(engine);
        return engine;
    }

    private static boolean ValidateSection(ConfigurationSection section) {
        String name = section.getString("name");
        double multiplier = section.getDouble("multiplier", -1);
        String material = section.getString("material");

        List<?> recipe = section.getList("recipe");
        ConfigurationSection ingredients = section.getConfigurationSection("ingredients");

        if (name == null) {
            Logger.getAnonymousLogger().warning("Name was null!");
            return false;
        }
        
        if (multiplier == -1) {
            Logger.getAnonymousLogger().warning("Multiplier was null (or -1)!");
            return false;
        }
        
        if (material == null) {
            Logger.getAnonymousLogger().warning("Material was null!");
            return false;
        }
        
        if (recipe == null) {
            Logger.getAnonymousLogger().warning("Recipe was null!");
            return false;
        }
        
        if (recipe.size() != 3) {
            Logger.getAnonymousLogger().warning("Recipe size wasn't 3!");
            return false;
        }
        
        if (ingredients == null) {
            Logger.getAnonymousLogger().warning("Ingredients was null!");
            return false;
        }
        
        return true;
    }
}
