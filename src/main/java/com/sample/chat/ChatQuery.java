package com.sample.chat;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;


public class ChatQuery implements Runnable {

    private static boolean done = false;

    private static void queryItemsToBuy() throws IOException {
        HashSet<String> sellingList = new HashSet<>();
        try (BufferedReader wtbBr = new BufferedReader(new FileReader("wtb.txt"));
             RandomAccessFile chatBr = new RandomAccessFile("chatposts.txt", "rwd")) {
            Set<String> itemList = new HashSet<>();
            String item;
            while ((item = wtbBr.readLine()) != null) {
                itemList.add(item);
            }
            System.out.println(itemList);
            for (String s : itemList) {
                chatBr.seek(0);
                String chatMsg;
                while ((chatMsg = chatBr.readLine()) != null) {
                    if (StringUtils.containsAny(chatMsg.toLowerCase(), new String[]{"sell", "s>", "wts"}) && StringUtils.contains(chatMsg.toLowerCase(), s.toLowerCase())) {
                        String playerName = StringUtils.substringBetween(chatMsg, ")", ":");
                        String sToAdd = playerName + " is selling: " + s;
//                        System.out.println(playerName + " is selling: " + s);
                        sellingList.add(sToAdd);
                    }
                }
            }
            saveToFile(sellingList, "PlayerSells.txt");
        }
    }

    private static void queryItemsToSell() throws IOException {
        HashSet<String> buyingList = new HashSet<>();
        try (BufferedReader wtbBr = new BufferedReader(new FileReader("wts.txt"));
             RandomAccessFile chatBr = new RandomAccessFile("chatposts.txt", "rwd")) {
            HashSet<String> itemList = new HashSet<>();
            String item;
            while ((item = wtbBr.readLine()) != null) {
                itemList.add(item);
            }
            System.out.println(itemList);
            for (String s : itemList) {
//                System.out.println(s);
                chatBr.seek(0);
                String chatMsg;
                while ((chatMsg = chatBr.readLine()) != null) {
                    if (StringUtils.containsAny(chatMsg.toLowerCase(), new String[]{"buy", "b>", "wts"}) && StringUtils.contains(chatMsg.toLowerCase(), s.toLowerCase())) { // (if msg contains "sell, etc" && msg contains "item")
                        String playerName = StringUtils.substringBetween(chatMsg, ")", ":");
                        buyingList.add(playerName + " is buying: " + s);
//                        System.out.println(playerName + " is buying: " + s);
                    }
                }
            }
            saveToFile(buyingList, "PlayerBuys.txt");
        }
    }

    private static void saveToFile(HashSet<String> hashSet, String fileName) throws IOException {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(fileName, true));
             RandomAccessFile raf = new RandomAccessFile(fileName, "rwd")) {
            for (String s : hashSet) {
                Boolean postExists = false;
                raf.seek(0);
                while (true) {
                    String line = raf.readLine();
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
                    br.write(s);
                    br.write("\r\n");
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
                Thread.sleep(1000);
                queryItemsToBuy();
                Thread.sleep(5000);
            }
            done = false;
        } catch (InterruptedException | IOException e) {
            System.out.println("Thread interrupted");
            e.printStackTrace();
            done = false;
        }
    }
}
