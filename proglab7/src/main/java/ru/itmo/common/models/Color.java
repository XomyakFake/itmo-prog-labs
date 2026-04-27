package ru.itmo.common.models;

/**
 * Перечисление цветов.
 * @author XomyakFake
 */
public enum Color {
    GREEN,
    RED,
    BLUE,
    YELLOW,
    WHITE,
    BLACK,
    BROWN;

    /**
     * @return Строка со всеми элементами enum через строку
     */

    public static String names(){
        String namesList = "";
        for(var colorType : values()){
            namesList += colorType.ordinal()+1 + "| " + colorType.name() + ", ";
        }
        return namesList.substring(0, namesList.length() - 2);
    }
}
