package com.example.sokoban_in_java;
import java.io.Serializable;
import java.util.Objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class displayed_object implements Serializable {
    private static final long serialVersionUID = 1L;
    public int which_object;
    public boolean movable;

    public displayed_object(int which_object) {
        this.which_object = which_object;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void setWhich_object(int which_object) {
        this.which_object = which_object;
    }
}
