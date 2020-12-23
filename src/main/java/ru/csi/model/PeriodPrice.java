package ru.csi.model;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PeriodPrice {
    private String pattern = "dd.MM.yyyy HH:mm:ss";
    private SimpleDateFormat ft = new SimpleDateFormat(pattern);
    private Date start;
    private Date end;
    private List<DataPrice> valueS;


    public PeriodPrice(Date start, Date end, List<DataPrice> valueS) {
        this.start = start;
        this.end = end;
        this.valueS = valueS;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public List<DataPrice> getValueS() {
        return valueS;
    }

    @Override
    public String toString() {
        String pattern = "dd.MM.yyyy HH:mm:ss";
        SimpleDateFormat ft = new SimpleDateFormat(pattern);
        return "PriceS{" +
                "start=" + ft.format(start) +
                ", end=" + ft.format(end) +
                ", valueS=" + valueS +
                '}';
    }


}