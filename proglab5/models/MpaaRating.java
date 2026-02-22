package proglab5.models;

public enum MpaaRating {
    G,
    PG,
    PG_13,
    R,
    NC_17;

    public static String names(){
        StringBuilder namesList = new StringBuilder();
        for(var mpaaType : values()){
            namesList.append(mpaaType.name()).append(", ");
        }
        namesList.setLength(namesList.length() - 2);
        return namesList.toString();
    }
}