package ru.csi.service;

import ru.csi.model.DataPrice;
import ru.csi.model.PeriodPrice;

import java.util.Collection;
import java.util.List;

public interface UnionService {

    List<PeriodPrice> convert(Collection<PeriodPrice> input);

    List<DataPrice> runUni(List<PeriodPrice> lOld, List<PeriodPrice> lNew);

    List<PeriodPrice> uniList(List<PeriodPrice> oldList, List<PeriodPrice> newList);
}
