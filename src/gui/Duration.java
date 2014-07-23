/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Tiffany
 */
public class Duration {
    private String name;
    private int hours;
    private int minutes;
    private int seconds;
    private long totalMilliseconds;
    
    public Duration(String name) {
        this.name = name;
        this.hours = 0;
        this.minutes = 0;
        this.seconds = 0;
        this.totalMilliseconds = 0;
    }
    
    public Duration() {
        this("");
    }
    
    public void addHours(int h) {
        setHours(hours + h);
    }
    
    public void setHours(int h) {
        if(h >= 0) {
            hours = h;
        }
    }
    
    public int getHours() {
        return hours;
    }
    
    public double getDecimalHours() {
        //System.out.println(name+": "+hours+" h + ("+minutes+" m = "+((double)minutes/60.0)+" h) + ("+seconds+" s = "+((double)seconds/60.0)+"m = "+((double)seconds/3600.0)+"h)");
        return hours+((double)minutes+(double)seconds/60.0)/60.0;
    }
    
    public void addMinutes(int m) {
        setMinutes(minutes + m);
    }
    
    public void setMinutes(int m) {
        if(m >= 0) {
            minutes = m;
        }
        setHours((int)(minutes/60));
        minutes = minutes % 60;
    }
    
    public int getMinutes() {
        return minutes;
    }
    
    public void addSeconds(int s) {
        setSeconds(seconds + s);
        //System.out.println(name+": +"+s+"s => "+seconds+"s");
    }
    
    public void setSeconds(int s) {
        if(s >= 0) {
            seconds = s;
        }
        setMinutes((int)(seconds/60));
        seconds = seconds % 60;
    }
    
    public int getSeconds() {
        return seconds;
    }
    
    public void setTotalMilliseconds(long ms) {
        totalMilliseconds = ms;
        setSeconds((int)(totalMilliseconds/1000));
    }

    public long getTotalMilliseconds() { return totalMilliseconds; }
    
    public void addTotalMilliseconds(long ms) {
        setTotalMilliseconds(totalMilliseconds + ms);
    }
    
    public Date getTime() {
        return new GregorianCalendar(0,0,0,hours,minutes,seconds).getTime();
    }
    
    public String toString() {
        return TimerFrame.CLIENTTIME_DATEFORMAT.format(getTime());
        //return String.format("%02d", hours)+":"+String.format("%02d", minutes)+":"+String.format("%02d", seconds);
    }
    
    public String toDecimalHourString() {
        double time = getDecimalHours();
        return TimerFrame.TIME_DECIMALHOURS_DECIMALFORMAT.format(time);
    }
    
    public String toFullString() {
        if(TimerFrame.CLIENTTIME_DATEFORMAT == TimerFrame.CLIENTTIME_MAXIMIZED_DATEFORMAT) {
            return toString() + " (" + toDecimalHourString() + " hour(s))";
        } else {
            return toString();
        }
    }
}
