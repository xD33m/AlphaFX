package com.sample.ocr;

import com.sample.chat.StringSimilarity;
import com.sample.ocr.imageProcessing.ChatScreenshot;
import com.sample.ui.mainPanel.MainController;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.util.LoadLibs;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;


public class TessOcr implements Runnable {


    private static TessOcr instance = new TessOcr();
    private String ocrText = "Start";
    private Rectangle rectangle;
    private HashSet<String> chatPosts;
    private boolean done = false;


    private TessOcr() {
    }

    public static TessOcr getInstance() {
        return instance;
    }

    public void setDone() {
        done = true;
    }

    @Override
    public void run() {
        try {
            while (!done) {
                startOcr();
                Thread.sleep(5000);
            }
            done = false;
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
            done = false;
        }
    }

    private void startOcr() {
        scanOCRImage();
        saveChatLog();
//                Thread.sleep(3000);
    }

    private void scanOCRImage() {
        String windowName = MainController.getWindowName();
        System.out.println("From TessOcr: " + windowName);
        WinDef.HWND hWnd = User32.INSTANCE.FindWindow(null, windowName);
        try {
            Tesseract1 instance = new Tesseract1();  // JNA Interface Mapping
            File tessDataFolder = LoadLibs.extractTessResources("tessdata");

            instance.setDatapath(tessDataFolder.getAbsolutePath());
//            instance.setLanguage("eng");
//            instance.setDatapath("C:\\Users\\lucas\\AppData\\Local\\Temp\\tess4j\\tessdata");
            BufferedImage bI = new ChatScreenshot().capture(hWnd);
            Thread.sleep(500);
            ocrText = instance.doOCR(bI);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveChatLog() {
        try (BufferedWriter posts = new BufferedWriter(new FileWriter("chatposts.txt", true));
             RandomAccessFile postsReader = new RandomAccessFile("chatposts.txt", "rwd")) {
            HashSet<String> tempSet = splitPosts(getOcrText());
            for (String s : tempSet) {
                Boolean postExists = false;
                postsReader.seek(0);
                while (true) {
                    String line = postsReader.readLine();
//                    System.out.println("Line from postReader.readLine(): " + line + " //Line end");
//                    System.out.println("Line from hashet is: " + s + " //Line end");
                    if (line == null) break;
                    if ((StringSimilarity.similarity(line.trim(), s.trim())) > 0.8) {
//                        System.out.println("The message " + s + "// is already in file");
                        postExists = true;
                        break;
                    }
                    postExists = false;
                }
                if (!postExists) {
//                    System.out.println(s + "// is not in file -> add");
                    // remove \n\r from ocr text & add custom ones.
                    String sToAdd = s.replace("\r", " ");
                    sToAdd = sToAdd.replace("\n", " ");
                    posts.write(sToAdd);
                    posts.write("\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    private HashSet<String> splitPosts(String message) {
        chatPosts = new HashSet<>();
        String[] messages = message.split("\\[[0-9]{2}:[0-9]{2}\\]");
        for (String s : messages) {
            String ts = s.trim();
            chatPosts.add(ts);
        }
        return chatPosts;
    }

    private String getOcrText() {
        return ocrText;
    }

}
