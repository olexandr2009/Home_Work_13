package org.example.task1;

import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.util.List;

public class Task1 {
    private static final String PUT_USER_URI = "https://jsonplaceholder.typicode.com/users/1";
    private static final String DELETE_USER_URI = "https://jsonplaceholder.typicode.com/users/1";
    private static final String GET_or_POST_ALL_USERS_URI = "https://jsonplaceholder.typicode.com/users";
    public static void main(String[] args) {
        System.out.println(TypicodeUtils.sendGetByUsername("Bret"));

        System.out.println("ByUsername");
        System.out.println();

        User user = TypicodeUtils.sendGetById(4);
        System.out.println(user);
        System.out.println("ById");
        System.out.println();

        System.out.println(TypicodeUtils.sendPost(URI.create(GET_or_POST_ALL_USERS_URI), user));
        System.out.println("Post");
        System.out.println();

        System.out.println(TypicodeUtils.sendGetALL(URI.create(GET_or_POST_ALL_USERS_URI), new TypeToken<List<User>>(){}.getType()));
        System.out.println("allUsers");
        System.out.println();

        System.out.println(TypicodeUtils.sendDelete(URI.create(DELETE_USER_URI)));
        System.out.println("DELETE Statuscode");
        System.out.println();

        System.out.println(user);
        System.out.println(TypicodeUtils.sendPut(URI.create(PUT_USER_URI),user));
    }
}
