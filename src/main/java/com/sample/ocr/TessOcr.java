package com.sample.ocr;

import com.sample.chat.ImgResize;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.util.ImageHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;



public class TessOcr {


    private static TessOcr instance = new TessOcr();
    private String ocrText = "Start";
    private Rectangle rectangle;
    private HashSet<String> chatPosts;

    private TessOcr() {
    }

    public static TessOcr getInstance() {
        return instance;
    }

    public void startOcr() {
        takeScreenshot(rectangle);
        saveChatLog();
//                Thread.sleep(3000);
    }

    public void takeScreenshot(Rectangle rectangle) {
        Tesseract1 instance = new Tesseract1();  // JNA Interface Mapping
        instance.setLanguage("eng");
        instance.setDatapath("C:\\Users\\lucas\\AppData\\Local\\Temp\\tess4j\\tessdata");

        try {
            BufferedImage capture = new Robot().createScreenCapture(new Rectangle(rectangle));
            BufferedImage captureGrey = ImageHelper.convertImageToGrayscale(capture);

            File img = new File("File.png");
            ImageIO.write(captureGrey, "png", img);
            File resizedImg = new File("File4000.png");
            new ImgResize().resizeImage("File.png", "400%", "4000x", "File4000.png");

            ocrText = instance.doOCR(resizedImg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveChatLog() {
        try (BufferedWriter posts = new BufferedWriter(new FileWriter("chatposts.txt", true));
             RandomAccessFile postsReader = new RandomAccessFile("chatposts.txt", "rwd")) {
            HashSet<String> tempSet = splitPosts(getOcrText());
            Boolean postExists = false;
            for (String s : tempSet) {
                postsReader.seek(0);
                while (true) { // need randomacces?
                    String line = postsReader.readLine();
//                    System.out.println("Line from postReader.readLine(): " + line);
                    if (line == null) break;
                    if ((line.trim()).contains((s.trim()))) {
//                        System.out.println(s + "// is already in file");
                        postExists = true;
                        break;
                    }
                    postExists = false;
                }
                if (!postExists) {
//                    System.out.println(s + "// is not in file -> add");
                    posts.write(s);
                    posts.write("\r\n");
                    postExists = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public HashSet<String> splitPosts(String message) {
        chatPosts = new HashSet<>();
        String[] messages = message.split("\\[[0-9]{2}:[0-9]{2}\\]");
        for (String s : messages) {
            String ts = s.trim();
            chatPosts.add(ts);
        }
        return chatPosts;
    }

    public String getOcrText() {
        return ocrText;
    }
}
