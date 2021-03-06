package com.mcdiamondfire.dftools.commands;

import com.mcdiamondfire.dftools.utils.ItemUtils;
import com.mcdiamondfire.dftools.utils.MessageUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.arguments.ItemStackArgumentType;

import io.github.cottonmc.clientcommands.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

public class GiveCommand {
    private static final MinecraftClient minecraft = MinecraftClient.getInstance();

    public static int guiSummoned = 0;

    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("dfgive")
        .then(ArgumentBuilders.argument("item", ItemStackArgumentType.itemStack())
            .then(ArgumentBuilders.argument("count", IntegerArgumentType.integer(1, 64))
                .executes(ctx -> execute(ctx, true)))
            .executes(ctx -> execute(ctx, false)))
        .then(ArgumentBuilders.literal("get")
            .executes(ctx -> executeGet(ctx)))
        .then(ArgumentBuilders.literal("codetemplate")
            .executes(ctx -> {
                if (!minecraft.player.isCreative()) {
                    MessageUtils.errorMessage("You need to be in build mode or dev mode to do this!");
                    return 1;
                }
                guiSummoned = 2;
                return 1;
            }))
        .executes(ctx -> {
            if (!minecraft.player.isCreative()) {
                MessageUtils.errorMessage("You need to be in build mode or dev mode to do this!");
                return 1;
            }
            guiSummoned = 1;
            return 1;
        }));

        // Shortcut to /dfgive, same code as above.
        dispatcher.register(ArgumentBuilders.literal("dfg")
        .then(ArgumentBuilders.argument("item", ItemStackArgumentType.itemStack())
            .then(ArgumentBuilders.argument("count", IntegerArgumentType.integer(1, 64))
                .executes(ctx -> execute(ctx, true)))
            .executes(ctx -> execute(ctx, false)))
        .then(ArgumentBuilders.literal("get")
            .executes(ctx -> executeGet(ctx)))
        .then(ArgumentBuilders.literal("codetemplate")
            .executes(ctx -> {
                if (!minecraft.player.isCreative()) {
                    MessageUtils.errorMessage("You need to be in build mode or dev mode to do this!");
                    return 1;
                }
                guiSummoned = 2;
                return 1;
            }))
        .executes(ctx -> {
            if (!minecraft.player.isCreative()) {
                MessageUtils.errorMessage("You need to be in build mode or dev mode to do this!");
                return 1;
            }
            guiSummoned = 1;
            return 1;
        }));
    }

    private static int execute(CommandContext<CottonClientCommandSource> context, boolean useAmount) throws CommandSyntaxException {
        int amount = 1;
        if (useAmount == true) {
            amount = IntegerArgumentType.getInteger(context, "count");
        }

        ItemStack itemStack = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(amount, false);

        //Checks if player is not in survival mode.
        if (!minecraft.player.isCreative()) {
            MessageUtils.errorMessage("You need to be in build mode or dev mode to do this!");
            return 1;
        }

        //Checks if item stack is not air.
		if (itemStack.isEmpty()) {
			MessageUtils.errorMessage("Invalid item!");
            return 1;
		}
        
        //Sends updated item to the server.
        ItemUtils.setItemInHotbar(itemStack, false);
        return 1;
    }

    private static int executeGet(CommandContext<CottonClientCommandSource> context) throws CommandSyntaxException {
        ItemStack itemStack = minecraft.player.getMainHandStack();

        //Checks if player is not in survival mode.
        if (!minecraft.player.isCreative()) {
            MessageUtils.errorMessage("You need to be in build mode or dev mode to do this!");
            return 1;
        }

        //Checks if item stack is not air.
		if (itemStack.isEmpty()) {
			MessageUtils.errorMessage("Invalid item!");
            return 1;
		}

        String itemId = Registry.ITEM.getId(itemStack.getItem()).toString();
        String itemTag = itemStack.getOrCreateTag().toString();

        if (itemTag.equals("{}")) {
            itemTag = "";
        }
        
        //Creates the click and hover events for the message.
		Style messageStyle = new Style();
		messageStyle.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, itemId + itemTag));
		messageStyle.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click here to copy the /give-formatted\nitem data to your clipboard.").formatted(Formatting.BLUE)));
		
		//Creates the actual message text component.
		Text messageText = new LiteralText(itemId + itemTag).formatted(Formatting.BLUE);
		messageText.setStyle(messageStyle);
		
		//Sends the message.
		MessageUtils.infoMessage("/give-formatted item data:");
        minecraft.player.sendMessage(messageText);
        return 1;
    }
}