package ru.csi.service;


import ru.csi.exception.BadEntryDataException;
import ru.csi.model.DataPrice;
import ru.csi.model.PeriodPrice;


import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация класса объединения таблиц
 *
 * @author HoshArt
 * @version 1.3
 */


public class UnionServiceImpl implements UnionService {

    /**
     * Лист цен ,которые нужно объединять по периодам
     */
    static List<PeriodPrice> firstUnionList = new ArrayList<>();

    /**
     * Лист цен,которые находятся в единственном экземпляре  в старых или новых ценах /не нужно объединять по периодам
     */
    static List<PeriodPrice> lonelyPriceList = new ArrayList<>();


    /**
     * Метод ,в котором формируются два листа: лист цен,которые хранятся в единственном экземпляре, и лист цен,который нужно будет объеденить по периодам
     *
     * @param oldList - лист старых цен
     * @param newList - лист новых цен
     * @return возвращает лист  цен ,которые нужно объединить по ценам
     */
    public List<PeriodPrice> uniList(List<PeriodPrice> oldList, List<PeriodPrice> newList) {

        boolean isAbsent;


        /* ищем цены,которые находятся в единственном экземпляре */
        for (PeriodPrice pO : oldList) {
            isAbsent = false;
            isAbsent = newList.stream().noneMatch(price -> isPriceEq(pO, price));   //если в новых записях нет ни одной старой то true
            if (isAbsent) lonelyPriceList.add(pO);
        }
        for (PeriodPrice pN : newList) {
            isAbsent = false;
            isAbsent = oldList.stream().noneMatch(price -> isPriceEq(pN, price));   //если в новых записях нет ни одной новой то true
            if (isAbsent) lonelyPriceList.add(pN);
        }

        /* ищем цены,которые нужно объединить/обработать*/
        for (PeriodPrice pO : oldList) {
            for (PeriodPrice pN : newList) {
                if (isPriceEq(pO, pN)) {
                    firstUnionList.add(pN);
                    firstUnionList.add(pO);
                }
            }
        }
        return firstUnionList.stream().distinct().collect(Collectors.toList());
    }


    /**
     * Метод получения объединенного по периодам листа
     *
     * @param input - лист цен для первичного объединения
     * @return возвращает лист объединенных цен в отсортированном по начальной дате виде
     */
    public List<PeriodPrice> convert(Collection<PeriodPrice> input) {

        NavigableMap<Date, List<DataPrice>> map = new TreeMap<>();
        map.put(new Date(Long.MIN_VALUE), new ArrayList<>());

        for (PeriodPrice periodPrice : input) {
            if (!map.containsKey(periodPrice.getStart())) {
                map.put(periodPrice.getStart(), new ArrayList<>(map.lowerEntry(periodPrice.getStart()).getValue()));
            }
            if (!map.containsKey(periodPrice.getEnd())) {
                map.put(periodPrice.getEnd(), new ArrayList<>(map.lowerEntry(periodPrice.getEnd()).getValue()));
            }
            for (List<DataPrice> set : map.subMap(periodPrice.getStart(), periodPrice.getEnd()).values()) {
                set.addAll(periodPrice.getValueS());
            }
        }
        List<PeriodPrice> result = new ArrayList<>();
        Date prev = null;
        List<DataPrice> pL = new ArrayList<>();
        for (Map.Entry<Date, List<DataPrice>> entry : map.entrySet()) {
            if (!pL.isEmpty()) {
                result.add(new PeriodPrice(prev, entry.getKey(), pL));
            }
            prev = entry.getKey();
            pL = entry.getValue();
        }
        firstUnionList = result.stream().sorted(Comparator.comparing(PeriodPrice::getStart)).collect(Collectors.toList());
        return firstUnionList;
    }


    /**
     * Метод,проверяющий равенство цен
     *
     * @param pO- первая цена
     * @param p1  - вторая цена
     * @return истинность равенства цен
     */
    private static boolean isPriceEq(PeriodPrice pO, PeriodPrice p1) {
        return (pO.getValueS().get(0).getProduct_code().equals(p1.getValueS().get(0).getProduct_code())
                && pO.getValueS().get(0).getDepart() == p1.getValueS().get(0).getDepart()
                && pO.getValueS().get(0).getNumber() == p1.getValueS().get(0).getNumber());
    }


