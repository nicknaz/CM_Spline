package table;

import javax.swing.*;
import java.awt.*;

public class DataTable extends JFrame
{
    public DataTable(Object[][] array, Object[] columnsHeader) throws HeadlessException {
        super("Простой пример с JTable");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Простая таблица
        JTable table1 = new JTable(array, columnsHeader);
        // Таблица с настройками
//        JTable table2 = new JTable(3, 5);
//        // Настройка таблицы
//        table2.setRowHeight(30);
//        table2.setRowHeight(1, 20);
//        table2.setIntercellSpacing(new Dimension(10, 10));
//        table2.setGridColor(Color.blue);
//        table2.setShowVerticalLines(false);


        Box contents = new Box(BoxLayout.Y_AXIS);
        contents.add(new JScrollPane(table1));
        //contents.add(new JScrollPane(table2));
        // Вывод окна на экран
        setContentPane(contents);
        setSize(500, 400);
        setVisible(true);
    }
}
