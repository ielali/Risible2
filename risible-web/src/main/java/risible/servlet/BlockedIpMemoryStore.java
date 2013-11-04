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

import java.util.HashSet;
import java.util.Set;

/**
 * This implementation will remember ips that have been blocked. Warning: use with caution. Use of this
 * implementation allows an attacker to publish a link to http://yoursite.example.com/illegal_uri.php ,
 * thereby blocking your entire site for any user who accesses it via that link. This includes search
 * engine spiders. You might prefer to implement a refined version that only blocks repeat offenders.
 */
public class BlockedIpMemoryStore implements BlockedIpStore {
    private Set ips = new HashSet();

    /**
     * @param ip the ip address to check
     * @return true if the parameter was previously a parameter to #block(ip)
     */
    public boolean isBlocked(String ip) {
        return ips.contains(ip);
    }

    /**
     * Subsequent calls to isBlocked(ip) will return true for the ip passed here
     *
     * @param ip the ip to block
     */
    public void block(String ip) {
        ips.add(ip);
    }

    public void unblock(String ip) {
        ips.remove(ip);
    }
}
