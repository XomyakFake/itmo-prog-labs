package ru.itmo.client.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import ru.itmo.common.models.Movie;
import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;

public class Client {
    private InetAddress host;
    private int port;
    private Scanner scanner;
    private DatagramSocket socket;
    private Response response;
    private Request request;
    private static final Set<String> executingScripts = new HashSet<>();
    private int attempts = 2;

    public Client(InetAddress host, int port){
        this.host = host;
        this.port = port;
        this.scanner = new Scanner(System.in);
    }

    private byte[] receiveChunks(DatagramSocket socket) throws IOException{
        ByteArrayOutputStream chunks = new ByteArrayOutputStream();
        while(true){
            byte[] buf = new byte[60001];
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
            socket.receive(receivePacket);

            boolean isLast = (buf[receivePacket.getLength() - 1] == 1) ? true : false;
            chunks.write(buf, 0, receivePacket.getLength() - 1);
            if(isLast) break;
        }
        return chunks.toByteArray();
    }

    public void run(){
        try{
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            System.out.println("Запуск клиентского приложения");
            while(true){
                try{
                    if(!scanner.hasNextLine()) break;
                    String[] full = scanner.nextLine().strip().split(" ", 2);
                    String command = full[0].toLowerCase();
                    String args = "";
                    if(full.length == 2) args = full[1].strip();
                    
                    if(command.isEmpty()) continue;
                    
                    ClientCommand(command, args);
                }
                catch(NoSuchElementException e){
                    System.out.println("Остановка клиента");
                    System.exit(0);
                }
            }

        }
        catch(SocketException e){
            System.out.println("Не удалость открыть сокет");
        }
    }


    private void ClientCommand(String command, String args){
        request = null;

        if(command.equals("exit")){
            System.out.println("Работа клиентского приложения завершена");
            System.exit(0);
        }
        else if (command.equals("execute_script")){
            executeScript(args);
            return;
        }
        else if(command.equals("add") || command.equals("add_if_max")  || command.equals("remove_greater") || command.equals("update")){
            Movie movie = null; 
            
            if(!args.isEmpty()){
                movie = Movie.fromArrayNoId(args.split(",", -1));
                if(movie == null || !movie.validate()){
                    System.out.println("Ошибка парсинга");
                    return;
                }
            }
            else{
                movie = Ask.askMovie(scanner);
            }
            
            if(movie != null){
                request = new Request(command, movie);
            }
        }
        else if(command.equals("remove_by_id") || command.equals("filter_greater_than_mpaa_rating")){
            if(args.isEmpty()){
                System.out.println("Команде нужен аргумент");
                return;
            }
            request = new Request(command, args);
        }
        else{
            if(command.equals("save")){
                System.out.println("Эта команда запрещена");
                return;
            }
            request = new Request(command, null);
        }

        if(request != null){
            SendAndReceive(request);
        }
    }

    private void SendAndReceive(Request request){
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(request);
            oos.flush();
            byte[] object = baos.toByteArray();
            
            boolean received = false;
            for(int i = 0; i < attempts; i++){
                try{
                    DatagramPacket sendPacket = new DatagramPacket(object, object.length, host, port);
                    socket.send(sendPacket);
                    socket.setSoTimeout(3000);

                    byte[] receiveBytes = receiveChunks(socket);

                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receiveBytes, 0, receiveBytes.length));
                    response = (Response) ois.readObject();

                    if(response.getResponseId().equals(request.getRequestId())){
                        received = true;
                        break;
                    }
                }
                catch(SocketTimeoutException e){
                    System.out.println("Ответ от сервера не был получен. Попытка №" + (i+1));
                }
            }

            if(response.isSuccess() && received){
                System.out.println(response.getMessage());
                if(response.getCollection() != null){
                    System.out.println(response.getCollection());
                }
            }
            else if(!received){
                System.out.println("Запрос не обработан");
            }
            else{
                System.out.println("Ошибка сервера");
            }

        }
        catch(SocketTimeoutException e){
            System.out.println("Ошибка. Время подключения вышло");
        }
        catch(Exception e){
            System.out.println("Сервер не отвечает");
        }

    }

    private void executeScript(String file){
        if(file == null){
            System.out.println("Ошибка. Файла не указан");
            return;
        }
        Path path = Path.of(file);
        if(!Files.exists(path)){
            System.out.println("Такого файла нет");
            return;
        }
        if(!Files.isReadable(path)){
            System.out.println("Нет прав на чтение из файла");
            return;
        }
        String absolutePath = path.toAbsolutePath().normalize().toString();
        if(executingScripts.contains(absolutePath)){
            System.out.println("Обнаружена рекурсия. Пропуск выполнения");
            return;
        }

        executingScripts.add(absolutePath);
        try( 
            FileInputStream fis = new FileInputStream(absolutePath);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            Scanner filescanner = new Scanner(isr);
        ){
            while(filescanner.hasNextLine()){
                try{
                    String commandName = filescanner.nextLine().strip();
                    if(commandName.isEmpty()){
                        continue;
                    }
                    String[] full = commandName.split(" ", 2);
                    String command = full[0];
                    String arg = (full.length == 2 && !full[1].isEmpty()) ? full[1] : null;
                    ClientCommand(command, arg);
                }
                catch(Exception e){
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        }
        catch(Exception e){
            System.out.println("Ошибка выполнения скрипта " + e.getMessage());
        }
        finally{
            executingScripts.remove(absolutePath);
        }

    }

}
