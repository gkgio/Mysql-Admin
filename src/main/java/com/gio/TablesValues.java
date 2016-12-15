package com.gio;

import lombok.Data;

/**
 * Created by Георгий on 03.05.2016.
 *
 * Класс для хранеиния значении таблиц из Бд
 */
@Data
public class TablesValues {
    private String valueOne;
    private String valueTwo;
    private String valueThree;
    private String valueFour;
    private String valueFive;
    private String valueZero;

    public TablesValues(String valueZero, String valueOne, String valueTwo, String valueThree, String valueFour, String valueFive) {
        this.valueZero = valueZero;
        this.valueOne = valueOne;
        this.valueTwo = valueTwo;
        this.valueThree = valueThree;
        this.valueFour = valueFour;
        this.valueFive = valueFive;
    }

    public TablesValues(String valueZero,String valueOne, String valueTwo, String valueThree, String valueFour) {
        this.valueZero = valueZero;
        this.valueOne = valueOne;
        this.valueTwo = valueTwo;
        this.valueThree = valueThree;
        this.valueFour = valueFour;
        this.valueFive = "";
    }
}