    /**
     * Метод,конвертирующий класс цен для обработки в класс цен для результирующего объединения и  вывода
     *
     * @param priceListBefore- первая цена
     * @return priceResList - лист после обработки и добавления цен,находящихся в единственном экземпляре
     */
    private static List<DataPrice> filterDataList(List<PeriodPrice> priceListBefore) {
        List<DataPrice> dataPriceList = new ArrayList<>();
        for (PeriodPrice p : priceListBefore) {
            dataPriceList.add(new DataPrice(p.getValueS().get(0).getProduct_code(),
                    p.getValueS().get(0).getNumber(), p.getValueS().get(0).getDepart(),
                    p.getStart(), p.getEnd(), p.getValueS().get(0).getValue()));
        }
        for (PeriodPrice p : lonelyPriceList) {
            dataPriceList.add(new DataPrice(p.getValueS().get(0).getProduct_code(),
                    p.getValueS().get(0).getNumber(), p.getValueS().get(0).getDepart(),
                    p.getStart(), p.getEnd(), p.getValueS().get(0).getValue()));
        }
        return dataPriceList;
    }

    /**
     * Метод,совершающий объединение по одинаковым ценам и пересеченным периодам
     *
     * @param preResList- конвертированный и объединенный по одинаковым ценам лист,но ещ не побъединенный по периодам
     * @return возвращает полностью объедененный по ценам и периодам лист в отсортированном виде
     */

    private static List<DataPrice> uniDatePrice(List<DataPrice> preResList) {

        /* результирующий лист цен,объедененных по ценам и периодам*/
        List<DataPrice> unionList = new ArrayList<>();
        /* лист конвертированных цен,которые встречаются в единственном экземпляре*/
        List<DataPrice> lonelyConvertList = new ArrayList<>();
        /* поиск минимума и максимума даты для установления полного периода действия*/
        Date dateMin = new Date(Long.MAX_VALUE);
        Date dateMax = new Date(Long.MIN_VALUE);
        for (int i = 0; i < preResList.size(); i++) {
            for (int j = i + 1; j < preResList.size(); j++) {
                DataPrice p1 = preResList.get(i);
                DataPrice p2 = preResList.get(j);
                if (isEqPRes(p1, p2)) {
                    if (isValueEq(p1, p2)) {
                        if (isResInter(p1, p2)) {
                            dateMin = p1.getBeginD().before(dateMin) ? p1.getBeginD() : dateMin;
                            dateMax = p2.getEndD().after(dateMax) ? p2.getEndD() : dateMax;
                            if (j + 1 == preResList.size() || preResList.get(j).getValue() != preResList.get(j + 1).getValue()) {
                                p1.setBeginD(dateMin);
                                p1.setEndD(dateMax);
                                dateMin = new Date(Long.MAX_VALUE);
                                dateMax = new Date(Long.MIN_VALUE);
                                unionList.add(p1);
                                break;
                            }
                        }
                    } else break;
                }
            }
        }
        //добавляем конвертированные цены,которые находятся в единтсвенном экземпляре к результирующему листу
        for (DataPrice dataPrice : preResList) {
            boolean isAbsent = false;
            isAbsent = unionList.stream().noneMatch(price -> isEqPRes(dataPrice, price) && isValueEq(dataPrice, price));
            if (isAbsent) lonelyConvertList.add(dataPrice);
        }

        //объединяем листы
        unionList.addAll(lonelyConvertList);
        return unionList.stream().distinct().sorted(Comparator.comparing(DataPrice::getProduct_code).
                thenComparing(DataPrice::getBeginD)).
                collect(Collectors.toList());
    }


    //равенство значений конвертированных цен
    static boolean isValueEq(DataPrice p1, DataPrice p2) {
        return p1.getValue() == p2.getValue();
    }

    //равенство  конвертированных цен
    static boolean isEqPRes(DataPrice p1, DataPrice p2) {
        return p1.getNumber() == p2.getNumber() && p1.getDepart() == p2.getDepart() && p1.getProduct_code().equals(p2.getProduct_code());
    }

    //пересечение  периодов цен
    static boolean isResInter(DataPrice p1, DataPrice p2) {
        return (!p1.getEndD().before(p2.getBeginD()));
    }

    //запуск потока
    public static void main(String[] args) {

    }

    /**
     * Вывод результирующего листа после всех обработок
     *
     * @param oldList- текущие цены
     * @param newList- пришедшие цены
     * @return возвращает полностью объедененный по ценам и периодам лист в отсортированном виде
     */
    @Override
    public List<DataPrice> runUni(List<PeriodPrice> oldList, List<PeriodPrice> newList) {

        if (oldList == null || newList == null) throw new BadEntryDataException("лист не может быть null");
        List<PeriodPrice> inputUnionList = convert(uniList(oldList, newList));
        List<DataPrice> outPutUnionList = filterDataList(inputUnionList);
        outPutUnionList = uniDatePrice(outPutUnionList);
        outPutUnionList.stream().forEach(System.out::println);
        return outPutUnionList;
    }
}




