package org.example.task2;


import org.example.task1.TypicodeUtils;



public class Task2 {
    public static String filePath = "./src/main/java/files/user-X-post-Y-comments.json";
    public static void main(String[] args) {
       TypicodeUtils.printCommentsInFile(filePath);
    }

}
