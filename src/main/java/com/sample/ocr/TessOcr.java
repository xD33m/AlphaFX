package com.sample.ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class TessOcr {

    private static TessOcr instance = new TessOcr();
    private String ocrText;
    private Rectangle rectangle;
    private int secsBewtweenScreenshots = 10;


    private TessOcr() {
    }

    public static TessOcr getInstance() {
        return instance;
    }

    public void startOcr(Rectangle rectangle) {

        while (true) {
            try {
                takeScreenshot(rectangle);
                Thread.sleep(secsBewtweenScreenshots * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void takeScreenshot(Rectangle rectangle) {
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        instance.setDatapath(tessDataFolder.getAbsolutePath());

        try {
            Rectangle screenRect = new Rectangle(rectangle);
            BufferedImage capture = new Robot().createScreenCapture(screenRect);

//            ImageIO.write(capture, "png", new File("Test.png"));

            String result = instance.doOCR(capture);
            System.out.println(result);
            setOcrText(result);

        } catch (AWTException e) {
            e.printStackTrace();
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }


    public String getOcrText() {
        return ocrText;
    }

    public void setOcrText(String ocrText) {
        this.ocrText = ocrText;
    }
}
