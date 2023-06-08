package main.ui;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

@Component
public class AppView {

    @PostConstruct
    public void initializeView (){
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(500, 500));
        frame.setVisible(true);
        System.out.println("initialized");
    }

}
