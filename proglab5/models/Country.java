package proglab5.models;

public enum Country {
    RUSSIA,
    CHINA,
    THAILAND,
    SOUTH_KOREA,
    JAPAN;

    public static String names(){
        StringBuilder namesList = new StringBuilder();
        for(var countryType : values()){
            namesList.append(countryType.name()).append(", ");
        }
        namesList.setLength(namesList.length() - 2);
        return namesList.toString();
    }
}
