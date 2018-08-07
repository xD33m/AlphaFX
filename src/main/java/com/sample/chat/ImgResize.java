package com.sample.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ImgResize {

    public boolean resizeImage(String image_path, String quality, String size, String output_image) {
        // absolute path to ImageMagick: Command-line Tools: Convert
        String convert_path = "D:\\Program Files\\ImageMagick-7.0.8-Q16\\convert.exe";

        // Build process to execute convert
        ProcessBuilder pb = new ProcessBuilder(
                convert_path,
                image_path,
                "-quality",
                quality,
                "-resize",
                size,
                output_image);

        pb.redirectErrorStream(true);

        try {
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
