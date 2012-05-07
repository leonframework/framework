package io.leon.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateUtils {

    public static String timeInLongToReadableString(long longTime) {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(longTime);

        StringBuilder sb = new StringBuilder();
        sb.append(c.get(Calendar.YEAR));
        sb.append("-");
        sb.append(c.get(Calendar.MONTH) + 1);
        sb.append("-");
        sb.append(c.get(Calendar.DAY_OF_MONTH));
        sb.append(" ");
        sb.append(c.get(Calendar.HOUR_OF_DAY));
        sb.append(":");
        sb.append(c.get(Calendar.MINUTE));
        sb.append(":");
        sb.append(c.get(Calendar.SECOND));
        sb.append(".");
        sb.append(c.get(Calendar.MILLISECOND));

        return sb.toString();
    }
}
