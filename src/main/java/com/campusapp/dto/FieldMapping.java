package com.campusapp.dto;

public class FieldMapping {

    private String placeholder;
    private float x;
    private float y;
    private float fontSize;
    private String fontName;
    private String color;

    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getFontSize() { return fontSize; }
    public void setFontSize(float fontSize) { this.fontSize = fontSize; }

    public String getFontName() { return fontName; }
    public void setFontName(String fontName) { this.fontName = fontName; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}