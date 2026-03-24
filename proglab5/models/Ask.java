package proglab5.models;

import java.util.Scanner;

import proglab5.managers.CollectionManager;

public class Ask {
    public static Movie askMovie(Scanner scanner, CollectionManager cm){
        String name = null;
        while(true){
            System.out.print("Введите имя: ");
            name = scanner.nextLine().strip();
            if(!name.isEmpty()) break;
            System.out.println("Имя не может быть пустым");
        }

        Coordinates coordinates = askCoordinates(scanner);

        long oscarsCount;
        while (true){
            System.out.print("Введите количесвто оскаров: ");
            String line = scanner.nextLine().strip();
            try{
                oscarsCount = Long.parseLong(line);
                if (oscarsCount > 0) break;
                System.out.println("Значение должно быть больше 0");
            } 
            catch (NumberFormatException e) {
                System.out.println("Введите число значение которого больше 0d");
            }
        }

        Long goldenPalmCount;
        while (true){
            System.out.print("Введите goldenPalmCount: ");
            String line = scanner.nextLine().strip();
            try{
                goldenPalmCount = Long.parseLong(line);
                if (goldenPalmCount > 0) break;
                System.out.println("Значение должно быть больше 0");
            } 
            catch (NumberFormatException e) {
                System.out.println("Введите число значение которого больше 0");
            }
        }

        System.out.print("Введите tagline: ");
        String tagline = scanner.nextLine().strip();
        if (tagline.isEmpty()) tagline = null;

        MpaaRating mpaaRating = askMpaaRating(scanner);

        Person director = askPerson(scanner, cm);

        return new Movie(name, coordinates, oscarsCount, goldenPalmCount, tagline, mpaaRating, director);
    }

    public static Coordinates askCoordinates(Scanner scanner){
        Double x;
        while(true){
            System.out.print("Введите координату х: ");
            try{
                var line = scanner.nextLine().strip();
                x = Double.parseDouble(line);
                if(x <= 567) break;
                System.out.println("Значение должно быть не больше 567");
            }
            catch(NumberFormatException e){
                System.out.println("Введите число значение которого не больше 567");
            }
        }
        Integer y;
        while(true){
            System.out.print("Введите координату y: ");
            try{
                var line = scanner.nextLine().strip();
                y = Integer.parseInt(line);
                if(y <= 631) break;
                System.out.println("Значение должно быть не больше 631");
            }
            catch(NumberFormatException e){
                System.out.println("Введите число значение которого не больше 631");
            }
        }
        return new Coordinates(x,y);
    }

    public static Color askColor(Scanner scanner){
        Color[] values = Color.values();
        while(true){
            System.out.print("Color (" + Color.names() + "): ");
            var line = scanner.nextLine().strip();
            if(line.isEmpty()){
                return null;
            }
            try{
                int num = Integer.parseInt(line);
                if(num >= 1 && num <= values.length){
                    return values[num-1];
                }
                else{
                    System.out.println("Число должно быть от 1 до " + values.length);
                }
            }
            catch(NumberFormatException e){
                try{
                    return Color.valueOf(line);
                }
                catch(IllegalArgumentException ex){
                    System.out.println("Такого цвета нет");
                }
            }
        }
    }

    public static Country askCountry(Scanner scanner){
        Country[] values = Country.values();
        while(true){
            System.out.print("Country (" + Country.names() + "): ");
            var line = scanner.nextLine().strip();
            if(line.isEmpty()){
                return null;
            }
            try{
                int num = Integer.parseInt(line);
                if(num >= 1 && num <= values.length){
                    return values[num-1];
                }
                else{
                    System.out.println("Число должно быть от 1 до " + values.length);
                }
            }
            catch(NumberFormatException e){
                try{
                    return Country.valueOf(line);
                }
                catch(IllegalArgumentException ex){
                    System.out.println("Такой страны нет");
                }
            }
        }
    }

    public static MpaaRating askMpaaRating(Scanner scanner){
        MpaaRating[] values = MpaaRating.values();
        while(true){
            System.out.print("MpaaRating (" + MpaaRating.names() + "): ");
            var line = scanner.nextLine().strip();
            if(line.isEmpty()){
                return null;
            }
            try{
                int num = Integer.parseInt(line);
                if(num >= 1 && num <= values.length){
                    return values[num-1];
                }
                else{
                    System.out.println("Число должно быть от 1 до " + values.length);
                }
            }
            catch(NumberFormatException e){
                try{
                    return MpaaRating.valueOf(line);
                }
                catch(IllegalArgumentException ex){
                    System.out.println("Такого рейтинга нет");
                }
            }
        }
    }

    public static Person askPerson(Scanner scanner, CollectionManager cm){
        System.out.print("Введите имя режиссера: ");
        var name = scanner.nextLine().strip();
        if(name.isEmpty()){
            return null;
        }
        String passportID = null;
        while(true){
            System.out.print("Введите passportID: ");
            var line = scanner.nextLine().strip();
            if(line.isEmpty()){
                break;
            }
            if(line.length() > 47){
                System.out.println("Длина ID должна быть не больше 47");
                continue;
            }

            if(!cm.isUniquePassportId(line)){
                System.out.println("Такой ID режисера уже существует");
                continue;
            }
            passportID = line;
            break;
        }
        System.out.print("Введите цвет глаз: ");
        Color eyeColor = askColor(scanner);

        System.out.print("Введите цвет волос: ");
        Color hairColor = askColor(scanner);

        System.out.print("Введите национальность: ");
        Country nationality  = askCountry(scanner);

        return new Person(name, passportID, eyeColor, hairColor, nationality);
    }
}
