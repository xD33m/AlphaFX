package com.sample.ocr.imageProcessing;


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
import org.imgscalr.Scalr;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;


public class ChatScreenshot {

    private static final int WS_ICONIC = 0x20000000;
    private static final int WS_MAXIMIZE = 0x01000000;

    public BufferedImage capture(WinDef.HWND hWnd) throws IOException {

        WinUser.WINDOWINFO info = new WinUser.WINDOWINFO();
        User32.INSTANCE.GetWindowInfo(hWnd, info);

        if ((info.dwStyle & WS_ICONIC) == WS_ICONIC) { // if window is minimized
            System.out.println("window is minimized");
            User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_SHOWNOACTIVATE);
        }

        if (!((info.dwStyle & WS_MAXIMIZE) == WS_MAXIMIZE)) { // if window is not maximized
            System.out.println("window is not maximized");
            User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_MAXIMIZE);
        }

        HDC hdcWindow = User32Extra.INSTANCE.GetDC(hWnd);
        HDC hdcMemDC = GDI32Extra.INSTANCE.CreateCompatibleDC(hdcWindow);
        Rectangle rectangle = new SelectionPane().getRectangleBounds();

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

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width);
        image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, image.getWidth() * 2, image.getHeight() * 2, Scalr.OP_ANTIALIAS);
        image = Binarization.GetBmp(image);

        File output400DpiPng = new File(System.getenv("APPDATA") + "\\DofusChat\\text\\", "Output400dpi.png");
        saveImage(image, output400DpiPng, 400);

        BufferedImage image1 = ImageIO.read(output400DpiPng);
//        Image image1 = ImageIO.read(output400DpiPng);
//        BufferedImage buffResizedImg = (BufferedImage) image1;

        GDI32Extra.INSTANCE.DeleteObject(hBitmap);
        User32Extra.INSTANCE.ReleaseDC(hWnd, hdcWindow);

        return image1;
    }


    private void saveImage(BufferedImage sourceImage, File output, int DPI) throws IOException {
        output.delete();

        final String formatName = "png";

        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(formatName); iw.hasNext(); ) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            // up compression quality to from 70% to 100%
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // Needed see javadoc
            writeParam.setCompressionQuality(1.0F); // Highest quality

            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }

            setDPI(metadata, DPI);


            try (ImageOutputStream stream = ImageIO.createImageOutputStream(output)) {
                writer.setOutput(stream);
                writer.write(metadata, new IIOImage(sourceImage, null, metadata), writeParam);
            }
            break;
        }
    }

    private void setDPI(IIOMetadata metadata, int DPI) throws IIOInvalidTreeException {
        double INCH_2_CM = 2.54;

        // for PMG, it's dots per millimeter
        double dotsPerMilli = 1.0 * DPI / 10 / INCH_2_CM;

        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);

        metadata.mergeTree("javax_imageio_1.0", root);
    }
}