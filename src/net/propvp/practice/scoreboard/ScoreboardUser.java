package net.propvp.practice.scoreboard;

import org.bukkit.entity.Player;

import net.propvp.practice.timing.ManualTimer;

public class ScoreboardUser {
	
    private ManualTimer enderpearlTimer;
    private ManualTimer combatTimer;
    private Player player;
    
    public ScoreboardUser(Player player) {
        this.enderpearlTimer = new ManualTimer(false);
        this.combatTimer = new ManualTimer(false);
        this.player = player;
    }
    
    public Player asPlayer() {
        return this.player;
    }
    
    public ManualTimer getEnderpearlTimer() {
        return this.enderpearlTimer;
    }
    
    public ManualTimer getCombatTimer() {
        return this.combatTimer;
    }
    
    public boolean hasAnyActiveTimers() {
        return this.enderpearlTimer.isActive() || this.combatTimer.isActive();
    }

}