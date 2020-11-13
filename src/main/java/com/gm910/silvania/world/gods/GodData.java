package com.gm910.silvania.world.gods;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.gm910.silvania.SilvaniaMod;
import com.gm910.silvania.api.util.GMNBT;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GodData extends WorldSavedData {

	public static final String NAME = SilvaniaMod.MODID + "_gods";

	private MinecraftServer server;

	private Map<String, Deity> gods = new HashMap<>();

	/**
	 * Whether the world has just been created
	 */
	private boolean worldCreate = true;

	@SuppressWarnings("unchecked")
	public static final Set<Function<GodData, Deity>> DEITY_INITIALIZERS = Sets.newHashSet();

	public GodData(String name) {
		super(name);
		MinecraftForge.EVENT_BUS.register(this);

	}

	public GodData() {
		this(NAME);
	}

	public void initialize() {
		if (!worldCreate)
			return;
		worldCreate = false;
		this.addGods(DEITY_INITIALIZERS.stream().map((e) -> e.apply(this)).toArray((m) -> new Deity[m]));
		// TODO
	}

	@SubscribeEvent
	public void onCreate(WorldEvent.Load event) {
		this.initialize();
	}

	public MinecraftServer getServer() {
		return server;
	}

	public void addGods(Deity... deities) {
		for (Deity deity : deities) {
			this.gods.put(deity.getName(), deity);
			MinecraftForge.EVENT_BUS.register(deity);
		}
	}

	public void removeGods(Deity... deities) {
		for (Deity deity : deities) {
			this.gods.remove(deity.getName(), deity);
			MinecraftForge.EVENT_BUS.unregister(deity);
		}
	}

	public Collection<Deity> getGods() {
		return gods.values();
	}

	public Deity byName(String name) {
		return gods.get(name);
	}

	@Override
	public void read(CompoundNBT nbt) {
		gods.clear();
		gods.putAll(GMNBT.createMap((ListNBT) nbt.get("Gods"), (inbt) -> {
			Deity d = new Deity(this, (CompoundNBT) inbt);
			return Pair.of(d.getName(), d);
		}));
		worldCreate = nbt.getBoolean("WorldCreate");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		CompoundNBT nbt = new CompoundNBT();
		ListNBT list = GMNBT.makeList(gods.values(), (entry) -> {
			return entry.serialize();
		});
		nbt.put("Gods", list);
		nbt.putBoolean("WorldCreate", worldCreate);
		return nbt;
	}

	public static GodData get(MinecraftServer server) {
		DimensionSavedDataManager dimdat = server.getWorld(DimensionType.OVERWORLD).getSavedData();
		return dimdat.getOrCreate(() -> {
			GodData dat = new GodData();
			dat.server = server;
			MinecraftForge.EVENT_BUS.register(dat);
			return dat;
		}, NAME);
	}

}
