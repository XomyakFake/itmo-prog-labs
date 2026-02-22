package proglab5.models;

import proglab5.utility.Validate;
import java.time.ZonedDateTime;

public class Movie implements Validate, Comparable<Movie> {
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

    public boolean validate() {
        if(id == null || id <= 0) return false;
        if(name == null || name.isEmpty()) return false;
        if(!coordinates.validate()) return false;
        if(creationDate == null) return false;
        if(oscarsCount <= 0) return false;
        if(goldenPalmCount == null || goldenPalmCount <= 0) return false;
        if(mpaaRating == null) return false;
        if(director != null && !director.validate()) return false;
            return true;
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
        if(this.oscarsCount > other.oscarsCount) return 1;
        if(this.oscarsCount < other.oscarsCount) return -1;
        return Integer.compare(this.id, other.id);
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