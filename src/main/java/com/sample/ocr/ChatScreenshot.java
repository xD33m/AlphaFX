package com.sample.ocr;

import com.sample.chat.ImgResize;
import com.sample.ui.mainPanel.snipTool.SelectionPane;
import com.sun.jna.Memory;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser;
import net.sourceforge.tess4j.util.ImageHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ChatScreenshot {


    public BufferedImage capture(WinDef.HWND hWnd) throws IOException {

        User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_SHOWNOACTIVATE);
//        User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_MAXIMIZE);

        HDC hdcWindow = User32Extra.INSTANCE.GetDC(hWnd);
        HDC hdcMemDC = GDI32Extra.INSTANCE.CreateCompatibleDC(hdcWindow);
        Rectangle rectangle = new SelectionPane().getRectangleBounds();
//        System.out.println(rectangle);

        RECT bounds = new RECT();
        User32Extra.INSTANCE.GetClientRect(hWnd, bounds);
        int x;
        int y;
        int width;
        int height;
        if (rectangle != null) {
            x = (int) rectangle.getX();
            y = (int) rectangle.getY();
            width = (int) rectangle.getWidth();
            height = (int) rectangle.getHeight();
        } else {
            System.out.println("No chat area rectangle drawn");
            return null;
        }

        HBITMAP hBitmap = GDI32Extra.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height);

        HANDLE hOld = GDI32Extra.INSTANCE.SelectObject(hdcMemDC, hBitmap);
        GDI32Extra.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, x, y - 23, WinGDIExtra.SRCCOPY);

        GDI32Extra.INSTANCE.SelectObject(hdcMemDC, hOld);
        GDI32Extra.INSTANCE.DeleteDC(hdcMemDC);

        BITMAPINFO bmi = new BITMAPINFO();
        bmi.bmiHeader.biWidth = width;
        bmi.bmiHeader.biHeight = -height;
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = WinGDIExtra.BI_RGB;

        Memory buffer = new Memory(width * height * 4);
        GDI32Extra.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, WinGDIExtra.DIB_RGB_COLORS);
        // ToDo ziemlich unmständlich
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width);
        BufferedImage captureGrey = ImageHelper.convertImageToGrayscale(image);
        File img = new File("File.png");
        ImageIO.write(captureGrey, "png", img);

        File resizedImg = new File("File4000.png");
        new ImgResize().resizeImage("File.png", "400%", "4000x", "File4000.png");
        Image image1 = ImageIO.read(resizedImg.getAbsoluteFile());
        BufferedImage buffResizedImg = (BufferedImage) image1;

        GDI32Extra.INSTANCE.DeleteObject(hBitmap);
        User32Extra.INSTANCE.ReleaseDC(hWnd, hdcWindow);

        return buffResizedImg;
    }
}