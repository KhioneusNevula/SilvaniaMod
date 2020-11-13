package com.gm910.silvania.world.gods;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.gm910.silvania.api.util.ServerPos;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ActionForDeity {

	private int points;
	private UUID doer;
	private Deity deity;
	private TranslationTextComponent message;
	@Nullable
	private BiConsumer<Deity, LivingEntity> action;

	public ActionForDeity(int pointsGainedOrLost, UUID forWhom, Deity god, TranslationTextComponent message,
			@Nullable BiConsumer<Deity, LivingEntity> rewardOrPunishment) {
		this.points = pointsGainedOrLost;
		this.doer = forWhom;
		this.deity = god;
		this.message = message;
		this.action = rewardOrPunishment;
	}

	public Deity getDeity() {
		return deity;
	}

	public UUID getDoer() {
		return doer;
	}

	public LivingEntity getDoerEntity() {
		LivingEntity en = (LivingEntity) ServerPos.getEntityFromUUID(doer, deity.getData().getServer());
		if (en == null)
			throw new IllegalStateException(doer + " does not exist as a worshiper of " + deity);
		return en;
	}

	public TranslationTextComponent getMessage() {
		return message;
	}

	public void doAction() {
		LivingEntity entity = getDoerEntity();

		this.action.accept(deity, entity);
	}

	public void doAllActions() {
		getDoerEntity().sendMessage(
				deity.getDeityStyleTextComponent().appendSibling(new StringTextComponent("[" + deity.getName() + "] "))
						.applyTextStyle(TextFormatting.RESET).appendSibling(getMessage()));
		Objects.requireNonNull(deity.getReligion(getDoer()), deity + " has no follower " + this.getDoerEntity())
				.changeWorshipPoints(getPoints());
		this.doAction();
	}

	public int getPoints() {
		return points;
	}
}
