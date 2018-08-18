package com.sample.chat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;


public class ChatQuery implements Runnable {

    private static boolean done = false;


    private static void saveToFile(HashSet<String> hashSet, String fileName) throws IOException {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(fileName, true));
             Scanner scanner = new Scanner(new File(fileName))) {
            boolean postExists = false;
            for (String s : hashSet) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    System.out.println("Scanner line: " + line);
                    System.out.println("Hashset line: " + s);
                    if ((StringSimilarity.similarity(line.trim(), s.trim())) > 0.9) {
                        System.out.println("The message " + s + "// is already in file");
                        postExists = true;
                        break;
                    }
                    postExists = false;
                }
                scanner.reset();
//                System.out.println("Post exists: "+postExists);
                if (!postExists && !FileUtils.readFileToString(new File("Blacklist.txt"), "UTF-8").contains(s)) { // if post does not exists & msg is not on the blacklist.
                    System.out.println(s + "// is not in file -> add");
                    br.write("\r\n");
                    br.write(s.trim());
                }
            }
        }
    }

    private void queryItemsToBuy() throws IOException {
        try (BufferedReader wtbBr = new BufferedReader(new FileReader("wtb.txt"));
             RandomAccessFile chatBr = new RandomAccessFile("chatposts.txt", "rwd")) {
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
        try (BufferedReader wtbBr = new BufferedReader(new FileReader("wts.txt"));
             RandomAccessFile chatBr = new RandomAccessFile("chatposts.txt", "rwd")) {
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
