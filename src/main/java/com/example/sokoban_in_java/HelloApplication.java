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


        Scene scene = new Scene(root, 800, 800);
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
        HashMap<Integer, HashMap<Integer, displayed_object>> map = new HashMap<>();
        // 0 = ground, 1 = point, 2 = box, 3 = player, 4 = wall
        int[][] layout = {
                {4, 4, 4, 4, 4, 4, 4},
                {4, 0, 1, 0, 0, 0, 4},
                {4, 0, 0, 0, 0, 0, 4},
                {4, 0, 0, 3, 0, 0, 4},
                {4, 0, 2, 0, 0, 0, 4},
                {4, 0, 0, 0, 0, 0, 4},
                {4, 4, 4, 4, 4, 4, 4}
        };

        for (int x = 0; x < layout.length; x++) {
            HashMap<Integer, displayed_object> row = new HashMap<>();
            for (int y = 0; y < layout[x].length; y++) {
                displayed_object obj = new displayed_object(layout[x][y]);

                row.put(y, obj);
            }
            map.put(x, row);
        }
        return map;
    }

    public void update_field(HashMap<Integer, HashMap<Integer, displayed_object>> map, VBox root) {
        VBox vbox = new VBox();
        int maxRow = map.keySet().stream().max(Integer::compareTo).orElse(0);
        int maxCol = map.values().stream()
                .flatMap(innerMap -> innerMap.keySet().stream())
                .max(Integer::compareTo)
                .orElse(0);

        for (int i = 0; i <= maxRow; i++) {
            HBox hbox = new HBox();
            for (int j = 0; j <= maxCol; j++) {
                ImageView imageView;
                HashMap<Integer, displayed_object> innerMap = map.get(i);

                if (innerMap != null && innerMap.containsKey(j)) {
                    displayed_object obj = innerMap.get(j);
                    imageView = object_image(obj.which_object);
                } else {
                    imageView = new ImageView();
                }

                imageView.setFitWidth(64);
                imageView.setFitHeight(64);
                hbox.getChildren().add(imageView);
            }
            vbox.getChildren().add(hbox);
        }

        root.getChildren().clear();
        root.getChildren().addAll(vbox);
    }

    public void move_player(String way, VBox root) {
        System.out.println(current_map.get(player_pos.pos_x).get(player_pos.pos_y).which_object);
        // 0 = ground, 1 = point, 2 = box, 3 = player, 4 = wall
        switch (way) {
            case "w":
                switch (current_map.get(player_pos.pos_x - 1).get(player_pos.pos_y).which_object) {
                    case 0,1:
                        swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x - 1, player_pos.pos_y);
                        break;
                    case 2:
                        if (!current_map.get(player_pos.pos_x - 1).get(player_pos.pos_y).isMovable()) {
                            return;
                        }
                        if (current_map.get(player_pos.pos_x - 2).get(player_pos.pos_y).which_object == 0) {

                            swapTiles(player_pos.pos_x - 1, player_pos.pos_y, player_pos.pos_x - 2, player_pos.pos_y);
                            swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x - 1, player_pos.pos_y);
                        }
                        else if (current_map.get(player_pos.pos_x - 2).get(player_pos.pos_y).which_object == 1) {
                            swapTiles(player_pos.pos_x - 1, player_pos.pos_y, player_pos.pos_x - 2, player_pos.pos_y);
                            swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x - 1, player_pos.pos_y);
                            current_map.get(player_pos.pos_x).get(player_pos.pos_y).which_object = 0;
                            current_map.get(player_pos.pos_x - 2).get(player_pos.pos_y).setMovable(false);
                        } else {
                            player_pos.pos_x++;
                        }
                        break;
                    case 4:
                        player_pos.pos_x++;
                        break;
                }
                player_pos.pos_x--;
                break;
            case "s":
                switch (current_map.get(player_pos.pos_x + 1).get(player_pos.pos_y).which_object) {
                    case 0,1:
                        swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x + 1, player_pos.pos_y);
                        break;
                    case 2:
                        if (!current_map.get(player_pos.pos_x + 1).get(player_pos.pos_y).isMovable()) {
                            return;
                        }
                        if (current_map.get(player_pos.pos_x + 2).get(player_pos.pos_y).which_object == 0) {

                            swapTiles(player_pos.pos_x + 1, player_pos.pos_y, player_pos.pos_x + 2, player_pos.pos_y);
                            swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x + 1, player_pos.pos_y);
                        }
                        else if (current_map.get(player_pos.pos_x + 2).get(player_pos.pos_y).which_object == 1) {
                            swapTiles(player_pos.pos_x + 1, player_pos.pos_y, player_pos.pos_x + 2, player_pos.pos_y);
                            swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x + 1, player_pos.pos_y);
                            current_map.get(player_pos.pos_x).get(player_pos.pos_y).which_object = 0;
                            current_map.get(player_pos.pos_x + 2).get(player_pos.pos_y).setMovable(false);
                        } else {
                            player_pos.pos_x--;
                        }
                        break;
                    case 4:
                        player_pos.pos_x--;
                        break;
                }
                player_pos.pos_x++;
                break;
            case "a":
                switch (current_map.get(player_pos.pos_x).get(player_pos.pos_y-1).which_object) {
                    case 0,1:
                        swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x , player_pos.pos_y-1);
                        break;
                    case 2:
                        if (!current_map.get(player_pos.pos_x ).get(player_pos.pos_y-1).isMovable()) {
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
                    case 4:
                        player_pos.pos_y++;
                        break;
                }
                player_pos.pos_y--;
                break;
            case "d":
                switch (current_map.get(player_pos.pos_x).get(player_pos.pos_y+1).which_object) {
                    case 0,1:
                        swapTiles(player_pos.pos_x, player_pos.pos_y, player_pos.pos_x , player_pos.pos_y+1);
                        break;
                    case 2:
                        if (!current_map.get(player_pos.pos_x ).get(player_pos.pos_y+1).isMovable()) {
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
                    case 4:
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
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(hashMap);
            objectStream.flush();
            byte[] byteArray = byteStream.toByteArray();

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
        int maxRow = map.keySet().stream().max(Integer::compareTo).orElse(0);
        int maxCol = map.values().stream()
                .flatMap(innerMap -> innerMap.keySet().stream())
                .max(Integer::compareTo)
                .orElse(0);

        for (int i = 0; i <= maxRow; i++) {
            System.out.print("[ ");
            for (int j = 0; j <= maxCol; j++) {
                HashMap<Integer, displayed_object> innerMap = map.get(i);
                if (innerMap != null && innerMap.containsKey(j)) {
                    System.out.print(innerMap.get(j).which_object + " ");
                } else {
                    System.out.print("-1 ");
                }
            }
            System.out.println("]");
        }
    }

    public void swapTiles(
            int row1, int col1, int row2, int col2) {
        if (current_map.containsKey(row1) && current_map.get(row1).containsKey(col1)
                && current_map.containsKey(row2) && current_map.get(row2).containsKey(col2)) {

            displayed_object obj1 = current_map.get(row1).get(col1);
            displayed_object obj2 = current_map.get(row2).get(col2);

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