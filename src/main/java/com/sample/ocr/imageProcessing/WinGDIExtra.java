package com.sample.ocr.imageProcessing;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinGDI;

public interface WinGDIExtra extends WinGDI {

    DWORD SRCCOPY = new DWORD(0x00CC0020);

}
