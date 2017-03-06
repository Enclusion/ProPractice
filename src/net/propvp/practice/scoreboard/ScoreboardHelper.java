package net.propvp.practice.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Preconditions;

import net.propvp.practice.utils.MessageUtils;

public class ScoreboardHelper {
	
    private List<ScoreboardText> list;
    private Scoreboard scoreBoard;
    private Objective objective;
    private String tag;
    private int lastSentCount;
    
    public ScoreboardHelper(Scoreboard scoreBoard) {
        this.list = new ArrayList<ScoreboardText>();
        this.tag = "PlaceHolder";
        this.lastSentCount = -1;
        this.scoreBoard = scoreBoard;
        (this.objective = this.getOrCreateObjective(this.tag)).setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public ScoreboardHelper(Scoreboard scoreBoard, String title) {
        this.list = new ArrayList<ScoreboardText>();
        this.tag = "PlaceHolder";
        this.lastSentCount = -1;
        Preconditions.checkState(title.length() <= 32, "title can not be more than 32");
        this.tag = ChatColor.translateAlternateColorCodes('&', title);
        this.scoreBoard = scoreBoard;
        (this.objective = this.getOrCreateObjective(this.tag)).setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public void add(String left, String right) {
        Preconditions.checkState(left.length() <= 16, "left can not be more than 16");
        Preconditions.checkState(right.length() <= 16, "right can not be more than 16");
        this.list.add(new ScoreboardText(MessageUtils.color(left), MessageUtils.color(right)));
    }
    
    public void set(int index, String left, String right) {
        Preconditions.checkState(left.length() <= 16, "left can not be more than 16");
        Preconditions.checkState(right.length() <= 16, "right can not be more than 16");
        this.list.set(index, new ScoreboardText(MessageUtils.color(left), MessageUtils.color(right)));
    }
    
    public void clear() {
        this.list.clear();
    }
    
    public void remove(int index) {
        String name = this.getNameForIndex(index);
        this.scoreBoard.resetScores(name);
        Team team = this.getOrCreateTeam(String.valueOf(String.valueOf(ChatColor.stripColor(this.tag))) + index, index);
        team.unregister();
    }
    
    public void update(Player player) {
        player.setScoreboard(this.scoreBoard);
        if (this.lastSentCount != -1) {
            for (int sentCount = this.list.size(), i = 0; i < this.lastSentCount - sentCount; ++i) {
                this.remove(sentCount + i);
            }
        }
        for (int j = 0; j < this.list.size(); ++j) {
            Team team = this.getOrCreateTeam(String.valueOf(String.valueOf(ChatColor.stripColor(this.tag))) + j, j);
            ScoreboardText str = this.list.get(this.list.size() - j - 1);
            team.setPrefix(str.getLeft());
            team.setSuffix(str.getRight());
            this.objective.getScore(this.getNameForIndex(j)).setScore(j + 1);
        }
        this.lastSentCount = this.list.size();
    }
    
    public Team getOrCreateTeam(String team, int i) {
        Team value = this.scoreBoard.getTeam(team);
        if (value == null) {
            value = this.scoreBoard.registerNewTeam(team);
            value.addEntry(this.getNameForIndex(i));
        }
        return value;
    }
    
    public Objective getOrCreateObjective(String objective) {
        Objective value = this.scoreBoard.getObjective("dummyhubobj");
        if (value == null) {
            value = this.scoreBoard.registerNewObjective("dummyhubobj", "dummy");
        }
        value.setDisplayName(objective);
        return value;
    }
    
    public String getNameForIndex(int index) {
        return String.valueOf(String.valueOf(ChatColor.values()[index].toString())) + ChatColor.RESET;
    }
    
    public static class ScoreboardText {
        private String left;
        private String right;
        
        public ScoreboardText(String left, String right) {
            this.left = left;
            this.right = right;
        }
        
        public String getLeft() {
            return this.left;
        }
        
        public void setLeft(String left) {
            this.left = left;
        }
        
        public String getRight() {
            return this.right;
        }
        
        public void setRight(String right) {
            this.right = right;
        }
    }
}
