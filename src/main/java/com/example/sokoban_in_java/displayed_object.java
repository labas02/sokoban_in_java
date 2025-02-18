package com.example.sokoban_in_java;
import java.io.Serializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class displayed_object implements Serializable{
    public int which_object;
    private transient ImageView img;

    public displayed_object(int which_object) {
        this.which_object = which_object;
    }

    public void setWhich_object(int which_object) {
        this.which_object = which_object;
    }

    public ImageView display_object(){
        switch (which_object){
            case 0:
                img.setImage(new Image(getClass().getResource("/assets/ground.png").toExternalForm()));

                break;
            case 1:
                img.setImage(new Image(getClass().getResource("/assets/point.png").toExternalForm()));
                break;
            case 2:
                img.setImage(new Image(getClass().getResource("/assets/box.png").toExternalForm()));
                break;
            case 3:
                img.setImage(new Image(getClass().getResource("/assets/player.png").toExternalForm()));
                break;
            case 4:
                img.setImage(new Image(getClass().getResource("/assets/wall.png").toExternalForm()));

                break;
        }
        return new ImageView();
    }
}
