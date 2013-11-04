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

package risible.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

public class SessionMap implements Map {
    private HttpServletRequest req;

    public SessionMap(HttpServletRequest req) {
        this.req = req;
    }

    public int size() {
        return keySet().size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    public boolean containsValue(Object value) {
        return false;
    }

    public Object get(Object key) {
        HttpSession session = getSession(false);
        if (session == null) {
            return null;
        }
        return session.getAttribute((String) key);
    }

    public Object put(Object key, Object value) {
        Object before = get(key);
        getSession(true).setAttribute((String) key, value);
        return before;
    }

    public Object remove(Object key) {
        Object before = get(key);
        if (before == null) {
            return before;
        }
        getSession(true).removeAttribute((String) key);
        return before;
    }

    public void putAll(Map t) {
        for (Object o : t.keySet()) {
            put(o, t.get(o));
        }
    }

    public void clear() {
        for (Object o : keySet()) {
            remove(o);
        }
    }

    public Set keySet() {
        Set result = new HashSet();
        HttpSession session = getSession(false);
        if (session == null) {
            return result;
        }
        Enumeration e = session.getAttributeNames();
        while (e.hasMoreElements()) {
            result.add(e.nextElement());
        }
        return result;
    }

    public Collection values() {
        Set result = new HashSet();
        HttpSession session = getSession(false);
        if (session == null) {
            return result;
        }
        Enumeration e = session.getAttributeNames();
        while (e.hasMoreElements()) {
            result.add(get(e.nextElement()));
        }
        return result;
    }

    public Set entrySet() {
        Set result = new HashSet();
        HttpSession session = getSession(false);
        if (session == null) {
            return result;
        }
        Enumeration e = session.getAttributeNames();
        while (e.hasMoreElements()) {
            result.add(new SessionMapEntry(e.nextElement()));
        }
        return result;
    }

    private HttpSession getSession(boolean create) {
        return req.getSession(create);
    }

    private class SessionMapEntry implements Entry {
        Object key;

        public SessionMapEntry(Object key) {
            this.key = key;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return get(key);
        }

        public Object setValue(Object value) {
            return put(key, value);
        }
    }
}
