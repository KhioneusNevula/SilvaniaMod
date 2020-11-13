package com.gm910.silvania.world.gods;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.gm910.silvania.api.util.ServerPos;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class Deity {

	private UUID avatar;
	private GodData data;
	private final String name;
	private Map<UUID, PersonalReligion> followers = new HashMap<>();
	private TextFormatting[] deityStyleTag = { TextFormatting.BOLD, TextFormatting.GOLD };

	public Deity(GodData data, String name) {
		this.data = data;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Set<UUID> getFollowers() {
		return followers.keySet();
	}

	public Deity setDeityStyleTag(TextFormatting... formats) {
		this.deityStyleTag = formats;
		return this;
	}

	public TextFormatting[] getDeityStyleTag() {
		return deityStyleTag;
	}

	public ITextComponent getDeityStyleTextComponent() {
		return new StringTextComponent("").applyTextStyles(this.deityStyleTag);
	}

	/**
	 * True if no other deity is followed by this entity
	 * 
	 * @param entity
	 * @return
	 */
	public boolean addFollower(UUID entity) {
		for (Deity other : this.data.getGods()) {
			if (other.followers.containsKey(entity)) {
				return false;
			}
		}
		this.followers.put(entity, new PersonalReligion(this, entity));
		return true;
	}

	public void excommunicateFollower(UUID entity) {
		this.followers.remove(entity);
	}

	public Deity(GodData data, CompoundNBT nbt) {
		this(data, nbt.getString("Name"));
		if (nbt.hasUniqueId("Avatar")) {
			this.avatar = nbt.getUniqueId("Avatar");
		}
	}

	public CompoundNBT serialize() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("Name", name);
		if (avatar != null) {
			nbt.putUniqueId("Avatar", avatar);
		}
		return nbt;
	}

	public Entity getAvatar() {
		return ServerPos.getEntityFromUUID(avatar, data.getServer());
	}

	public UUID getAvatarId() {
		return avatar;
	}

	public void setAvatarId(UUID avatar) {
		this.avatar = avatar;
	}

	public GodData getData() {
		return data;
	}

	public void setAvatar(Entity avatar) {
		this.avatar = avatar == null ? null : avatar.getUniqueID();
	}

	public PersonalReligion getReligion(UUID en) {
		return this.followers.get(en);
	}

	public static class DeityType {

		public static final Map<String, DeityType> REGISTERED_GODS = new HashMap<>();

		public DeityType(Function<GodData, Deity> creator, BiFunction<GodData, CompoundNBT, Deity> deserializer,
				Function<Deity, CompoundNBT> serializer) {

		}
	}

}
