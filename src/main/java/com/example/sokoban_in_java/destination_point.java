package com.example.sokoban_in_java;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class destination_point {
    public int pos_x;
    public int pos_y;
    public boolean has_crate;

    ImageView display(){
        ImageView img = new ImageView();
        img.setImage(new Image(String.valueOf(this.getClass().getResource("/assets/point.jpg"))));
        return new ImageView();
    }
}
