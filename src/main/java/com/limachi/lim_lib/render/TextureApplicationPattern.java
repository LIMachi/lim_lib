package com.limachi.lim_lib.render;

public enum TextureApplicationPattern {
    STRETCH, //default mode, fill the given coordinates by deforming the image
    MIDDLE_EXPANSION, //vanilla method for buttons, scale the image from the corners to the middle of the image
    TILE; //vanilla method for background, repeat the image with it's original ratio to fill the coordinates
}
