package org.example.task1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.task2.Comment;
import org.example.task2.Post;
import org.example.task3.ToDos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


public class TypicodeUtils {
    private static final String GET_ALL_TODOS_URI = "https://jsonplaceholder.typicode.com/users/X/todos";
    private static final String GET_USER_BY_USERNAME_URI = "https://jsonplaceholder.typicode.com/users?username={username}";
    private static final String GET_USER_BY_ID_URI = "https://jsonplaceholder.typicode.com/users/{id}";
    private static final String GET_ALL_POSTS_URI = "https://jsonplaceholder.typicode.com/users/1/posts";
    private static final String GET_ALL_COMMENTS_URI = "https://jsonplaceholder.typicode.com/posts/10/comments";
    private static final Gson GSON = new Gson();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();


    public static User sendGetById(int id){
        String getbyId = GET_USER_BY_ID_URI.replace("{id}",String.valueOf(id));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getbyId))
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return GSON.fromJson(response.body(),User.class);
    }

    public static User sendGetByUsername(String username) {
        String getbyUsername = GET_USER_BY_USERNAME_URI.replace("{username}",username);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getbyUsername))
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
         List<User> a = GSON.fromJson(response.body(), new TypeToken<List<User>>(){}.getType());
        User user;
        try {
            user = a.get(0);
        }catch (IndexOutOfBoundsException iobex){
            return null;
        }
        return user;
    }

    public static User sendPost(URI uri,User user){
        String requestBody = GSON.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-type","application/json")
                .build();
        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return GSON.fromJson(response.body(), User.class);
    }

    public static int sendDelete(URI uri){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .header("Content-type","application/json")
                .build();
        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static User sendPut(URI uri,User user) {
        String requestBody = GSON.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-type","application/json")
                .build();
        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return GSON.fromJson(response.body(), User.class);
    }
    public static <T> List<T> sendGetALL(URI uri, Type classtype){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return (GSON.fromJson(response.body(), classtype));
    }
    public static void printCommentsInFile(String filePath){
        List<Post> posts = TypicodeUtils.sendGetALL(URI.create(GET_ALL_POSTS_URI), new TypeToken<List<Post>>(){}.getType());

        Optional<Post> lastPost = posts.stream().max(Comparator.comparingInt(Post::getId));
        List<Comment> comments = TypicodeUtils.sendGetALL(URI.create(GET_ALL_COMMENTS_URI), new TypeToken<List<Comment>>(){}.getType());
        if (lastPost.isEmpty()){
            throw new NoSuchElementException("post not found");
        }

        filePath = filePath.replace("X",String.valueOf(lastPost.get().getUserId()));
        filePath = filePath.replace("Y",String.valueOf(lastPost.get().getId()));

        File file = new File(filePath);
        makeFile(file);
        StringBuilder sb = new StringBuilder();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            sb.append(gson.toJson(comments));
            bw.write(sb.toString());
        } catch (IOException ioEx){
            ioEx.printStackTrace();
        }
    }
    public static void printOpenTodos(int id){
        String getAllToDosURI = GET_ALL_TODOS_URI.replace("X", String.valueOf(id));
        List<ToDos> todos = sendGetALL(URI.create(getAllToDosURI), new TypeToken<List<ToDos>>(){}.getType());
        List<ToDos> opentodos = todos.stream().filter(todo -> !todo.isCompleted()).collect(Collectors.toList());
        System.out.println(opentodos);
    }
    public static void makeFile(File file){
        if (!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
