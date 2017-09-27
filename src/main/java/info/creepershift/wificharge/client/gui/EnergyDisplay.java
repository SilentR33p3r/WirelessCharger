package info.creepershift.wificharge.client.gui;

import info.creepershift.wificharge.Reference;
import info.creepershift.wificharge.block.tile.ForgeEnergyImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class EnergyDisplay extends Gui {

    private ForgeEnergyImpl storage;
    private int x;
    private int y;
    private boolean outline;
    private boolean drawTextNextTo;

    private final ResourceLocation location = new ResourceLocation(Reference.MODID, "textures/gui/gui_inventory.png");

    public EnergyDisplay(int x, int y, ForgeEnergyImpl storage, boolean outline, boolean drawTextNextTo){
        this.setData(x, y, storage, outline, drawTextNextTo);
    }

    public EnergyDisplay(int x, int y, ForgeEnergyImpl storage){
        this(x, y, storage, false, false);
    }

    public void setData(int x, int y, ForgeEnergyImpl storage, boolean outline, boolean drawTextNextTo){
        this.x = x;
        this.y = y;
        this.storage = storage;
        this.outline = outline;
        this.drawTextNextTo = drawTextNextTo;
    }

    public void draw(){
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(location);

        int barX = this.x;
        int barY = this.y;

        if(this.outline){
            this.drawTexturedModalRect(this.x, this.y, 52, 163, 26, 93);

            barX += 4;
            barY += 4;
        }
        this.drawTexturedModalRect(barX, barY, 18, 171, 18, 85);

        if(this.storage.getEnergyStored() > 0){
            int i = this.storage.getEnergyStored()*83/this.storage.getMaxEnergyStored();

            float[] color = getWheelColor(mc.world.getTotalWorldTime()%256);
            GlStateManager.color(color[0]/255F, color[1]/255F, color[2]/255F);
            this.drawTexturedModalRect(barX+1, barY+84-i, 36, 172, 16, i);
            GlStateManager.color(1F, 1F, 1F);
        }

        if(this.drawTextNextTo){
            this.drawString(mc.fontRenderer, this.getOverlayText(), barX+25, barY+78, 16777215);
        }
    }

    public void drawOverlay(int mouseX, int mouseY){
        if(this.isMouseOver(mouseX, mouseY)){
            Minecraft mc = Minecraft.getMinecraft();

            List<String> text = new ArrayList<String>();
            text.add(this.getOverlayText());
            GuiUtils.drawHoveringText(text, mouseX, mouseY, mc.displayWidth, mc.displayHeight, -1, mc.fontRenderer);
        }
    }

    private boolean isMouseOver(int mouseX, int mouseY){
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x+(this.outline ? 26 : 18) && mouseY < this.y+(this.outline ? 93 : 85);
    }

    private String getOverlayText(){
        NumberFormat format = NumberFormat.getInstance();
        return String.format("%s/%s RF", format.format(this.storage.getEnergyStored()), format.format(this.storage.getMaxEnergyStored()));
    }

    public static float[] getWheelColor(float pos){
        if(pos < 85.0f){
            return new float[]{pos*3.0F, 255.0f-pos*3.0f, 0.0f};
        }
        if(pos < 170.0f){
            return new float[]{255.0f-(pos -= 85.0f)*3.0f, 0.0f, pos*3.0f};
        }
        return new float[]{0.0f, (pos -= 170.0f)*3.0f, 255.0f-pos*3.0f};
    }

}
