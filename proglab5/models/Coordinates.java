package proglab5.models;

import proglab5.utility.Validate;

public class Coordinates implements Validate{
    private Double x; //Максимальное значение поля: 567, Поле не может быть null
    private Integer y; //Максимальное значение поля: 631, Поле не может быть null

    public Coordinates(Double x, Integer y){
        this.x = x;
        this.y = y;
    }

    public boolean validate(){
        if(x == null || x > 567) return false;
        if(y == null || y > 631) return false;
            return true;
    } 
    
    public Double getX() { return x; }
    public Integer getY() { return y; }

    @Override
    public String toString(){
        return x + ";" + y;
    }

}
