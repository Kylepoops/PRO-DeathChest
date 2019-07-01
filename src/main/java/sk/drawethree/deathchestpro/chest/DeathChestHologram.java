package sk.drawethree.deathchestpro.chest;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.commands.PlaceholderAPICommands;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import sk.drawethree.deathchestpro.DeathChestPro;
import sk.drawethree.deathchestpro.DeathChestProHook;
import sk.drawethree.deathchestpro.utils.LocationUtil;
import sk.drawethree.deathchestpro.utils.Time;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DeathChestHologram {

    private static final double LINE_SPACER = 0.25;
    private static final double ARMOR_STAND_HEIGHT = 2.0;

    private DeathChest deathChest;
    private Location location;
    private ArrayList<ArmorStand> armorStands;

    public DeathChestHologram(DeathChest deathChest, Location loc) {
        this.deathChest = deathChest;
        this.location = loc;
        this.location.getChunk().load();
        this.armorStands = new ArrayList<>();
        this.inicialize();
    }

    private void inicialize() {
        for (String s : DeathChestPro.getHologramLines()) {
            s = s.replaceAll("%locked%", deathChest.getLockedString())
                    .replaceAll("%player%", deathChest.getOfflinePlayer().getName())
                    .replaceAll("%death_date%", DeathChestPro.getDeathDateFormat().format(new Date()))
                    .replaceAll("%timeleft%", deathChest.getTimeLeft() == -1 ? "∞" : new Time(deathChest.getTimeLeft(), TimeUnit.SECONDS).toString());

            if (DeathChestProHook.PLACEHOLDER_API.isEnabled()) {
                s = PlaceholderAPI.setPlaceholders(deathChest.getOfflinePlayer(), s);
            }

            this.appendTextLine(s);

        }
        this.teleport(LocationUtil.getCenter(this.location.clone().add(0, 0.5 + this.getHeight(), 0)));
    }

    private void removeLine(int lineNumber) {
        this.armorStands.remove(lineNumber).remove();
        this.update();
    }

    public void appendTextLine(String text) {
        ArmorStand as = (ArmorStand) this.location.getWorld().spawnEntity(this.location.clone().subtract(0, this.armorStands.size() * LINE_SPACER, 0), EntityType.ARMOR_STAND);

        as.setGravity(false);
        as.setCanPickupItems(false);
        as.setBasePlate(false);
        as.setCustomName(text);
        as.setCustomNameVisible(true);
        as.setVisible(false);

        this.armorStands.add(as);
        this.update();
    }

    private void update() {
        for (int i = 0; i < this.armorStands.size(); i++) {
            ArmorStand as = this.armorStands.get(i);
            as.teleport(this.location.clone().subtract(0, ARMOR_STAND_HEIGHT + (i * LINE_SPACER), 0));
        }
    }


    private void teleport(Location newLocation) {
        this.location = newLocation;
        this.update();
    }

    public Location getLocation() {
        return location;
    }

    private double getHeight() {
        return this.armorStands.size() * LINE_SPACER;
    }

    public void delete() {
        for (ArmorStand as : this.armorStands) {

            if (!as.getLocation().getChunk().isLoaded()) {
                as.getLocation().getChunk().load();
            }

            as.setCustomName("");
            as.setCustomNameVisible(false);
            as.remove();
        }
    }

    private void setLine(int lineNumber, String text) {
        this.armorStands.get(lineNumber).setCustomName(text);
    }

    public void updateHologram(int timeLeft) {
        for (int i = 0; i < DeathChestPro.getHologramLines().size(); i++) {
            String line = DeathChestPro.getHologramLines().get(i);
            if (line.contains("%timeleft%")) {
                int lineNumber = i;

                /*if (DeathChestPro.isDisplayPlayerHead()) {
                    lineNumber += 1;
                }*/

                this.setLine(lineNumber, line.replaceAll("%timeleft%", timeLeft == -1 ? "∞" : new Time(timeLeft, TimeUnit.SECONDS).toString()));
            } else if (line.contains("%locked%")) {
                this.setLine(i, line.replaceAll("%locked%", deathChest.getLockedString()));
            }
        }
    }

}