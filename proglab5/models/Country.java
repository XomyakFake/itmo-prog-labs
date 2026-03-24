package proglab5.models;

/**
 * Перечисление стран.
 * @author XomyakFake
 */

public enum Country {
    RUSSIA,
    CHINA,
    THAILAND,
    SOUTH_KOREA,
    JAPAN;

    /**
     * @return Строка со всеми элементами enum через строку
     */

    public static String names(){
        String namesList = "";
        for(var countryType : values()){
            namesList += countryType.ordinal()+1 + "| " + countryType.name() + ", ";
        }
        return namesList.substring(0, namesList.length() - 2);
    }
}
