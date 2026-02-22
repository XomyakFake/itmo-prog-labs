package proglab5.models;

public enum Color {
    GREEN,
    RED,
    BLUE,
    YELLOW,
    WHITE,
    BLACK,
    BROWN;


    public static String names(){
        StringBuilder namesList = new StringBuilder();
        for(var colorType : values()){
            namesList.append(colorType.name()).append(", ");
        }
        namesList.setLength(namesList.length() - 2);
        return namesList.toString();
    }
}
