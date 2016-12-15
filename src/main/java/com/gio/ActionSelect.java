package com.gio;

import com.mysql.fabric.jdbc.FabricMySQLDriver;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Георгий on 04.05.2016.
 */
public class ActionSelect extends JFrame implements ActionListener {
    //Параметры для подключения к БД и аутентификации
    private static final String URL = "jdbc:mysql://localhost:3306/dbtest";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "21091995";

    public static List TableValuesList = new LinkedList(); //Храним значения БД в промежуточных шагах
    public static DefaultTableModel DataModel = new DefaultTableModel() {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
            return false;
        }
    };
    public static JTable dpTable = new JTable(DataModel);
    public static JFileChooser FChooser = new JFileChooser();
    public static int selectedId;

    public final static String nameValueZeroTableSellers = "seller_id";
    public final static String nameValueOneTableSellers = "seller_name";
    public final static String nameValueTwoTableSellers = "seller_lastname";
    public final static String nameValueThreeTableSellers = "seller_username";
    public final static String nameValueFourTableSellers = "seller_password";

    public final static String nameValueZeroTableSoldProducts = "product_id";
    public final static String nameValueOneTableSoldProducts = "product_key";
    public final static String nameValueTwoTableSoldProducts = "product_name";
    public final static String nameValueThreeTableSoldProducts = "seller_name";
    public final static String nameValueFourTableSoldProducts = "seller_lastname";
    public final static String nameValueFiveTableSoldProducts = "product_price";

    public final static String nameValueZeroTableProductsRepository = "product_id";
    public final static String nameValueOneTableProductsRepository = "product_key";
    public final static String nameValueTwoTableProductsRepository = "product_name";
    public final static String nameValueThreeTableProductsRepository = "count_of_product";
    public final static String nameValueFourTableProductsRepository = "product_price";

    JMenuItem menu[];
    JLabel lab[];
    JTextField labt[];
    JButton btnAdd, btnSave;

    boolean flagThatTableUseNotSoldProducts = true;
    byte numberOfUsingTable = 0; //1-Sellers; 2-SoldProducts; 3-ProductsRepository

    public ActionSelect(String nameOfFrame, int frameWidth, int frameHeight) {
        super(nameOfFrame);
        try {
            Driver driver = new FabricMySQLDriver();//подключаем драивер для подключения к БД
            DriverManager.registerDriver(driver); // и его регистрация в DriverManager
        } catch (SQLException e1) {
            Error("Write administrator \"Problem with load class from driver or his registration\" ");
            e1.printStackTrace();
        }
        JMenuBar bar = new JMenuBar();
        JMenu mfill = new JMenu("File");
        JMenu mopt = new JMenu("Edit");

        menu = new JMenuItem[9];

        menu[0] = new JMenuItem("Table exit");
        menu[1] = new JMenuItem("ProductsRepository"); //table name ProductsRepository
        menu[2] = new JMenuItem("Sellers"); //table name Sellers
        menu[3] = new JMenuItem("SoldProducts"); //table name SoldProducts
        menu[4] = new JMenuItem("Save in disc");
        menu[5] = new JMenuItem("Exit");

        menu[6] = new JMenuItem("Add");
        menu[7] = new JMenuItem("Correct");
        menu[8] = new JMenuItem("Remove");
        /*Утвнавливаем размер шрифта каждого пункта меню, ставим на них слушателя,
        * и добавляем их в соответствующие раскрывающиеся меню File и Edit
        */
        for (int i = 0; i < 9; i++) {
            menu[i].setFont(new Font("Monospaced", Font.HANGING_BASELINE, 15));
            menu[i].addActionListener(this);
            if (i < 6) mfill.add(menu[i]);
            else mopt.add(menu[i]);
        }

        bar.add(mfill);//добавли раскрывающееся menu File в общую Bar
        bar.add(mopt);//добавли раскрывающееся menu Edit в общую Bar
        setJMenuBar(bar);
        JScrollPane TableScrollPane = new JScrollPane(dpTable);
        getContentPane().add(TableScrollPane);
        dpTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setSize(frameWidth, frameHeight);//устанавливаем размеры Frame
        setVisible(true);//делаем Frame видимым после создания
    }

    //Обработка нажатии на кнопки и пункты меню
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == menu[0]) { //table exit
            DataModel.setRowCount(0);
            TableValuesList.clear();
            numberOfUsingTable = 0;
            DataModel.setColumnCount(0);
        }

        if (event.getSource() == menu[1]) {//table ProductsRepository
            try {
                numberOfUsingTable = 3;
                flagThatTableUseNotSoldProducts = true;
                loadTable(TableValuesList, nameValueZeroTableProductsRepository, nameValueOneTableProductsRepository, nameValueTwoTableProductsRepository,
                        nameValueThreeTableProductsRepository, nameValueFourTableProductsRepository, "");
            } catch (IOException e) {
                Error("Write administrator \"Problem with loadProductsRepository table\" ");
                e.printStackTrace();
            }
            this.repaint();
        }

        if (event.getSource() == menu[2]) {//table Sellers
            try {
                numberOfUsingTable = 1;
                flagThatTableUseNotSoldProducts = true;
                loadTable(TableValuesList, nameValueZeroTableSellers, nameValueOneTableSellers,
                        nameValueTwoTableSellers, nameValueThreeTableSellers, nameValueFourTableSellers, "");
            } catch (IOException e) {
                Error("Write administrator \"Problem with Sellers table\" ");
                e.printStackTrace();
            }
            this.repaint();
        }

        if (event.getSource() == menu[3]) {//table SoldProducts
            try {
                numberOfUsingTable = 2;
                flagThatTableUseNotSoldProducts = false;
                loadTable(TableValuesList, nameValueZeroTableSoldProducts, nameValueOneTableSoldProducts, nameValueTwoTableSoldProducts,
                        nameValueThreeTableSoldProducts, nameValueFourTableSoldProducts, nameValueFiveTableSoldProducts);
            } catch (IOException e) {
                Error("Write administrator \"Problem with SoldProducts table\" ");
                e.printStackTrace();
            }
            this.repaint();
        }

        if (event.getSource() == menu[4]) {//Save in disc
            if (numberOfUsingTable != 0) {
                if (JFileChooser.APPROVE_OPTION == FChooser.showSaveDialog(this)) {
                    try {
                        if (!(Save(TableValuesList, FChooser.getSelectedFile().getPath())))
                            Error("ERROR SAVE !");
                    } catch (Exception e) {
                        Error("ERROR SAVE!");
                    }
                }
                this.repaint();
            } else Error("You do not open table");
        }

        if (event.getSource() == menu[5]) {//exit
            System.exit(0);
        }

        if (event.getSource() == menu[6]) {//add value
            switch (numberOfUsingTable) {
                case 1:
                    frameForAddData(nameValueZeroTableSellers, nameValueOneTableSellers, nameValueTwoTableSellers, nameValueThreeTableSellers,
                            nameValueFourTableSellers, "");
                    break;
                case 2:
                    frameForAddData(nameValueZeroTableSoldProducts, nameValueOneTableSoldProducts, nameValueTwoTableSoldProducts,
                            nameValueThreeTableSoldProducts, nameValueFourTableSoldProducts, nameValueFiveTableSoldProducts);
                    break;
                case 3:
                    frameForAddData(nameValueZeroTableProductsRepository, nameValueOneTableProductsRepository, nameValueTwoTableProductsRepository,
                            nameValueThreeTableProductsRepository, nameValueFourTableProductsRepository, "");
                    break;
                default:
                    Error("You do not open table");
                    break;
            }
        }

        if (event.getSource() == menu[7]) { //correct
            switch (numberOfUsingTable) {
                case 1:
                    frameForCorrectData(nameValueZeroTableSellers, nameValueOneTableSellers, nameValueTwoTableSellers, nameValueThreeTableSellers,
                            nameValueFourTableSellers, "");
                    break;
                case 2:
                    frameForCorrectData(nameValueZeroTableSoldProducts, nameValueOneTableSoldProducts, nameValueTwoTableSoldProducts,
                            nameValueThreeTableSoldProducts, nameValueFourTableSoldProducts, nameValueFiveTableSoldProducts);
                    break;
                case 3:
                    frameForCorrectData(nameValueZeroTableProductsRepository, nameValueOneTableProductsRepository, nameValueTwoTableProductsRepository,
                            nameValueThreeTableProductsRepository, nameValueFourTableProductsRepository, "");
                    break;
                default:
                    Error("You do not open table");
                    break;
            }
        }

        if (event.getSource() == menu[8]) {//remove
            if (numberOfUsingTable != 0) {
                delValue(TableValuesList, (dpTable.getSelectedRow()));
            } else Error("You do not open table");
        }

        if (event.getSource() == btnAdd) {
            addValueInTable();
        }

        if (event.getSource() == btnSave) {
            updateValueInTable();
        }
    }

    //Edit выбранный пункт menu
    public void updateValueInTable() {
        //in first part of this method we update data in database
        boolean correctValue = true;
        switch (numberOfUsingTable) {
            case 1:
                try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); PreparedStatement preparedStatement =
                        connection.prepareStatement("UPDATE sellers SET seller_name = ?, seller_lastname = ?, seller_username = ?, " +
                                "seller_password = ? WHERE seller_id = ?")) {
                    preparedStatement.setString(1, labt[0].getText());
                    preparedStatement.setString(2, labt[1].getText());
                    preparedStatement.setString(3, labt[2].getText());
                    preparedStatement.setString(4, labt[3].getText());
                    preparedStatement.setInt(5, Integer.parseInt(labt[5].getText()));
                    preparedStatement.executeUpdate();//выполняем sql запрос
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                if (isInteger(labt[0].getText()) && isInteger(labt[4].getText())) {
                    correctValue = true;
                    try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); PreparedStatement preparedStatement =
                            connection.prepareStatement("UPDATE soldproducts SET product_key = ?, product_name = ?, seller_name = ?, " +
                                    "seller_lastname = ?, product_price = ? WHERE product_id = ?)")) {
                        preparedStatement.setInt(1, Integer.parseInt(labt[0].getText()));
                        preparedStatement.setString(2, labt[1].getText());
                        preparedStatement.setString(3, labt[2].getText());
                        preparedStatement.setString(4, labt[3].getText());
                        preparedStatement.setInt(5, Integer.parseInt(labt[4].getText()));
                        preparedStatement.setInt(6, Integer.parseInt(labt[5].getText()));
                        preparedStatement.executeUpdate();//выполняем sql запрос
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    correctValue = false;
                    Error("Not correct value");
                }
                break;
            case 3:
                if (isInteger(labt[0].getText()) && isInteger(labt[3].getText()) && isInteger(labt[2].getText())) {
                    correctValue = true;
                    try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); PreparedStatement preparedStatement =
                            connection.prepareStatement("UPDATE productsrepository SET product_key = ?, product_name = ?, count_of_product = ?, " +
                                    "product_price = ? WHERE product_id = ?")) {
                        preparedStatement.setInt(1, Integer.parseInt(labt[0].getText()));
                        preparedStatement.setString(2, labt[1].getText());
                        preparedStatement.setInt(3, Integer.parseInt(labt[2].getText()));
                        preparedStatement.setInt(4, Integer.parseInt(labt[3].getText()));
                        preparedStatement.setInt(5, Integer.parseInt(labt[5].getText()));
                        preparedStatement.executeUpdate();//выполняем sql запрос
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    correctValue = false;
                    Error("Not correct value");
                }
                break;
            default:
                break;
        }
        //update date in desktop application
        if (correctValue) {
            if (flagThatTableUseNotSoldProducts) {
                DataModel.insertRow(selectedId, new Object[]{labt[5].getText(), labt[0].getText(), labt[1].getText(),
                        labt[2].getText(), labt[3].getText()});
                ((TablesValues) (TableValuesList.get(selectedId))).setValueZero(labt[5].getText());
                ((TablesValues) (TableValuesList.get(selectedId))).setValueOne(labt[0].getText());
                ((TablesValues) (TableValuesList.get(selectedId))).setValueTwo(labt[1].getText());
                ((TablesValues) (TableValuesList.get(selectedId))).setValueThree(labt[2].getText());
                ((TablesValues) (TableValuesList.get(selectedId))).setValueFive(labt[3].getText());
                DataModel.removeRow(selectedId + 1);
            } else {
                DataModel.insertRow(selectedId, new Object[]{labt[5].getText(), labt[0].getText(), labt[1].getText(),
                        labt[2].getText(), labt[3].getText(), labt[4].getText()});
                ((TablesValues) (TableValuesList.get(selectedId))).setValueZero(labt[5].getText());
                ((TablesValues) (TableValuesList.get(selectedId))).setValueOne(labt[0].getText());
                ((TablesValues) (TableValuesList.get(selectedId))).setValueTwo(labt[1].getText());
                ((TablesValues) (TableValuesList.get(selectedId))).setValueThree(labt[2].getText());
                ((TablesValues) (TableValuesList.get(selectedId))).setValueFive(labt[3].getText());
                ((TablesValues) (TableValuesList.get(selectedId))).setValueFour(labt[4].getText());
                DataModel.removeRow(selectedId + 1);
            }
        }
    }

    //Добавление значения в выбранной таблице
    public void addValueInTable() {
        boolean correctValue = true;
        switch (numberOfUsingTable) {
            case 1:
                if (isInteger(labt[5].getText())) {
                    correctValue = true;
                    try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); PreparedStatement preparedStatement =
                            connection.prepareStatement("INSERT INTO sellers (seller_id, seller_name, seller_lastname, seller_username, " +
                                    "seller_password) VALUES (?, ?, ?, ?, ?)")) {
                        preparedStatement.setInt(1, Integer.parseInt(labt[5].getText()));
                        preparedStatement.setString(2, labt[0].getText());
                        preparedStatement.setString(3, labt[1].getText());
                        preparedStatement.setString(4, labt[2].getText());
                        preparedStatement.setString(5, labt[3].getText());
                        preparedStatement.executeUpdate();//выполняем sql запрос
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    correctValue = false;
                    Error("Not correct value");
                }
                break;
            case 2:
                if (isInteger(labt[0].getText()) && isInteger(labt[4].getText()) && isInteger(labt[5].getText())) {
                    correctValue = true;
                    try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); PreparedStatement preparedStatement =
                            connection.prepareStatement("INSERT INTO soldproducts (product_id, product_key, product_name, seller_name, " +
                                    "seller_lastname, product_price) VALUES (?, ?, ?, ?, ?, ?)")) {
                        preparedStatement.setInt(1, Integer.parseInt(labt[5].getText()));
                        preparedStatement.setInt(2, Integer.parseInt(labt[0].getText()));
                        preparedStatement.setString(3, labt[1].getText());
                        preparedStatement.setString(4, labt[2].getText());
                        preparedStatement.setString(5, labt[3].getText());
                        preparedStatement.setInt(6, Integer.parseInt(labt[4].getText()));
                        preparedStatement.executeUpdate();//выполняем sql запрос
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    correctValue = false;
                    Error("Not correct value");
                }
                break;
            case 3:
                if (isInteger(labt[0].getText()) && isInteger(labt[3].getText()) && isInteger(labt[2].getText())) {
                    correctValue = true;
                    try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); PreparedStatement preparedStatement =
                            connection.prepareStatement("INSERT INTO productsrepository (product_id, product_key, " +
                                    "product_name, count_of_product, product_price) VALUES (?, ?, ?, ?, ?)")) {
                        preparedStatement.setInt(1, Integer.parseInt(labt[5].getText()));
                        preparedStatement.setInt(2, Integer.parseInt(labt[0].getText()));
                        preparedStatement.setString(3, labt[1].getText());
                        preparedStatement.setInt(4, Integer.parseInt(labt[2].getText()));
                        preparedStatement.setInt(5, Integer.parseInt(labt[3].getText()));
                        preparedStatement.executeUpdate();//выполняем sql запрос
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    correctValue = false;
                    Error("Not correct value");
                }
                break;
            default:
                break;
        }
        //add values in desktop application
        if (correctValue) {
            if (flagThatTableUseNotSoldProducts) {
                TablesValues tablesValues = new TablesValues(labt[5].getText(), labt[0].getText(), labt[1].getText(), labt[2].getText(), labt[3].getText());
                DataModel.addRow(new Object[]{labt[5].getText(), labt[0].getText(), labt[1].getText(), labt[2].getText(), labt[3].getText()});
                TableValuesList.add(tablesValues);
            } else {
                TablesValues tablesValues = new TablesValues(labt[5].getText(), labt[0].getText(), labt[1].getText(), labt[2].getText(),
                        labt[3].getText(), labt[4].getText());
                DataModel.addRow(new Object[]{labt[5].getText(), labt[0].getText(), labt[1].getText(), labt[2].getText(), labt[3].getText(),
                        labt[4].getText()});
                TableValuesList.add(tablesValues);
                labt[4].setText("");
            }
            labt[0].setText("");
            labt[1].setText("");
            labt[2].setText("");
            labt[3].setText("");
            labt[5].setText("");
        }
    }

    //метод для провеки коретности ввода данных, а именно являются ли вводимые данные числами
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public static void Error(String text) {
        JOptionPane.showMessageDialog(null, text, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * удаляем выбранное значение в таблице
     * @param listValues список с значениями (таблица)
     * @param i номер параметра в sql запросе
     * @return
     */
    public boolean delValue(List listValues, int i) {

        switch (numberOfUsingTable) {
            case 1:
                try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); PreparedStatement preparedStatement =
                        connection.prepareStatement("DELETE FROM sellers WHERE seller_name =? AND seller_lastname = ?" +
                                " AND seller_username =? ")) {
                    preparedStatement.setString(1, ((TablesValues) (listValues.get(i))).getValueOne());
                    preparedStatement.setString(2, ((TablesValues) (listValues.get(i))).getValueTwo());
                    preparedStatement.setString(3, ((TablesValues) (listValues.get(i))).getValueThree());
                    preparedStatement.executeUpdate();//выполняем sql запрос
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); PreparedStatement preparedStatement =
                        connection.prepareStatement("DELETE FROM soldproducts WHERE product_id = ?")) {
                    preparedStatement.setInt(1, Integer.parseInt(((TablesValues) (listValues.get(i))).getValueZero()));
                    preparedStatement.executeUpdate();//выполняем sql запрос

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); PreparedStatement preparedStatement =
                        connection.prepareStatement("DELETE FROM productsrepository WHERE product_key = ? AND product_name = ? ")) {
                    preparedStatement.setInt(1, Integer.parseInt(((TablesValues) (listValues.get(i))).getValueOne()));
                    preparedStatement.setString(2, ((TablesValues) (listValues.get(i))).getValueTwo());
                    preparedStatement.executeUpdate();//выполняем sql запрос
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        try {
            listValues.remove(i);
            DataModel.removeRow(i);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //Сохранение  таблицы на внешний носитель
    public boolean Save(List listValues, String adr) throws IOException {
        try {
            PrintWriter sv = new PrintWriter(new BufferedWriter(new FileWriter(adr)));
            Formatter formatter;
            if (flagThatTableUseNotSoldProducts) {
                formatter = new Formatter();
                sv.println(formatter.format("%20s %20s %20s %20s %20s", DataModel.getColumnName(0), DataModel.getColumnName(1), DataModel.getColumnName(2),
                        DataModel.getColumnName(3), DataModel.getColumnName(4)));
                for (int i = 0; i < listValues.size(); i++) {
                    formatter = new Formatter();
                    sv.println(formatter.format("%20s %20s %20s %20s %20s", ((TablesValues) (listValues.get(i))).getValueZero(),
                            ((TablesValues) (listValues.get(i))).getValueOne(), ((TablesValues) (listValues.get(i))).getValueTwo(),
                            ((TablesValues) (listValues.get(i))).getValueThree(), ((TablesValues) (listValues.get(i))).getValueFour()));
                }
            } else {
                formatter = new Formatter();
                sv.println(formatter.format("%20s %20s %20s %20s %20s %20s", DataModel.getColumnName(0), DataModel.getColumnName(1), DataModel.getColumnName(2),
                        DataModel.getColumnName(3), DataModel.getColumnName(4), (DataModel.getColumnName(5))));
                for (int i = 0; i < listValues.size(); i++) {
                    formatter = new Formatter();
                    sv.println(formatter.format("%20s %20s %20s %20s %20s %20s", ((TablesValues) (listValues.get(i))).getValueZero(),
                            ((TablesValues) (listValues.get(i))).getValueOne(), ((TablesValues) (listValues.get(i))).getValueTwo(),
                            ((TablesValues) (listValues.get(i))).getValueThree(), ((TablesValues) (listValues.get(i))).getValueFour(),
                            ((TablesValues) (listValues.get(i))).getValueFive()));
                }
            }
            sv.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //Создаем Frame для ввода изменении в выбранном пукте таблицы (Correct)
    public void frameForCorrectData(String nameZeroColumn, String nameOneColumn, String nameTwoColumn, String nameThreeColumn, String nameFourColumn, String nameFiveColumn) {
        if (dpTable.getSelectedRow() >= 0) {
            selectedId = dpTable.getSelectedRow();
            JDialog pan = new JDialog(this, true);
            pan.setTitle("Edit Values");
            pan.setLayout(new FlowLayout());

            lab = new JLabel[6];
            labt = new JTextField[6];

            lab[5] = new JLabel(nameZeroColumn);
            labt[5] = new JTextField(26);
            labt[5].setText(((TablesValues) (TableValuesList.get(selectedId))).getValueZero());

            lab[0] = new JLabel(nameOneColumn);
            labt[0] = new JTextField(26);
            labt[0].setText(((TablesValues) (TableValuesList.get(selectedId))).getValueOne());
            pan.add(lab[0]);
            pan.add(labt[0]);

            lab[1] = new JLabel(nameTwoColumn);
            labt[1] = new JTextField(26);
            labt[1].setText(((TablesValues) (TableValuesList.get(selectedId))).getValueTwo());
            pan.add(lab[1]);
            pan.add(labt[1]);

            lab[2] = new JLabel(nameThreeColumn);
            labt[2] = new JTextField(26);
            labt[2].setText(((TablesValues) (TableValuesList.get(selectedId))).getValueThree());
            pan.add(lab[2]);
            pan.add(labt[2]);

            lab[3] = new JLabel(nameFourColumn);
            labt[3] = new JTextField(26);
            labt[3].setText(((TablesValues) (TableValuesList.get(selectedId))).getValueFour());
            pan.add(lab[3]);
            pan.add(labt[3]);

            if (flagThatTableUseNotSoldProducts == false) {
                lab[4] = new JLabel(nameFiveColumn);
                labt[4] = new JTextField(26);
                labt[4].setText(((TablesValues) (TableValuesList.get(selectedId))).getValueFive());
                pan.add(lab[4]);
                pan.add(labt[4]);
            }

            btnSave = new JButton("apply");
            btnSave.addActionListener(this);
            pan.add(btnSave);

            pan.setLocation(150, 200);
            pan.setSize(330, 380);
            pan.setResizable(false);
            pan.setVisible(true);
        }
    }

    //создаем Frame для добавления значения в таблицу (Add)
    public void frameForAddData(String nameZeroColumn, String nameOneColumn, String nameTwoColumn,
                                                    String nameThreeColumn, String nameFourColumn, String nameFiveColumn) {
        JDialog pan = new JDialog(this, true);
        pan.setTitle("Add new Value");
        pan.setLayout(new FlowLayout());

        lab = new JLabel[6];
        labt = new JTextField[6];

        lab[5] = new JLabel(nameZeroColumn);
        labt[5] = new JTextField(26);
        pan.add(lab[5]);
        pan.add(labt[5]);

        lab[0] = new JLabel(nameOneColumn);
        labt[0] = new JTextField(26);
        pan.add(lab[0]);
        pan.add(labt[0]);

        lab[1] = new JLabel(nameTwoColumn);
        labt[1] = new JTextField(26);
        pan.add(lab[1]);
        pan.add(labt[1]);

        lab[2] = new JLabel(nameThreeColumn);
        labt[2] = new JTextField(26);
        pan.add(lab[2]);
        pan.add(labt[2]);

        lab[3] = new JLabel(nameFourColumn);
        labt[3] = new JTextField(26);
        pan.add(lab[3]);
        pan.add(labt[3]);

        if (flagThatTableUseNotSoldProducts == false) {
            lab[4] = new JLabel(nameFiveColumn);
            labt[4] = new JTextField(26);
            pan.add(lab[4]);
            pan.add(labt[4]);
        }

        btnAdd = new JButton("Add");
        btnAdd.addActionListener(this);
        pan.add(btnAdd);

        pan.setLocation(150, 200);
        pan.setSize(330, 380);
        pan.setResizable(false);
        pan.setVisible(true);
    }

    //Загрузка таблицы из БД
    public boolean loadTable(List listWithValues, String nameZeroColumn, String nameOneColumn, String nameTwoColumn, String nameThreeColumn,
                             String nameFourColumn, String nameFiveColumn) throws IOException {
        String sql = null;
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); Statement statement = connection.createStatement()) {
            switch (numberOfUsingTable) {
                case 1:
                    sql = "SELECT seller_id, seller_name, seller_lastname, seller_username, seller_password FROM sellers";
                    break;
                case 2:
                    sql = "SELECT product_id, product_key, product_name, seller_name,seller_lastname, product_price FROM soldproducts";
                    break;
                case 3:
                    sql = "SELECT product_id, product_key, product_name, count_of_product, product_price FROM productsrepository";
                    break;
                default:
                    break;
            }
            ResultSet rs = statement.executeQuery(sql);
            DataModel.setRowCount(0);
            TableValuesList.clear();
            DataModel.setColumnCount(0);
            DataModel.addColumn(nameZeroColumn);
            DataModel.addColumn(nameOneColumn);
            DataModel.addColumn(nameTwoColumn);
            DataModel.addColumn(nameThreeColumn);
            DataModel.addColumn(nameFourColumn);

            if (flagThatTableUseNotSoldProducts == false) {
                DataModel.addColumn(nameFiveColumn);
            }
            String zero, one, two, three, four, five;
            while (rs.next()) {
                zero = rs.getString(nameZeroColumn);
                one = rs.getString(nameOneColumn);
                two = rs.getString(nameTwoColumn);
                three = rs.getString(nameThreeColumn);
                four = rs.getString(nameFourColumn);
                TablesValues tablesValues;
                if (flagThatTableUseNotSoldProducts) {
                    tablesValues = new TablesValues(zero, one, two, three, four);
                    DataModel.addRow(new Object[]{tablesValues.getValueZero(), tablesValues.getValueOne(), tablesValues.getValueTwo(),
                            tablesValues.getValueThree(), tablesValues.getValueFour()});
                } else {
                    five = rs.getString(nameFiveColumn);
                    tablesValues = new TablesValues(zero, one, two, three, four, five);
                    DataModel.addRow(new Object[]{tablesValues.getValueZero(), tablesValues.getValueOne(), tablesValues.getValueTwo(),
                            tablesValues.getValueThree(), tablesValues.getValueFour(), tablesValues.getValueFive()});
                }
                listWithValues.add(tablesValues);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
