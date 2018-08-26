package smu.ac.kr.johnber.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



public class DateUtil {
    /**
     * 월의 해당 주의 날짜 배열을 얻어온다.
     * @param yyyymm
     * @param weekSeq
     * @return
     */
    public static int[] getRangeDateOfWeek(String yyyymm, int weekSeq) {
        int rangeDateOfWeek [] = new int[7];

        int startDayOfWeek = dayOfWeek(yyyymm.substring(0, 4), yyyymm.substring(4, 6), "1");

        if( startDayOfWeek == 0 || weekSeq > 1 ){
            Calendar cal = converterDate(yyyymm+"01");
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            int lastDateOfMonth = getLastDateOfMonth(new SimpleDateFormat("yyyyMM").format(cal.getTime()));

            int startDate = 1 + ((weekSeq-1)*7) - startDayOfWeek;
            for( int i=0; i<7; i++ ){
                if( startDate > lastDateOfMonth ){
                    startDate = 1;
                }
                rangeDateOfWeek[i] = startDate++;
            }
        }else{
            Calendar cal = converterDate(yyyymm+"01");
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.add(Calendar.MONTH, -1);
            int lastDateOfBeforeMonth = getLastDateOfMonth(new SimpleDateFormat("yyyyMM").format(cal.getTime()));

            int startDate = (lastDateOfBeforeMonth + 1) - startDayOfWeek;
            for( int i=0; i<7; i++ ){
                if( startDate > lastDateOfBeforeMonth ){
                    startDate = 1;
                }
                rangeDateOfWeek[i] = startDate++;
            }
        }
        return rangeDateOfWeek;
    }
    /**
     * 특정날짜의  요일의 숫자를 리턴
     * 0:일요일 ~ 6:토요일
     * @return
     */
    public static int dayOfWeek(String sYear, String sMonth, String sDay) {

        int iYear = Integer.parseInt(sYear);
        int iMonth = Integer.parseInt(sMonth) - 1;
        int isDay = Integer.parseInt(sDay);

        GregorianCalendar gc = new GregorianCalendar(iYear, iMonth, isDay);

        return gc.get(gc.DAY_OF_WEEK) - 1;
    }

    //jgh
    public static int dayOfWeekForWeeklyStatistics(String sDate) {

        int iYear = Integer.parseInt(sDate.split("-")[0]);
        int iMonth = Integer.parseInt(sDate.split("-")[1]) - 1;
        int isDay = Integer.parseInt(sDate.split("-")[2]);

        GregorianCalendar gc = new GregorianCalendar(iYear, iMonth, isDay);

        return gc.get(gc.DAY_OF_WEEK) - 1;
    }


    /**
     * String 형식의 날자를 Calendar 로 변환 해준다.
     *
     * @param yyyymmdd
     * @return
     */
    public static Calendar converterDate(String yyyymmdd) {
        Calendar cal = Calendar.getInstance(); // 양력 달력
        if (yyyymmdd == null)
            return cal;

        String date = yyyymmdd.trim();
        if (date.length() != 8) {
            if (date.length() == 4)
                date = date + "0101";
            else if (date.length() == 6)
                date = date + "01";
            else if (date.length() > 8)
                date = date.substring(0, 8);
            else
                return cal;
        }

        cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

        return cal;
    }

    /**
     * 해당 월의 마지막일을 구한다.
     * @return
     */
    public static int getLastDateOfMonth() {
        return getLastDateOfMonth(new Date());
    }
    public static int getLastDateOfMonth(Date date) {
        return getLastDateOfMonth(new SimpleDateFormat("yyyyMM").format(date));
    }
    public static int getLastDateOfMonth(String yyyymm) {
        int year = Integer.parseInt(yyyymm.substring(0, 4));
        int month = Integer.parseInt(yyyymm.substring(4, 6)) - 1;

        Calendar destDate = Calendar.getInstance();
        destDate.set(year, month, 1);

        return destDate.getActualMaximum(Calendar.DATE);
    }
}
