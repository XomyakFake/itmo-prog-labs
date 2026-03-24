package proglab5.models;

import java.util.Objects;

import proglab5.utility.Validate;

/**
 * Класс режиссера
 * @author XomyakFake
 */
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

    public Person(String s) {
    try {
      this.name = s.split(";")[0];
        try { this.passportID = s.split(";")[1].equals("null") ? null : s.split(";")[1]; } catch (NumberFormatException e) { return; }
        try { this.eyeColor = Color.valueOf(s.split(";")[2]); } catch (IllegalArgumentException  e) { this.eyeColor = null; }
        try { this.hairColor = Color.valueOf(s.split(";")[3]); } catch (IllegalArgumentException  e) { this.hairColor = null; }
        try { this.nationality = Country.valueOf(s.split(";")[4]); } catch (IllegalArgumentException  e) { this.nationality = null; }
    } 
    catch (ArrayIndexOutOfBoundsException e) {}
  }

    /**
     * Валидация полей.
     * @return true, если верно, иначе false
    */
    @Override
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
    public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Person person = (Person) o;
		return Objects.equals(name, person.name) && Objects.equals(passportID, person.passportID)
			&& Objects.equals(eyeColor, person.eyeColor) && Objects.equals(hairColor, person.hairColor)
			&& Objects.equals(nationality, person.nationality);
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(name, passportID, eyeColor, hairColor, nationality);
    }

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