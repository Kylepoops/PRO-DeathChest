package dev.drawethree.deathchestpro.utils;

import org.bukkit.Location;

public class LocationUtil {

    public static Location getCenter(Location loc) {
        return loc.clone().add(0.5, 0.0, 0.5);
    }


}
