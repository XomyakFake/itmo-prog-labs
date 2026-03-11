package proglab5.models;

import java.util.Objects;

import proglab5.utility.Validate;

/**
 * Класс координат
 * @author XomyakFake
 */

public class Coordinates implements Validate{
    private Double x; //Максимальное значение поля: 567, Поле не может быть null
    private Integer y; //Максимальное значение поля: 631, Поле не может быть null

    public Coordinates(Double x, Integer y){
        this.x = x;
        this.y = y;
    }

    public Coordinates(String s) {
        try {
            try {this.x = Double.parseDouble(s.split(";")[0]); } catch (NumberFormatException e) {this.x = null;}
            try {this.y = Integer.parseInt(s.split(";")[1]); } catch (NumberFormatException e) {this.y = null;}
        } 
        catch (ArrayIndexOutOfBoundsException e) {}
    }
    
    /**
     * Валидация полей.
     * @return true, если верно, иначе false
    */
    
    @Override
    public boolean validate(){
        if(x == null || x > 567) return false;
        if(y == null || y > 631) return false;
            return true;
    } 
    
    public Double getX() { return x; }
    public Integer getY() { return y; }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Coordinates that = (Coordinates) obj;
        return x.equals(that.x) && y.equals(that.y);
    }

    @Override
    public int hashCode(){
        return Objects.hash(x,y);
    }

    @Override
    public String toString(){
        return x + ";" + y;
    }

}
