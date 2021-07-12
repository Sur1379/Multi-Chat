import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class MySocket {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            Socket socket = new Socket("localhost", 8080);
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            Thread thread = new Thread(() -> {
                Gson gson = new Gson();
                while (!Thread.interrupted()) {
                    try {
                        InputStream inputStream = socket.getInputStream();
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                        String o = (String) objectInputStream.readObject();
                        User user = gson.fromJson(o, User.class);
                        if (Objects.nonNull(user.getMessage())) {
                            System.out.println(user.getName() + "`\n" + user.getMessage() + '\n');
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            });

            thread.start();

            User user = new User();
            while (true) {
                user.setMessage(scanner.nextLine());
                Gson gson = new Gson();
                String s = gson.toJson(user);
                objectOutputStream.writeObject(s);
                if (user.getMessage().equals("bye")) {
                    thread.interrupt();
                    Thread.sleep(100);
                    outputStream.close();
                    objectOutputStream.close();
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
