


package ru.csi.service;

import org.junit.*;
import org.junit.rules.TestName;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.csi.ConfigurationClass;
import ru.csi.exception.BadEntryDataException;
import ru.csi.model.DataPrice;
import ru.csi.model.PeriodPrice;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Простой тестовый класс
 *
 * @author HoshArt
 * @version 1.3
 */


public class UnionServiceImplTest {


    static List<PeriodPrice> oldList;
    static List<PeriodPrice> NewList;
    static Date date;

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfigurationClass.class);
    UnionService unionService = context.getBean("unionService", UnionService.class);

    @Rule
    public final TestName name = new TestName();


    @Before
    public void createList() {
        System.out.println("тест" + " " + name.getMethodName() + " Начат");
        NewList = new ArrayList<>();
        oldList = new ArrayList<>();
    }

    @After
    public void testOver() {
        System.out.println("тест" + " " + name.getMethodName() + " завершен");
    }

    @AfterClass
    public static void allOver() {
        System.out.println("Все тесты завершены");
    }
    //

    /**
     * добавляем дату с неверным форматом
     * ожидаем увидеть ошибку в парсинге даты {@link #getDate(String) method}
     */
    @Test(expected = BadEntryDataException.class)
    public void runUni1() {

        /** неверный формат даты*/
        Date dateNewBegin = getDate("4T.01.2013 00:00:00");

        //новая
        NewList.add(new PeriodPrice(dateNewBegin, getDate("10.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(100, 1, 1, "133"))));


    }

    /**
     * пришедшая цена разбивает текущую цену на три отрезка времени
     * ожидаем увидеть размер объединенного  листа размером 3
     */

    @Test
    public void runUni2() {

        oldList.add(new PeriodPrice(getDate("1.01.2013 00:00:00"), getDate("27.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(50, 1, 1, "133"))));
        //новая
        NewList.add(new PeriodPrice(getDate("5.01.2013 00:00:00"), getDate("15.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(80, 1, 1, "133"))));

        Assert.assertEquals(unionService.runUni(oldList, NewList).size(), 3);
    }


    /**
     * объединяем две цены с одной новой (пример №2 с вашего задания)
     */
    @Test
    public void runUni3() {

        oldList.add(new PeriodPrice(getDate("01.01.2013 00:00:00"), getDate("25.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(100, 1, 1, "133"))));
        oldList.add(new PeriodPrice(getDate("25.01.2013 00:00:00"), getDate("10.02.2013 00:00:00"),
                Arrays.asList(new DataPrice(200, 1, 1, "133"))));


        NewList.add(new PeriodPrice(getDate("07.01.2013 00:00:00"), getDate("27.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(300, 1, 1, "133"))));

        Assert.assertNotNull(oldList);
        Assert.assertNotNull(NewList);

        unionService.runUni(oldList, NewList);

    }

    /**
     * объединяем две нулевые коллекции
     */


    @Test(expected = BadEntryDataException.class)
    public void runUni4() {
        unionService.runUni(null, null);

    }


    /**
     * объединяем  к множеству цен на один продукт одну,перекрывающую старые цены
     * проверяем размер коллекций
     */

    @Test
    public void runUni5() {

        oldList.add(new PeriodPrice(getDate("01.01.2013 00:00:00"), getDate("10.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(100, 1, 1, "133"))));
        oldList.add(new PeriodPrice(getDate("10.01.2013 00:00:00"), getDate("15.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(130, 1, 1, "133"))));
        oldList.add(new PeriodPrice(getDate("15.01.2013 00:00:00"), getDate("26.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(220, 1, 1, "133"))));
        oldList.add(new PeriodPrice(getDate("26.01.2013 00:00:00"), getDate("25.02.2013 00:00:00"),
                Arrays.asList(new DataPrice(320, 1, 1, "133"))));
//новые
        NewList.add(new PeriodPrice(getDate("05.01.2013 00:00:00"), getDate("25.02.2013 00:00:00"),
                Arrays.asList(new DataPrice(100, 1, 1, "133"))));

        Assert.assertTrue(oldList.size() > 0 && NewList.size() > 0);
        unionService.runUni(oldList, NewList);


    }


    /**
     * в пришедшей цене нулевые данные продукта
     */

    @Test(expected = NullPointerException.class)
    public void runUni6() {



        oldList.add(new PeriodPrice(getDate("01.01.2013 00:00:00"), getDate("10.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(100, 1, 1, "133"))));
        oldList.add(new PeriodPrice(getDate("10.01.2013 00:00:00"), getDate("15.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(200, 1, 1, "133"))));
//новые
        NewList.add(new PeriodPrice(getDate("16.01.2013 00:00:00"), getDate("22.01.2013 00:00:00"),
                Arrays.asList(null)));


        unionService.runUni(oldList, NewList);
    }

    /**
     * добавили цены к товарам,которые еще не имеют цен ( с новым продукт-кодом),
     * поэтому ожидаем размер результирующей таблицы,больше чем старой
     */
    @Test
    public void runUni7() {

        oldList.add(new PeriodPrice(getDate("01.01.2013 00:00:00"), getDate("10.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(100, 1, 1, "133"))));
        oldList.add(new PeriodPrice(getDate("10.01.2013 00:00:00"), getDate("15.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(130, 1, 1, "133"))));
//новые
        NewList.add(new PeriodPrice(getDate("05.01.2013 00:00:00"), getDate("22.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(300, 1, 1, "233"))));


        Assert.assertFalse(unionService.runUni(oldList, NewList).size() <= oldList.size());
    }

    /**
     * добавили цены  для одного продукта ,но с другим номером или с другим отделом,
     * поэтому ожидаем размер результирующей таблицы,больше чем старой/новой
     */
    @Test
    public void runUni8() {

        //произвольный пример  - добавили цены  для одного продукта ,но с другим номером или с другим отделом

        oldList.add(new PeriodPrice(getDate("01.01.2013 00:00:00"), getDate("10.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(100, 1, 1, "133"))));
        oldList.add(new PeriodPrice(getDate("10.01.2013 00:00:00"), getDate("15.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(130, 1, 1, "133"))));
//новые
        NewList.add(new PeriodPrice(getDate("01.01.2013 00:00:00"), getDate("22.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(100, 2, 1, "133"))));
        NewList.add(new PeriodPrice(getDate("12.01.2013 00:00:00"), getDate("22.01.2013 00:00:00"),
                Arrays.asList(new DataPrice(100, 1, 2, "133"))));

        Assert.assertTrue(unionService.runUni(oldList, NewList).size() > NewList.size());
    }


    public static Date getDate(String s) {
        String pattern = "dd.MM.yyyy HH:mm:ss";
        SimpleDateFormat ft = new SimpleDateFormat(pattern);
        try {
            date = ft.parse(s);
        } catch (ParseException e) {
            throw new BadEntryDataException("Неверный формат даты");
        }
        return date;
    }


}