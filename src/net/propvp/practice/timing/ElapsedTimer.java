package net.propvp.practice.timing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ElapsedTimer {
	
	protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    protected static final TimeUnit UNIT = TimeUnit.NANOSECONDS;
    private long initialTime;

    public ElapsedTimer() {
        initialTime = System.nanoTime();
    }
    
    public void reset() {
    	initialTime = System.nanoTime();
    }

    public long elapsedTime() {
        return System.nanoTime() - initialTime;
    }

    public String toString() {
        return formatTime(elapsedTime(), UNIT, DATE_FORMAT);
    }

    private static int adjustTime(long timestamp, TimeUnit from, TimeUnit to) {
        return (int) to.convert(timestamp, from);
    }

    public static String formatTime(long timestamp, TimeUnit unit, DateFormat dateFormat) {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.SECOND, adjustTime(timestamp, unit, TimeUnit.SECONDS));
        timestamp -= TimeUnit.SECONDS.toNanos(TimeUnit.NANOSECONDS.toSeconds(timestamp));

        cal.set(Calendar.MINUTE, adjustTime(timestamp, unit, TimeUnit.MINUTES));
        timestamp -= TimeUnit.MINUTES.toNanos(TimeUnit.NANOSECONDS.toMinutes(timestamp));

        cal.set(Calendar.HOUR_OF_DAY, adjustTime(timestamp, unit, TimeUnit.HOURS));
        timestamp -= TimeUnit.HOURS.toNanos(TimeUnit.NANOSECONDS.toHours(timestamp));

        return dateFormat.format(cal.getTime());
    }
	
}