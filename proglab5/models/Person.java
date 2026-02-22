package proglab5.models;

import proglab5.utility.Validate;

public class Person implements Validate {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private String passportID; //Значение этого поля должно быть уникальным, Длина строки не должна быть больше 47, Строка не может быть пустой, Поле может быть null
    private Color eyeColor; //Поле может быть null
    private Color hairColor; //Поле может быть null
    private Country nationality; //Поле не может быть null

    public Person(String name, String passportID, Color eyeColor, Color haiColor, Country nationality) {
        this.name = name;
        this.passportID = passportID;
        this.eyeColor = eyeColor;
        this.hairColor = haiColor;
        this.nationality = nationality;
    }

    public boolean validate(){
        if(name == null || name.isEmpty()) return false;
        if(passportID != null && (passportID.length() > 47 || passportID.isEmpty())) return false;
        if(nationality == null) return false;
            return true;
    }

    public String getName() { return name; }
    public String getPassportID() { return passportID; }
    public Color getEyeColor() { return eyeColor; }
    public Color getHairColor() { return hairColor; }
    public Country getNationality() { return nationality; }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", passportID='" + passportID + '\'' +
                ", eyeColor=" + eyeColor +
                ", hairColor=" + hairColor +
                ", nationality=" + nationality +
                '}';
    }
}