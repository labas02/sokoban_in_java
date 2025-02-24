package com.example.sokoban_in_java;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.input.KeyEvent;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class HelloApplication extends Application {
    HashMap<Integer, HashMap<Integer, displayed_object>> current_map;
    player_position player_pos = new player_position();

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        Button start = new Button("start");
        start.setOnMouseClicked(e -> {
            root.getChildren().removeAll();
            current_map = loadHashMapFromFile("map1.dat");
            player_pos.pos_x = Objects.requireNonNull(current_map).size() / 2;
            player_pos.pos_y = Objects.requireNonNull(current_map).get(0).size() / 2;
            System.out.println(player_pos.pos_x + " " + player_pos.pos_y);
            update_field(current_map, root);
        });

        Button generate_field = new Button("generate_field");
        generate_field.setOnMouseClicked(e -> {
            HashMap map = create_map();
            printMapAsNestedArray(map);
            saveHashMapToFile(map, "map1.dat");
        });

        root.getChildren().addAll(start, generate_field);


        Scene scene = new Scene(root, 320, 240);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            switch (key.getCode()) {
                case W:
                    move_player("w", root);
                    break;
                case S:
                    move_player("s", root);
                    break;
                case A:
                    move_player("a", root);
                    break;
                case D:
                    move_player("d", root);
                    break;
                default:
            }
        });
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public HashMap<Integer, HashMap<Integer, displayed_object>> create_map() {
        // Step 1: Initialize the 2D map
        HashMap<Integer, HashMap<Integer, displayed_object>> map = new HashMap<>();

        // Step 2: Define a sample layout
        // 0 = ground, 1 = point, 2 = box, 3 = player, 4 = wall
        int[][] layout = {
                {4, 4, 4, 4, 4, 4, 4},   // Wall surrounding the map
                {4, 0, 1, 0, 0, 0, 4},   // Ground with a point
                {4, 0, 0, 0, 0, 0, 4},   // Box and player
                {4, 0, 0, 3, 0, 0, 4},   // More ground
                {4, 0, 2, 0, 0, 0, 4},   // More ground
                {4, 0, 0, 0, 0, 0, 4},   // More ground
                {4, 4, 4, 4, 4, 4, 4}    // Bottom wall
        };


        // Step 3: Populate the HashMap with displayed_object instances
        for (int x = 0; x < layout.length; x++) {
            HashMap<Integer, displayed_object> row = new HashMap<>();
            for (int y = 0; y < layout[x].length; y++) {
                // Create a displayed_object with the corresponding type
                displayed_object obj = new displayed_object(layout[x][y]);
                row.put(y, obj);
            }
            map.put(x, row);
        }
        // Step 4: Return the populated map
        return map;
    }

    public void update_field(HashMap<Integer, HashMap<Integer, displayed_object>> map, VBox root) {
        VBox vbox = new VBox(); // VBox for stacking rows
        int maxRow = map.keySet().stream().max(Integer::compareTo).orElse(0);
        int maxCol = map.values().stream()
                .flatMap(innerMap -> innerMap.keySet().stream())
                .max(Integer::compareTo)
                .orElse(0);

        for (int i = 0; i <= maxRow; i++) {
            HBox hbox = new HBox(); // HBox for each row
            for (int j = 0; j <= maxCol; j++) {
                ImageView imageView;
                HashMap<Integer, displayed_object> innerMap = map.get(i);

                if (innerMap != null && innerMap.containsKey(j)) {
                    displayed_object obj = innerMap.get(j);
                    imageView = object_image(obj.which_object);
                } else {
                    // Placeholder for missing objects (you can use a blank or default image)
                    //Image placeholderImage = new Image("path/to/placeholder/image.png");
                    imageView = new ImageView();
                }

                imageView.setFitWidth(64); // Set desired width
                imageView.setFitHeight(64); // Set desired height
                hbox.getChildren().add(imageView); // Add ImageView to the row
            }
            vbox.getChildren().add(hbox); // Add row to VBox
        }

        root.getChildren().clear();
        root.getChildren().addAll(vbox);
    }

    public void move_player(String way, VBox root) {
        System.out.println(current_map.get(player_pos.pos_x).get(player_pos.pos_y).which_object);
        switch (way) {
            case "w":
                switch (current_map.get(player_pos.pos_x - 1).get(player_pos.pos_y).which_object) {
                    case 0:
                        swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x - 1, player_pos.pos_y);
                        break;
                    case 2:
                        if (current_map.get(player_pos.pos_x - 1).get(player_pos.pos_y).isMovable()) {
                            return;
                        }
                        if (current_map.get(player_pos.pos_x - 2).get(player_pos.pos_y).which_object == 0) {

                            swapTiles(player_pos.pos_x - 1, player_pos.pos_y, player_pos.pos_x - 2, player_pos.pos_y);
                            swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x - 1, player_pos.pos_y);
                        }
                        else if (current_map.get(player_pos.pos_x - 2).get(player_pos.pos_y).which_object == 1) {
                            swapTiles(player_pos.pos_x - 1, player_pos.pos_y, player_pos.pos_x - 2, player_pos.pos_y);
                            current_map.get(player_pos.pos_x - 2).get(player_pos.pos_y).setMovable(false);
                            swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x - 1, player_pos.pos_y);
                            current_map.get(player_pos.pos_x).get(player_pos.pos_y).which_object = 0;
                        } else {
                            player_pos.pos_x++;
                        }
                        break;
                    case 4, 1:
                        player_pos.pos_x++;
                        break;
                }
                player_pos.pos_x--;
                break;
            case "s":
                switch (current_map.get(player_pos.pos_x + 1).get(player_pos.pos_y).which_object) {
                    case 0:
                        swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x + 1, player_pos.pos_y);
                        break;
                    case 2:
                        if (current_map.get(player_pos.pos_x + 1).get(player_pos.pos_y).isMovable()) {
                            return;
                        }
                        if (current_map.get(player_pos.pos_x + 2).get(player_pos.pos_y).which_object == 0) {

                            swapTiles(player_pos.pos_x + 1, player_pos.pos_y, player_pos.pos_x + 2, player_pos.pos_y);
                            swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x + 1, player_pos.pos_y);
                        }
                        else if (current_map.get(player_pos.pos_x + 2).get(player_pos.pos_y).which_object == 1) {
                            swapTiles(player_pos.pos_x + 1, player_pos.pos_y, player_pos.pos_x + 2, player_pos.pos_y);
                            current_map.get(player_pos.pos_x + 2).get(player_pos.pos_y).setMovable(false);
                            swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x + 1, player_pos.pos_y);
                            current_map.get(player_pos.pos_x).get(player_pos.pos_y).which_object = 0;
                        } else {
                            player_pos.pos_x--;
                        }
                        break;
                    case 4, 1:
                        player_pos.pos_x--;
                        break;
                }
                player_pos.pos_x++;
                break;
            case "a":
                switch (current_map.get(player_pos.pos_x).get(player_pos.pos_y-1).which_object) {
                    case 0:
                        swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x , player_pos.pos_y-1);
                        break;
                    case 2:
                        if (current_map.get(player_pos.pos_x ).get(player_pos.pos_y-1).isMovable()) {
                            return;
                        }
                        if (current_map.get(player_pos.pos_x).get(player_pos.pos_y-2).which_object == 0) {

                            swapTiles(player_pos.pos_x, player_pos.pos_y-1, player_pos.pos_x, player_pos.pos_y-2);
                            swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x , player_pos.pos_y-1);
                        }
                        else if (current_map.get(player_pos.pos_x).get(player_pos.pos_y-2).which_object == 1) {
                            swapTiles(player_pos.pos_x , player_pos.pos_y-1, player_pos.pos_x, player_pos.pos_y-2);
                            current_map.get(player_pos.pos_x).get(player_pos.pos_y-2).setMovable(false);
                            swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x, player_pos.pos_y-1);
                            current_map.get(player_pos.pos_x).get(player_pos.pos_y).which_object = 0;
                        } else {
                            player_pos.pos_y++;
                        }
                        break;
                    case 4, 1:
                        player_pos.pos_y++;
                        break;
                }
                player_pos.pos_y--;
                break;
            case "d":
                switch (current_map.get(player_pos.pos_x).get(player_pos.pos_y+1).which_object) {
                    case 0:
                        swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x , player_pos.pos_y+1);
                        break;
                    case 2:
                        if (current_map.get(player_pos.pos_x ).get(player_pos.pos_y+1).isMovable()) {
                            return;
                        }
                        if (current_map.get(player_pos.pos_x).get(player_pos.pos_y+2).which_object == 0) {

                            swapTiles(player_pos.pos_x, player_pos.pos_y+1, player_pos.pos_x, player_pos.pos_y+2);
                            swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x , player_pos.pos_y+1);
                        }
                        else if (current_map.get(player_pos.pos_x).get(player_pos.pos_y+2).which_object == 1) {
                            swapTiles(player_pos.pos_x , player_pos.pos_y+1, player_pos.pos_x, player_pos.pos_y+2);
                            current_map.get(player_pos.pos_x).get(player_pos.pos_y+2).setMovable(false);
                            swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x, player_pos.pos_y+1);
                            current_map.get(player_pos.pos_x).get(player_pos.pos_y).which_object = 0;
                        } else {
                            player_pos.pos_y--;
                        }
                        break;
                    case 4, 1:
                        player_pos.pos_y--;
                        break;
                }
                player_pos.pos_y++;
                break;
        }
        update_field(current_map, root);
    }

    public ImageView object_image(int which_object) {
        ImageView img = new ImageView();
        switch (which_object) {
            case 0:
                img.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/ground.png"))));
                break;
            case 1:
                img.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/point.png"))));
                break;
            case 2:
                img.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/crate.png"))));
                break;
            case 3:
                img.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/player.png"))));
                break;
            case 4:
                img.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/wall.png"))));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + which_object);
        }

        return img;
    }

    public static void saveHashMapToFile(HashMap<Integer, HashMap<Integer, displayed_object>> hashMap, String fileName) {
        try {
            // Serialize the HashMap
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(hashMap);
            objectStream.flush();
            byte[] byteArray = byteStream.toByteArray();

            // Save the byte array to a file
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(byteArray);
            fileOutputStream.close();

            System.out.println("HashMap serialized and saved to " + fileName + " successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Integer, HashMap<Integer, displayed_object>> loadHashMapFromFile(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            // Deserialize the HashMap
            HashMap<Integer, HashMap<Integer, displayed_object>> deserializedMap =
                    (HashMap<Integer, HashMap<Integer, displayed_object>>) objectInputStream.readObject();

            objectInputStream.close();
            System.out.println("HashMap deserialized from " + fileName + " successfully.");
            return deserializedMap;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void printMapAsNestedArray(HashMap<Integer, HashMap<Integer, displayed_object>> map) {
        // Get the maximum row and column keys for proper formatting
        int maxRow = map.keySet().stream().max(Integer::compareTo).orElse(0);
        int maxCol = map.values().stream()
                .flatMap(innerMap -> innerMap.keySet().stream())
                .max(Integer::compareTo)
                .orElse(0);

        for (int i = 0; i <= maxRow; i++) {
            System.out.print("[ "); // Start of a row
            for (int j = 0; j <= maxCol; j++) {
                // Get the inner map for the row
                HashMap<Integer, displayed_object> innerMap = map.get(i);
                if (innerMap != null && innerMap.containsKey(j)) {
                    // Fetch the object and display its type
                    System.out.print(innerMap.get(j).which_object + " ");
                } else {
                    // If no object exists at this position, display a placeholder (e.g., -1)
                    System.out.print("-1 ");
                }
            }
            System.out.println("]"); // End of a row
        }
    }

    public void swapTiles(
            int row1, int col1, int row2, int col2) {

        // Check if both tiles exist in the map
        if (current_map.containsKey(row1) && current_map.get(row1).containsKey(col1)
                && current_map.containsKey(row2) && current_map.get(row2).containsKey(col2)) {

            // Get the objects from the specified positions
            displayed_object obj1 = current_map.get(row1).get(col1);
            displayed_object obj2 = current_map.get(row2).get(col2);

            // Swap the objects
            current_map.get(row1).put(col1, obj2);
            current_map.get(row2).put(col2, obj1);

            System.out.println("Swapped (" + row1 + ", " + col1 + ") with (" + row2 + ", " + col2 + ")");
        } else {
            System.out.println("One or both tiles do not exist. Swap failed.");
        }
    }


    public static void main(String[] args) {
        launch();
    }
}

class player_position {
    public int pos_x;
    public int pos_y;
}