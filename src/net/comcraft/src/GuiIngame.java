package net.comcraft.src;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import net.comcraft.client.Comcraft;

public class GuiIngame extends GuiScreen {

    private GuiInventory guiInventory;
    private Sprite loadingChunksSprite;
    private Image lastLoadingChunksImage;
    private GuiButton screenshotButton;
    private Image pointer;
    private Image slot;
    private Image slot_selection;
    private Image loading_sprite;
    private Image slot_more;

    public GuiIngame(Comcraft cc) {
        super(null);

        this.cc = cc;
        elementsList = new Vector(6);
        guiInventory = new GuiInventory();
        
        pointer = cc.textureProvider.getImage("gui/pointer.png");
        slot = cc.textureProvider.getImage("gui/slot.png");
        slot_selection = cc.textureProvider.getImage("gui/slot_selection.png");
        loading_sprite = cc.textureProvider.getImage("gui/loading_sprite.png");
        if(Touch.isTouchSupported())
        {
            slot_more = cc.textureProvider.getImage("gui/slot_more.png");
        }
    }

    public void drawIngameGui() {
        if (Touch.isTouchSupported() && !Touch.isDragged() && cc.currentScreen == null) {
            for (int i = 0; i < elementsList.size(); ++i) {
                GuiButton guiButtonControl = (GuiButton) elementsList.elementAt(i);
                guiButtonControl.drawButton(null);
            }
        }

        drawFastSlotBar();

        if (!Touch.isTouchSupported() || Keyboard.hasAnyKeyBeenPressed()) {
            drawSelectionImage();
        }

        if (!Touch.isTouchSupported() && cc.settings.screenshotMode) {
            cc.g.setColor(0xFFFFFF);
            drawStringWithShadow(cc.g, cc.langBundle.getText("Ingame.screenshotInfo"), 3, Comcraft.screenHeight - 3, Graphics.LEFT | Graphics.BASELINE);
        }
        
        if (cc.world.chunkProvider.getChunksQueueNum() > 0) {
            drawLoadingChunksImage();
        }
    }

    private void initLoadingChunksImage() {
        lastLoadingChunksImage = loading_sprite;
        loadingChunksSprite = new Sprite(lastLoadingChunksImage, 50, 50);
    }

    private void drawLoadingChunksImage() {
        if (lastLoadingChunksImage != loading_sprite) {
            initLoadingChunksImage();
        }

        if (Touch.isTouchSupported()) {
            loadingChunksSprite.setPosition(cc.screenWidth - 3 - 50, cc.screenHeight - 3 - 50);
        } else {
            loadingChunksSprite.setPosition(cc.screenWidth - 3 - 50, 3);
        }

        loadingChunksSprite.paint(cc.g);

        loadingChunksSprite.nextFrame();
    }

    private void drawSelectionImage() {
        cc.g.drawImage(pointer, cc.screenWidth / 2, cc.screenHeight / 2, Graphics.HCENTER | Graphics.VCENTER);
    }

    private void drawFastSlotBar() {
        if (Touch.isTouchSupported()) {
            int startY = (cc.screenHeight - 4 * GuiButtonMoveControl.getButtonHeight()) / 2;

            for (int i = 0; i < 4; ++i) {
                drawFastSlot(i, cc.screenWidth - 3 - GuiButtonMoveControl.getButtonWidth(), startY + i * GuiButtonMoveControl.getButtonHeight());
            }
        } else {
            int startX = (cc.screenWidth - 3 * GuiButtonMoveControl.getButtonWidth()) / 2;

            for (int i = 0; i < 3; ++i) {
                drawFastSlot(i, startX + i * GuiButtonMoveControl.getButtonWidth(), 3);
            }
        }
    }

    private void drawFastSlot(int slotId, int x, int y) {
        if (slotId == 3) {
            cc.g.drawImage(slot_more, x, y, Graphics.TOP | Graphics.LEFT);
        } else {
            //cc.g.drawImage(cc.textureProvider.getItemTexture(cc.player.inventory.getItemStackAt(slotId).getItem().getIconIndex()), x, y, Graphics.TOP | Graphics.LEFT);
            cc.g.drawImage(cc.player.inventory.elementsImageList[slotId], x, y, Graphics.TOP | Graphics.LEFT);
        }

        cc.g.drawImage(slot, x, y, Graphics.TOP | Graphics.LEFT);

        if (cc.player.inventory.getSelectedElementNum() == slotId) {
            cc.g.drawImage(slot_selection, x - 2, y - 2, Graphics.TOP | Graphics.LEFT);
        }
    }

