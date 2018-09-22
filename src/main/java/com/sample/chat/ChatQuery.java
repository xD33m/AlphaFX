package com.sample.chat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

import static com.sample.Main.*;


public class ChatQuery implements Runnable {

    private static boolean done = false;


    private static void saveToFile(HashSet<String> hashSet, String fileName) throws IOException {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(APPDATATEXTPATH + fileName, true));
             Scanner scanner = new Scanner(new File(APPDATATEXTPATH + fileName))) {
            boolean postExists = false;
            for (String s : hashSet) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    System.out.println("Scanner line: " + line);
                    System.out.println("HashSet line: " + s);
                    if ((StringSimilarity.similarity(line.trim(), s.trim())) > 0.9) {
                        System.out.println("The message " + s + "// is already in file");
                        postExists = true;
                        break;
                    }
                    postExists = false;
                }
                scanner.reset();
//                System.out.println("Post exists: "+postExists);
                if (!postExists) { // if post does not exists & msg is not on the blacklist.
                    File blacklist = new File(BlacklistPATH);
                    if (blacklist.exists() && !blacklist.isDirectory()) {
                        if (!FileUtils.readFileToString(blacklist, "UTF-8").contains(s)) {
                            System.out.println(s + "// not on blacklist & not in file -> add");
                            br.write("\r\n");
                            br.write(s.trim());
                        }
                    } else {
                        System.out.println("No blacklist found & post is not in file -> add");
                        br.write("\r\n");
                        br.write(s.trim());
                    }
                }
            }
        }
    }

    private void queryItemsToBuy() throws IOException {
        File wtbtxt = new File(WTBPATH);
        if (!wtbtxt.isFile() && wtbtxt.createNewFile() && !wtbtxt.exists()) {
            throw new IOException("Error creating new file: " + wtbtxt.getAbsolutePath());
        }
        try (BufferedReader wtbBr = new BufferedReader(new FileReader(wtbtxt));
             RandomAccessFile chatBr = new RandomAccessFile(ChatpostsPATH, "rwd")) {
            HashSet<String> itemList = new HashSet<>();
            String item;
            while ((item = wtbBr.readLine()) != null) {
                if (item.trim().equals("")) {
                    continue;
                }
                itemList.add(item);
            }
            HashSet<String> sellingList;
            for (String s : itemList) {
                sellingList = new HashSet<>();
//                System.out.println(s);
                chatBr.seek(0);
                String chatMsg;
                while ((chatMsg = chatBr.readLine()) != null) {
                    if (StringUtils.containsAny(chatMsg.toLowerCase(), new String[]{"sell", "s>", "wts", "selling"}) && StringUtils.contains(chatMsg.toLowerCase(), s.toLowerCase())) {
                        String playerName = StringUtils.substringBetween(chatMsg, ")", ":");
                        sellingList.add(playerName.trim() + " is selling: " + s);
                    }
                }
                if (!sellingList.isEmpty()) {
                    System.out.println(sellingList);
                    saveToFile(sellingList, "PlayerSells.txt");
                }
            }
        }
    }

    private void queryItemsToSell() throws IOException {
        File wtstxt = new File(WTSPATH);
        if (!wtstxt.isFile() && wtstxt.createNewFile() && !wtstxt.exists()) {
            throw new IOException("Error creating new file: " + wtstxt.getAbsolutePath());
        }
        try (BufferedReader wtbBr = new BufferedReader(new FileReader(wtstxt));
             RandomAccessFile chatBr = new RandomAccessFile(ChatpostsPATH, "rwd")) {
            HashSet<String> itemList = new HashSet<>();
            String item;
            while ((item = wtbBr.readLine()) != null) {
                if (item.trim().equals("")) {
                    continue;
                }
                itemList.add(item);
            }
            HashSet<String> buyingList;
            for (String s : itemList) {
                buyingList = new HashSet<>();
//                System.out.println(s);
                chatBr.seek(0);
                String chatMsg;
                while ((chatMsg = chatBr.readLine()) != null) {
                    String playerName = StringUtils.substringBetween(chatMsg, ")", ":");
                    if (StringUtils.containsAny(chatMsg.toLowerCase(), new String[]{"buy", "b>", "wtb", "buying"}) && StringUtils.contains(chatMsg.toLowerCase(), s.toLowerCase())) { // (if msg contains "sell, etc" && msg contains "item") && is not in blacklist
                        buyingList.add(playerName.trim() + " is buying: " + s);
                    }
                }
                if (!buyingList.isEmpty()) {
                    System.out.println(buyingList);
                    saveToFile(buyingList, "PlayerBuys.txt");
                }
            }
        }
    }

    public static void setDone() {
        done = true;
    }

    @Override
    public void run() {
        try {
            while (!done) {
                queryItemsToSell();
                Thread.sleep(500);
                queryItemsToBuy();
                Thread.sleep(500);
            }
            done = false;
        } catch ( InterruptedException |IOException e) {
            System.out.println("Thread interrupted");
            e.printStackTrace();
            done = false;
        }
    }
}