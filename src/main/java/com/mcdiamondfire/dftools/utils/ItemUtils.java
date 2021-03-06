package com.mcdiamondfire.dftools.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.item.ItemStack;

public class ItemUtils {
	private static final MinecraftClient minecraft = MinecraftClient.getInstance();
	
	public static void setItemInHand(ItemStack itemStack) {
		//Sets the item in the player's main hand.
		minecraft.interactionManager.clickCreativeStack(itemStack, 36 + minecraft.player.inventory.selectedSlot);
	}

	public static void setItemInHotbar(ItemStack itemStack, boolean selectSlot) {
		//If the players main hand is empty, set the item in the player's main hand.
		//Otherwise, find the next open slot and set the item in that slot.
		if (minecraft.player.getMainHandStack().isEmpty()) {
			minecraft.interactionManager.clickCreativeStack(itemStack, 36 + minecraft.player.inventory.selectedSlot);
		} else {
			int firstEmptySlot = minecraft.player.inventory.getEmptySlot();
			
			//If an open slot has been found, set it there, otherwise, set it in the player's main hand.
			if (firstEmptySlot < 9) {
				minecraft.interactionManager.clickCreativeStack(itemStack, 36 + firstEmptySlot);
				if (selectSlot) {
					minecraft.player.inventory.selectedSlot = firstEmptySlot;
					minecraft.player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(firstEmptySlot));
				}
			} else {
				minecraft.interactionManager.clickCreativeStack(itemStack, firstEmptySlot);
			}
		}
	}
}