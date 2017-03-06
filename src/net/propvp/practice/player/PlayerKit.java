package net.propvp.practice.player;

import java.util.UUID;

public class PlayerKit {
	
	private String name;
	private UUID uuid;
	private PlayerInv inv;

	public PlayerKit(String name, PlayerInv inv) {
		this.name = name;
		this.uuid = UUID.randomUUID();
		this.inv = inv;
	}

	public String getName() {
		return name;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerInv getInv() {
		return this.inv;
	}

	public void setInv(PlayerInv inv) {
		this.inv = inv;
	}

}