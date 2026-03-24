package proglab5.models;

/**
 * Перечисление рейтингов.
 * @author XomyakFake
 */

public enum MpaaRating {
    G,
    PG,
    PG_13,
    R,
    NC_17;

   
    /**
    * @return Строка со всеми элементами enum через строку
    */
    public static String names(){
        String namesList = "";
        for(var mpaaType : values()){
            namesList += mpaaType.ordinal()+1 + "| " + mpaaType.name() + ", ";
        }
        return namesList.substring(0, namesList.length() - 2);
    }
}