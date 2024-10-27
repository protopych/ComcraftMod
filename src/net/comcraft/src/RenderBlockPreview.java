package net.comcraft.src;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.m3g.*;

public final class RenderBlockPreview {

    private RenderBlocks renderBlock;
    private Image[] blockPreviewImages;
    Camera camera;
    Transform blockTrans;
    Transform camTrans;
    Background background;
    Light light;
    
    public RenderBlockPreview(RenderBlocks renderBlock) {
        this.renderBlock = renderBlock;
        //size is 128
        //blockPreviewImages = new Image[24];
    }

    public Image getBlockPreviewImage(int i) {
            Image blockPreviewImage = Image.createImage(GuiButtonMoveControl.getButtonWidth(), GuiButtonMoveControl.getButtonHeight());

            if (Block.blocksList[i] != null) {
                Graphics g = blockPreviewImage.getGraphics();
                Graphics3D g3d = Graphics3D.getInstance();
                g3d.bindTarget(g, true, Graphics3D.ANTIALIAS);
                g3d.resetLights();
                g3d.addLight(light, camTrans);
                g3d.clear(background);
                g3d.setCamera(camera, camTrans);
                renderBlock.renderBlockAllFaces(Block.blocksList[i], 0, 0, 0, blockTrans);
                g3d.releaseTarget();
                
                if (Touch.isTouchSupported()) {
                    Image image = Image.createImage(blockPreviewImage, 0, 0, GuiButtonMoveControl.getButtonWidth(), GuiButtonMoveControl.getButtonHeight(), Sprite.TRANS_ROT90);
                    blockPreviewImage = image;
                }
            }
          return blockPreviewImage;
        //return blockPreviewImages[id];
    }
    
    public void releaseRenderBlockPreview() {
        blockPreviewImages = null;
    }
    
    public void reloadRenderBlockPreview() {
        camera = new Camera();
        camera.setPerspective(50, 1, 1, 200);

        blockTrans = new Transform();
        blockTrans.postTranslate(-10 * 0.625f, -10 * 0.35f, -10);
        blockTrans.postRotate(45, 0, 1, 0);
        blockTrans.postRotate(25, 1, 0, 1);

        camTrans = new Transform();
        camTrans.postTranslate(0, 0, 18);

        background = new Background();
        background.setColor(0xFFFFFF);

        light = new Light();
        light.setMode(Light.DIRECTIONAL);
        light.setIntensity(2.0f);

//        for (int i = 0; i < blockPreviewImages.length; ++i) {
//            blockPreviewImages[i] = Image.createImage(GuiButtonMoveControl.getButtonWidth(), GuiButtonMoveControl.getButtonHeight());
//
//            if (Block.blocksList[i] != null) {
//                Graphics g = blockPreviewImages[i].getGraphics();
//                Graphics3D g3d = Graphics3D.getInstance();
//                g3d.bindTarget(g, true, Graphics3D.ANTIALIAS);
//                g3d.resetLights();
//                g3d.addLight(light, camTrans);
//                g3d.clear(background);
//                g3d.setCamera(camera, camTrans);
//                renderBlock.renderBlockAllFaces(Block.blocksList[i], 0, 0, 0, blockTrans);
//                g3d.releaseTarget();
//                
//                if (Touch.isTouchSupported()) {
//                    Image image = Image.createImage(blockPreviewImages[i], 0, 0, GuiButtonMoveControl.getButtonWidth(), GuiButtonMoveControl.getButtonHeight(), Sprite.TRANS_ROT90);
//                    blockPreviewImages[i] = image;
//                }
//            }
//        }
    }
}