    public void initIngameGui() {
        initLoadingChunksImage();

        if (Touch.isTouchSupported()) {
            String imageName = "gui/arrow_key.png";

            //LEFT/BACK/RIGHT/FORW
            elementsList.addElement(new GuiButtonMoveControl(cc, this, 0, 0, 0, imageName, Sprite.TRANS_NONE));
            elementsList.addElement(new GuiButtonMoveControl(cc, this, 1, 0, 0 + GuiButtonMoveControl.getButtonHeight(), imageName, Sprite.TRANS_MIRROR_ROT270));
            elementsList.addElement(new GuiButtonMoveControl(cc, this, 2, 0, 0 + GuiButtonMoveControl.getButtonHeight() * 2, imageName, Sprite.TRANS_MIRROR_ROT180));
            elementsList.addElement(new GuiButtonMoveControl(cc, this, 3, 0 + GuiButtonMoveControl.getButtonWidth(), 0 + GuiButtonMoveControl.getButtonHeight(), imageName, Sprite.TRANS_ROT90));
            //DOWN/UP
            elementsList.addElement(new GuiButtonMoveControl(cc, this, 4, 0, cc.screenHeight - GuiButtonMoveControl.getButtonHeight(), imageName, Sprite.TRANS_MIRROR_ROT270));
            elementsList.addElement(new GuiButtonMoveControl(cc, this, 5, 0 + GuiButtonMoveControl.getButtonWidth(), cc.screenHeight - GuiButtonMoveControl.getButtonHeight(), imageName, Sprite.TRANS_ROT90));
            //Fast slot bar
            int startY = (cc.screenHeight - 4 * GuiButtonMoveControl.getButtonHeight()) / 2;

            for (int i = 0; i < 4; ++i) {
                elementsList.addElement(new GuiButtonArea(this, 6 + i, cc.screenWidth - 3 - 50, startY + i * 50, 50, 50));
            }

            screenshotButton = new GuiButtonPictured(cc, this, 10, (int) ((cc.screenWidth - GuiButtonPictured.getButtonWidth()) * 3f / 5), cc.screenHeight - GuiButtonPictured.getButtonHeight(), "gui/button_screenshot.png", Sprite.TRANS_ROT90);
            elementsList.addElement(screenshotButton);

            screenshotButton.drawButton = cc.settings.screenshotMode;
        }
    }

    public void updateScreenshotButton() {
        if (screenshotButton != null) {
            screenshotButton.drawButton = cc.settings.screenshotMode;
        }
    }

    protected void handleGuiAction(GuiButton guiButton) {
        if (!guiButton.enabled) {
            return;
        }

        if (guiButton.getId() == 0) {
            cc.player.moveEntity(1, 0, 0);
        } else if (guiButton.getId() == 1) {
            cc.player.moveEntity(0, 0, 1);
        } else if (guiButton.getId() == 2) {
            cc.player.moveEntity(-1, 0, 0);
        } else if (guiButton.getId() == 3) {
            cc.player.moveEntity(0, 0, -1);
        } else if (guiButton.getId() == 4) {
            cc.player.moveEntity(0, 1, 0);
        } else if (guiButton.getId() == 5) {
            cc.player.moveEntity(0, -1, 0);
        } else if (guiButton.getId() > 5 && guiButton.getId() < 9) {
            cc.player.inventory.setSelectedElement(guiButton.getId() - 6);
        } else if (guiButton.getId() == 9) {
            if (cc.currentScreen != null) {
                cc.displayGuiScreen(null);
            } else {
                cc.displayGuiScreen(guiInventory);
            }
        } else if (guiButton.getId() == 10) {
            takeScreenshot();
        }

        Touch.setInputHandled(true);
    }

