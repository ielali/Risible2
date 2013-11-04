// Copyright 2008 Conan Dalton and Jean-Philippe Hallot
//
// This file is part of risible-web.
//
// risible-web is free software: you can redistribute it and/or modify
// it under the terms of version 3 of the GNU Lesser General Public License as published by
// the Free Software Foundation
//
// risible-db is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// Copies of the GNU General Public License and the GNU Lesser General Public License
// are distributed with this software, see /GPL.txt and /LGPL.txt at the
// root of this distribution.
//

package risible.core.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RequestLogBean {
    private String logType;
    private String error;
    private String invocation;
    private String controller;
    private String uri;
    private String qs;
    private String remoteIp;
    private String referrer;
    private String userAgent;
    private String acceptLanguage;
    private Long elapsed;
    private Long year;
    private Long month;
    private Long dayOfMonth;
    private Long dayOfWeek;
    private Long hour;
    private Date date;

    public RequestLogBean() {
    }

    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public Long getElapsed() {
        return elapsed;
    }

    public void setElapsed(Long elapsed) {
        this.elapsed = elapsed;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getQs() {
        return qs;
    }

    public void setQs(String qs) {
        this.qs = qs;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public Long getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(Long dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public Long getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Long dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Long getHour() {
        return hour;
    }

    public void setHour(Long hour) {
        this.hour = hour;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public void setInvocation(String invocation) {
        this.invocation = invocation;
    }

    public String getInvocation() {
        return invocation;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public void setDateInfo(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        setDate(gc.getTime());
        setYear((long) gc.get(Calendar.YEAR));
        setMonth(1 + (long) gc.get(Calendar.MONTH));
        setDayOfMonth((long) gc.get(Calendar.DAY_OF_MONTH));
        setDayOfWeek((long) gc.get(Calendar.DAY_OF_WEEK));
        setHour((long) gc.get(Calendar.HOUR_OF_DAY));
    }

    public void setProblem(Throwable t) {
        if (t == null) {
            return;
        }
        StringWriter s = new StringWriter();
        t.printStackTrace(new PrintWriter(s));
        this.setError(s.toString());
    }
}
