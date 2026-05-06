package ru.itmo.common.models;

import ru.itmo.common.utility.Validate;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Класс фильма
 * @author XomyakFake
 */
public class Movie implements Validate, Comparable<Movie>, Serializable {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long oscarsCount; //Значение поля должно быть больше 0
    private Long goldenPalmCount; //Значение поля должно быть больше 0, Поле не может быть null
    private String tagline; //Поле может быть null
    private MpaaRating mpaaRating; //Поле не может быть null
    private Person director; //Поле может быть null

    public Movie(String name, Coordinates coordinates, ZonedDateTime creationDate, long oscarsCount, Long goldenPalmCount, String tagline, MpaaRating mpaaRating, Person director) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.oscarsCount = oscarsCount;
        this.goldenPalmCount = goldenPalmCount;
        this.tagline = tagline;
        this.mpaaRating = mpaaRating;
        this.director = director;
    }

    public Movie(String name, Coordinates coordinates,long oscarsCount, Long goldenPalmCount, String tagline, MpaaRating mpaaRating, Person director) {
        this(name, coordinates, ZonedDateTime.now(), oscarsCount, goldenPalmCount, tagline, mpaaRating, director);
    }

    /**
     * Валидация полей.
     * @return true, если верно, иначе false
    */
    @Override
    public boolean validate() {
        if(id != null && id <= 0) return false;
        if(name == null || name.isEmpty()) return false;
        if(!coordinates.validate()) return false;
        if(creationDate == null) return false;
        if(oscarsCount <= 0) return false;
        if(goldenPalmCount == null || goldenPalmCount <= 0) return false;
        if(mpaaRating == null) return false;
        if(director != null && !director.validate()) return false;
        return true;
    }

    public static Movie fromArrayNoId(String[] list) {
        String name;
        Coordinates coordinates;
        long oscarsCount;
        Long goldenPalmCount;
        String tagline;
        MpaaRating mpaaRating;
        Person director;
        
        try {
            name = list[0];
            
            coordinates = new Coordinates(list[1]);

            try { oscarsCount = Long.parseLong(list[2]); }
            catch (NumberFormatException e) { oscarsCount = 0; }

            try { goldenPalmCount = Long.parseLong(list[3]); }
            catch (NumberFormatException e) { goldenPalmCount = null; }

            if (list[4].equals("null")) tagline = null;
            else { tagline = list[4]; }

            try { mpaaRating = MpaaRating.valueOf(list[5]); }
            catch (IllegalArgumentException e) { mpaaRating = null; }

            if(list.length > 6 && !list[6].isEmpty() && !list[6].equals("null")) director = new Person(list[6]);
            else{director = null;}

            Movie movie = new Movie(name, coordinates, oscarsCount, goldenPalmCount, tagline, mpaaRating, director);
            
            return movie;

        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }



    public Integer getId() { return id; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public ZonedDateTime getCreationDate() { return creationDate; }
    public long getOscarsCount() { return oscarsCount; }
    public Long getGoldenPalmCount() { return goldenPalmCount; }
    public String getTagline() { return tagline; }
    public MpaaRating getMpaaRating() { return mpaaRating; }
    public Person getDirector() { return director; }
    
    public void setId(Integer id){
        this.id = id;
    }

    @Override
    public int compareTo(Movie other){
        return Long.compare(this.oscarsCount, other.oscarsCount);
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Movie that = (Movie) obj;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", oscarsCount=" + oscarsCount +
                ", goldenPalmCount=" + goldenPalmCount +
                ", tagline='" + tagline + '\'' +
                ", mpaaRating=" + mpaaRating +
                ", director=" + director +
                '}';
    }
}