import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Server {
    private static final List<Socket> socketList = new ArrayList<>();
    private static final List<Thread> threadList = new ArrayList<>();
    private static final List<String> gsonList = new LinkedList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            for (int i = 0; i < 3; i++) {
                int finalI = i + 1;
                threadList.add(new Thread(() -> {
                    try {
                        Socket socket = serverSocket.accept();
                        socketList.add(socket);
                        System.out.println("User " + (finalI) + " connected");
                        InputStream inputStream = socket.getInputStream();
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                        Gson gson = new Gson();
                        if (!gsonList.isEmpty()){
                            gsonList.forEach(s -> singleSend(s, socket));
                        }
                        while (true) {
                            String o = (String) objectInputStream.readObject();
                            if (o != null) {
                                gsonList.add(o);
                                sendAll(o);
                                User user = gson.fromJson(o, User.class);
                                if (user.getMessage().equals("bye")) {
                                    inputStream.close();
                                    objectInputStream.close();
                                    socket.close();
                                    return;
                                }
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        threadList.forEach(Thread::start);
    }

    private static void sendAll(String object) {
        socketList.forEach(socket -> {
            if (Objects.nonNull(socket) && !socket.isClosed()) {
                singleSend(object, socket);
            }
        });

    }

    private static void singleSend(String object, Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
