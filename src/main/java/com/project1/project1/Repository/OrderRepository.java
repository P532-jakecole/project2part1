package com.project1.project1.Repository;

import com.project1.project1.Trading.Order;
import com.project1.project1.Trading.OrderFactory;
import com.project1.project1.User.Portfolio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderRepository {

    private final Portfolio portfolio;
    private String DATABASE_NAME;
    private final OrderFactory orderFactory;

    @Autowired
    public OrderRepository(Portfolio portfolio, OrderFactory orderFactory) {
        this.portfolio = portfolio;
        this.orderFactory = orderFactory;
        DATABASE_NAME  = String.format("data/%d/History.txt", this.portfolio.getUserId());
        File dataDirectory = new File("data/");
        if(!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
        File user1Directory = new File("data/1");
        if(!user1Directory.exists()) {
            user1Directory.mkdirs();
        }
        File user2Directory = new File("data/2");
        if(!user2Directory.exists()) {
            user2Directory.mkdirs();
        }
        File user3Directory = new File("data/3");
        if(!user3Directory.exists()) {
            user3Directory.mkdirs();
        }
        for(int i = 1; i < 4; i++){
            File orderFile = new File(String.format("data/%d/History.txt", i));
            if(!orderFile.exists()) {
                try{
                    orderFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            File holdingFile = new File(String.format("data/%d/Holdings.txt", i));
            if(!holdingFile.exists()) {
                try{
                    holdingFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            File pendingFile = new File(String.format("data/%d/Pending.txt", i));
            if(!pendingFile.exists()) {
                try{
                    pendingFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static final String NEW_LINE = System.lineSeparator();
    private static void appendToFile(Path path, String content)
            throws IOException {
        Files.write(path,
                content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    private void updateDatabase(){
        DATABASE_NAME = String.format("data/%d/History.txt", this.portfolio.getUserId());
    }

    public void save(String[] order) throws IOException {
        orderFactory.createOrder(order[0], order[1], order[2], Double.parseDouble(order[3]), Double.parseDouble(order[4]));
    }


    public List<String> findAll() throws IOException {
        updateDatabase();
        Path path = Paths.get(DATABASE_NAME);
        List<String> data = Files.readAllLines(path);
        return data;
    }
}
