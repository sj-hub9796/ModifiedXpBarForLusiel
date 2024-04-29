package net.sjhub.modifiedxpbarforlusiel;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ModifiedXpBarForLusiel.MODID)
public class ModifiedXpBarForLusiel {

    public static final String MODID = "modifiedxpbarforlusiel";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");

    private static final int XP_BAR_WIDTH = 182;
    private static final int XP_BAR_HEIGHT = 5;
    private static final int XP_BAR_POS_Y_OFFSET = -29;
    private static final int TEXT_COLOR_GREEN = 0x00FF00;
    private static final int XP_BAR_TEXTURE_Y = 64; // 경험치 바의 텍스처 Y 위치
    private static final int XP_FILL_TEXTURE_Y = 69; // 채워진 경험치 바의 텍스처 Y 위치

    public ModifiedXpBarForLusiel() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGuiOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.EXPERIENCE_BAR.type()) {
            event.setCanceled(true);
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (shouldRenderXpBar(player)) {
                renderXpBarAndLevel(event);
            }
        }
    }

    private boolean shouldRenderXpBar(LocalPlayer player) {
        return player != null && !player.isCreative() && !player.isSpectator();
    }

    private void renderXpBarAndLevel(RenderGuiOverlayEvent.Pre event) {
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int xpBarPosY = screenHeight + XP_BAR_POS_Y_OFFSET;

        LocalPlayer player = minecraft.player;
        int level = player.experienceLevel;
        int levelPosX = screenWidth / 2 - minecraft.font.width(String.valueOf(level)) / 2;
        int levelPosY = xpBarPosY + 2;

        drawXpBar(guiGraphics, screenWidth, xpBarPosY, player);
        drawLevel(guiGraphics, levelPosX, levelPosY, level);
    }

    private void drawXpBar(GuiGraphics guiGraphics, int screenWidth, int xpBarPosY, LocalPlayer player) {
        int xpBarPosX = screenWidth / 2 - XP_BAR_WIDTH / 2;
        int xp = (int) (player.experienceProgress * XP_BAR_WIDTH);
        guiGraphics.blit(ICONS_LOCATION, xpBarPosX, xpBarPosY, 0, XP_BAR_TEXTURE_Y, XP_BAR_WIDTH, XP_BAR_HEIGHT);
        if (xp > 0) {
            guiGraphics.blit(ICONS_LOCATION, xpBarPosX, xpBarPosY, 0, XP_FILL_TEXTURE_Y, xp, XP_BAR_HEIGHT);
        }
    }

    private void drawLevel(GuiGraphics guiGraphics, int levelPosX, int levelPosY, int level) {
        if (level > 0) {
            Minecraft minecraft = Minecraft.getInstance();
            guiGraphics.drawString(minecraft.font, String.valueOf(level), levelPosX, levelPosY, TEXT_COLOR_GREEN, true);
        }
    }
}
