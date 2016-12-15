package com.gio;

/**
 * Created by Георгий on 02.05.2016.
 */

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public static void main(String args[]) {
        /*
        * Создаем экземпляр класса ActionSelect и передаем констрктору
        * название Frame и его размеры
         */
        ActionSelect menu = new ActionSelect("GIG", 800, 600);
        //Делаем чтобы Frame прекращал работу после закрытия
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //положени Frame по умолчанию по центру
        menu.setLocationRelativeTo(null);


    }
}


