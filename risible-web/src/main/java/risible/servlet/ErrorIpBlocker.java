package risible.servlet;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class ErrorIpBlocker implements Filter, BlockErrors {
    private static final Logger log = Logger.getLogger(ErrorIpBlocker.class);
    private final List<IpTime> iptime = new LinkedList();
    private final Set ips = new HashSet();
    private long windowSize = 10 * 60 * 1000;
    private int threshold = 5;
    private int reapInterval = 60000;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void init() {
        Runnable r = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        reap();
                        Thread.sleep(reapInterval);
                    } catch (ThreadDeath td) {
                        throw td;
                    } catch (Throwable t) {
                        //
                    }
                }
            }
        };

        new Thread(r).start();
    }

    private synchronized void reap() {
        Date now = new Date();
        now.setTime(System.currentTimeMillis() - windowSize);
        for (Iterator<IpTime> it = iptime.iterator(); it.hasNext(); ) {
            IpTime ipTime = it.next();
            if (ipTime.isBefore(now)) {
                it.remove();
            }
        }

        Map<String, Integer> count = new HashMap();
        for (IpTime ipTime : iptime) {
            inc(count, ipTime.ip);
        }

        Set updated = new HashSet();
        for (Entry<String, Integer> entry : count.entrySet()) {
            if (entry.getValue() > threshold) {
                updated.add(entry.getKey());
            }
        }

        ips.clear();
        ips.addAll(updated);
        if (ips.size() > 0) {
            log.warn(ips);
        } else {
            log.info("no blocked ips");
        }
    }

    private void inc(Map<String, Integer> count, String ip) {
        Integer c = count.get(ip);
        if (c == null) {
            c = 0;
        }
        count.put(ip, c + 1);
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String ip = RequestHelper.getForwardedForOrRemoteIp(req);
        if (ips.contains(ip)) {
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }

    public Set blocked() {
        return ips;
    }

    public synchronized void error(String ip) {
        log.info("got error from " + ip + " at " + new Date());
        log.info("error collection is now " + iptime);
        iptime.add(new IpTime(new Date(), ip));
    }

    public void setWindowSize(long windowSize) {
        this.windowSize = windowSize;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setReapInterval(int reapInterval) {
        this.reapInterval = reapInterval;
    }

    static class IpTime {
        public Date date;
        public String ip;

        public IpTime(Date date, String ip) {
            this.date = date;
            this.ip = ip;
        }

        public boolean isBefore(Date d) {
            return date.getTime() < d.getTime();
        }

        public String toString() {
            return ip + "@" + date;
        }
    }
}
