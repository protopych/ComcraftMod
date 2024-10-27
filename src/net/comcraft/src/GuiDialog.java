/*
 * Copyright (C) 2012 Piotr Wójcik
 *
 */

package net.comcraft.src;

import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Piotr Wójcik
 */
public class GuiDialog extends GuiScreen {

    private String displayedString;
    private GuiDialogHost dialogHost;
    
    public GuiDialog(GuiDialogHost dialogHost, String displayedString) {
        super(null);
        
        this.dialogHost = dialogHost;
        this.displayedString = displayedString;
    }
    
    protected void customDrawScreen() {
        cc.g.setColor(0xFFFFFF);
        drawStringInLines(cc.g, displayedString, cc.screenWidth / 2, 20, cc.screenWidth - 50, Graphics.TOP | Graphics.HCENTER);
    }

    protected void initGui() {
        int centerX = (cc.screenWidth - GuiButton.getButtonWidth()) / 2;

        int startY = 120;

        elementsList.addElement(new GuiButton(cc, 0, centerX, startY, cc.langBundle.getText("GuiDialog.buttonOk")));
    }

    protected void handleGuiAction(GuiButton guiButton) {
        dialogHost.dialogAction();
    }
}