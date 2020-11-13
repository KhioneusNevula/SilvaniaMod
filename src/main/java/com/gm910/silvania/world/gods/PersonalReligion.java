package com.gm910.silvania.world.gods;

import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;

public class PersonalReligion {

	private UUID owner;
	private Deity deity;
	private int worshipPoints = 0;

	public PersonalReligion(Deity deity, UUID owner) {
		this.owner = owner;
		this.deity = deity;
	}

	public Deity getDeity() {
		return deity;
	}

	public UUID getOwner() {
		return owner;
	}

	public int getCurrentWorshipPowerRate() {

		return worshipPoints * 100;
	}

	public int getWorshipPoints() {
		return worshipPoints;
	}

	public void setWorshipPoints(int worshipPoints) {
		this.worshipPoints = worshipPoints;
	}

	public void changeWorshipPoints(int worshippoints) {
		this.setWorshipPoints(getWorshipPoints() + worshippoints);
	}

	public CompoundNBT serialize() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("Worship", this.worshipPoints);

		return nbt;
	}

	public PersonalReligion(Deity deity, UUID owner, CompoundNBT data) {
		this(deity, owner);
		// TODO
	}

}
