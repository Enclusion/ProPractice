package net.propvp.practice.timing;

import java.text.DecimalFormat;

public abstract class AbstractTimer implements CountdownTimer {
	
    private static DecimalFormat format;
    private final boolean prefferedFormat;
    
    static {
        format = new DecimalFormat("#0.0");
    }
    
    protected AbstractTimer(final boolean prefferedFormat) {
        this.prefferedFormat = prefferedFormat;
    }
    
    @Override
    public boolean isActive() {
        return this.getTimerEnd() > System.currentTimeMillis();
    }
    
    @Override
    public long getTimeLeft() {
        return this.getTimerEnd() - System.currentTimeMillis();
    }
    
    @Override
    public abstract long getTimerEnd();
    
    public boolean getPrefferedFormat() {
        return this.prefferedFormat;
    }
    
    @Override
    public String toString() {
        return this.toString(this.getPrefferedFormat());
    }
    
    @Override
    public String toString(final boolean longFormat) {
        final long left = this.getTimeLeft();
        final long totalSecs = left / 1000L;
        return longFormat ? ((totalSecs >= 3600L) ? String.format("%02d:%02d:%02d", totalSecs / 3600L, totalSecs % 3600L / 60L, totalSecs % 60L) : String.format("%02d:%02d", totalSecs / 60L, totalSecs % 60L)) : (String.valueOf(String.valueOf(AbstractTimer.format.format(left / 1000.0f))) + "s");
    }
    
}
