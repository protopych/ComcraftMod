package net.comcraft.src;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import net.comcraft.client.Comcraft;

public class GuiButton extends GuiElement {
    final int color0 = 0x2D2D2D;
    final int color1 = 0x636363;
    final int color2 = 0x757FAD;
    final int[] colors = {color0,color1,color2};
    
    private Sprite buttonSprite = null;
    protected Comcraft cc;
    protected int id;
    public int xPos;
    public int yPos;
    public String displayString;
    public boolean enabled;
    public boolean drawButton;
    private Image buttonImage;

    public GuiButton(Comcraft cc, int id, int x, int y, String displayString) {
        this(cc, id, x, y);

        this.displayString = displayString;
        initButtonSprite();
    }

    protected GuiButton(Comcraft cc, int id, int x, int y) {
        this.cc = cc;
        enabled = true;
        drawButton = true;
        this.id = id;
        this.xPos = x;
        this.yPos = y;
    }

    public final int getId() {
        return id;
    }

    public final GuiButton setEnabled(boolean flag) {
        enabled = flag;
        return this;
    }

    protected Image getButtonImage() {
        //return cc.textureProvider.getImage("gui/button.png");
        return null;
    }

    protected final void initButtonSprite() {
        buttonImage = getButtonImage();

        if (buttonImage != null) {
            buttonSprite = new Sprite(buttonImage, getWidth(), getHeight());
        }
    }

    protected int getHooverState(boolean point) {
        int isOver = 1;

        if (point) {
            isOver = 2;
        } else if (!enabled) {
            return 0;
        }

        return isOver;
    }

    public void drawButton(GuiButton selectedButton) {
        if (!drawButton) {
            if (!Touch.isInputHandled() && Touch.isPressed() && checkPoint(Touch.getX(), Touch.getY())) {
                Touch.setInputHandled();
            }
            return;
        }

        if (Touch.isTouchSupported() && getButtonImage() != buttonImage) {
            initButtonSprite();
        }

        boolean flag = false;

        if ((!Touch.isInputHandled() || cc.currentScreen == null) && Touch.isPressed() && checkPoint(Touch.getX(), Touch.getY())) {
            flag = true;
        } else if (selectedButton == this) {
            flag = true;
        }

        int i = getHooverState(flag);

        int y = yPos;

        if (selectedButton != null && selectedButton.yPos + selectedButton.getHeight() > cc.screenHeight) {
            y -= selectedButton.yPos + selectedButton.getHeight() - cc.screenHeight + 10;
        }
        
        if(Touch.isTouchSupported() && buttonSprite != null)
        {
            buttonSprite.setPosition(xPos, y);
            buttonSprite.setFrame(i);
            buttonSprite.paint(cc.g);
        }
        else{
         cc.g.setColor(colors[i]);
         cc.g.fillRect(xPos, y, getWidth(), getHeight());
         cc.g.setColor(0);
         cc.g.drawRect(xPos, y, getWidth(), getHeight());
        }

        drawButtonString(flag, y);
    }

    protected void drawButtonString(boolean flag, int y) {
        if (!enabled) {
            cc.g.setColor(100, 100, 100);
        } else if (flag) {
            cc.g.setColor(255, 255, 0);
        } else {
            cc.g.setColor(220, 220, 220);
        }

        drawStringWithShadow(cc.g, getDisplayString(), xPos + getWidth() / 2, y + getHeight() / 2 - cc.g.getFont().getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
    }

    protected String getDisplayString() {
        return displayString;
    }

    public boolean checkPoint(int x, int y) {
        return enabled && x > xPos && y > yPos && x <= xPos + getWidth() && y <= yPos + getHeight();
    }

    protected int getWidth() {
        return getButtonWidth();
    }

    protected int getHeight() {
        return getButtonHeight();
    }

    public static int getButtonWidth() {
        if (Comcraft.getScreenWidth() == 240) {
            return 180;
        } else if (Comcraft.getScreenWidth() == 360) {
            return 280;
        } else if (Comcraft.getScreenWidth() == 320 && Comcraft.getScreenHeight() == 240) {
            return 180;
        } else if (Comcraft.getScreenWidth() == 320) {
            return 280;
        } else if (Comcraft.getScreenWidth() == 480) {
            return 280;
        } else if (Comcraft.getScreenWidth() == 176) {
            return 160;
        }

        return -1;
    }

    public static int getButtonHeight() {
        if (Comcraft.getScreenWidth() == 240) {
            return 30;
        } else if (Comcraft.getScreenWidth() == 360) {
            return 45;
        } else if (Comcraft.getScreenWidth() == 320 && Comcraft.getScreenHeight() == 240) {
            return 30;
        } else if (Comcraft.getScreenWidth() == 320) {
            return 45;
        } else if (Comcraft.getScreenWidth() == 480) {
            return 45;
        } else if (Comcraft.getScreenWidth() == 176) {
            return 30;
        }

        return -1;
    }
}