    private void takeScreenshot() {
        loadingChunksSprite.setPosition(Comcraft.screenWidth / 2 - 25, Comcraft.screenHeight / 2 - 25);
        loadingChunksSprite.paint(cc.g);

        Image blackImage = cc.textureProvider.getImage("gui/black.png");

        int rows = cc.screenWidth / blackImage.getWidth() + 1;
        int cols = cc.screenHeight / blackImage.getHeight() + 1;

        for (int y = 0; y < cols; ++y) {
            for (int x = 0; x < rows; ++x) {
                cc.g.drawImage(blackImage, x * blackImage.getWidth(), y * blackImage.getHeight(), Graphics.TOP | Graphics.LEFT);
            }
        }

        cc.flushGraphics();

        Image image = cc.render.getScreenshot();

        byte[] data;

        try {
            data = PNGEncoder.imageToPNG(image);
        } catch (IOException ex) {
            //#debug
//#             ex.printStackTrace();

            return;
        }

        Calendar cal = Calendar.getInstance();

        String fileName = "Comcraft_" + cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "_" + cal.get(Calendar.HOUR_OF_DAY) + "-" + cal.get(Calendar.MINUTE) + "-" + cal.get(Calendar.SECOND) + ".png";

        try {
            FileConnection file = (FileConnection) Connector.open(cc.settings.getComcraftFileSystem().getPathToFolder("screenshots/") + fileName, Connector.READ_WRITE);

            if (!file.exists()) {
                file.create();
            }

            OutputStream output = file.openOutputStream();

            output.write(data);
            output.close();

            file.close();
        } catch (IOException ex) {
            //#debug
//#             ex.printStackTrace();
        }
    }

    protected void handleKeyboardInput() {
        if (Keyboard.wasButtonDown(Keyboard.KEY_STAR) || Keyboard.wasButtonDown(Keyboard.KEY_R)) {
            if (cc.currentScreen != null) {
                cc.displayGuiScreen(null);
            } else {
                cc.displayGuiScreen(guiInventory);
            }
        } else if (Keyboard.wasButtonDown(Keyboard.KEY_NUM7) || Keyboard.wasButtonDown(Keyboard.KEY_U)) {
            if (cc.player.inventory.getSelectedElementNum() > 0) {
                cc.player.inventory.setSelectedElement(cc.player.inventory.getSelectedElementNum() - 1);
            } else {
                cc.player.inventory.setSelectedElement(cc.player.inventory.getFastSlotSize() - 1);
            }
        } else if (Keyboard.wasButtonDown(Keyboard.KEY_NUM9) || Keyboard.wasButtonDown(Keyboard.KEY_O)) {
            if (cc.player.inventory.getSelectedElementNum() < cc.player.inventory.getFastSlotSize() - 1) {
                cc.player.inventory.setSelectedElement(cc.player.inventory.getSelectedElementNum() + 1);
            } else {
                cc.player.inventory.setSelectedElement(0);
            }
        } else if (Keyboard.wasButtonDown(Keyboard.KEY_POUND) || Keyboard.wasButtonDown(Keyboard.KEY_Y)) {
            if (cc.currentScreen != null) {
                cc.displayGuiScreen(new GuiQuickMenu());
            } else if (cc.settings.screenshotMode) {
                takeScreenshot();
            }
        }
    }

    private boolean isPointAtFastSlotBox(int x, int y) {
        int startY = (cc.screenHeight - 4 * GuiButtonMoveControl.getButtonHeight()) / 2 - 10;
        int width = GuiButtonMoveControl.getButtonWidth() + 10;
        int height = 4 * GuiButtonMoveControl.getButtonHeight() + 20;

        return x >= cc.screenWidth - width && y >= startY && y <= startY + height;
    }

    protected void handleTouchInput() {
        if (Touch.isPressed() && Touch.wasUnpressed()) {
            touchClicked(Touch.getX(), Touch.getY());
        }
        if (isPointAtFastSlotBox(Touch.getX(), Touch.getY()) && !Touch.isDragged()) {
            Touch.setInputHandled(true);
        }
    }

    public void handleInput() {
        if (Touch.isTouchSupported() && (!Touch.isInputHandled() || cc.currentScreen == null)) {
            handleTouchInput();
        }
        handleKeyboardInput();
    }

    protected void customDrawScreen() {
    }

    protected void initGui() {
    }
}