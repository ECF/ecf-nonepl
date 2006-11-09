package org.eclipse.ecf.internal.provider.jms;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.core.runtime.Platform;

public class Trace {
    public static final String tracePrefix = "(trace)";
    
    public static boolean ON = false;
    protected static boolean isEclipse = false;
    protected static String pluginName = "";
    protected static String debugPrefix = "/debug/";
    static {
        try {
            ON = Platform.inDebugMode();
            String bundleName = JmsPlugin.getDefault().getBundle().getSymbolicName();
            String val = System.getProperty(bundleName+".Trace");
            if (val != null) {
                setTrace(true);
                isEclipse = false;
                // No eclipse Platform available
                System.out.println("WARNING:  Eclipse platform not available for trace...overridden by system property org.eclipse.ecf.Trace");
            } else {
                isEclipse = true;
                pluginName = bundleName;
            }
        } catch (Exception e) {
            try {
                String val = System.getProperty("org.eclipse.ecf.internal.provider.jms.Trace");
                if (val != null) {
                    setTrace(true);
                    isEclipse = false;
                    // No eclipse Platform available
                    System.out.println("WARNING:  Eclipse platform not available for trace...using system.out for org.eclipse.ecf");
                } else {
                    System.out.println(Trace.class.getName()+": OFF");
                }
            } catch (Exception except) {
            }
        }
    }
    public static void setTrace(boolean on) {
        ON = on;
    }

    public static Trace create(String key) {
        if (isEclipse) {
            String res = "";
            try {
                res = Platform.getDebugOption(pluginName + debugPrefix + key);
            } catch (Exception e) {
                // ignore...this means that the Platform class not found.
            }
            if (res != null) {
                Boolean on = new Boolean(res);
                if (on.booleanValue())
                    return new Trace(pluginName + "(" + key + ")");
                else
                    return null;
            } else {
                return null;
            }
        } else
            return new Trace(key);
    }

    String name;

    public void dumpStack(Throwable e, String msg) {
        msg(msg);
        e.printStackTrace(System.err);
    }

    public void msg(String msg) {
        StringBuffer sb = new StringBuffer(name);
        sb.append(getTimeString()).append(msg);
        System.out.println(sb.toString());
    }

    protected static String getTimeString() {
        Date d = new Date();
        SimpleDateFormat df = new SimpleDateFormat("[MM/dd/yy;HH:mm:ss:SSS]");
        return df.format(d);
    }

    protected Trace(String str) {
        name = tracePrefix+str;
    }
    public static String convertStringAToString(String [] strings) {
        if (strings==null) return "";
        StringBuffer sb = new StringBuffer();
        for(int i=0; i < strings.length; i++) {
            if (strings[i]==null) sb.append("(null)");
            else sb.append(strings[i]);
            if (i != (strings.length-1)) sb.append(";");
        }
        return sb.toString();
    }
    public static String convertObjectAToString(Object [] objs) {
        if (objs==null) return "";
        StringBuffer sb = new StringBuffer();
        for(int i=0; i < objs.length; i++) {
            if (objs[i]==null) sb.append("(null)");
            else sb.append(objs[i].toString());
            if (i != (objs.length-1)) sb.append(";");
        }
        return sb.toString();
    }

    public static void setThreadDebugGroup(Object obj) {
        // Do nothing
    }
}