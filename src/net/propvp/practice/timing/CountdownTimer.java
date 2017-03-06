package net.propvp.practice.timing;

public interface CountdownTimer {
	
    boolean isActive();
    
    long getTimeLeft();
    
    long getTimerEnd();
    
    String toString();
    
    String toString(final boolean p0);
    
}