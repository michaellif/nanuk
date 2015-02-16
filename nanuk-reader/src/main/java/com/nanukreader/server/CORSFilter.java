/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 31, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CORSFilter implements Filter {
    // For security reasons set this regex to an appropriate value
    // example: ".*example\\.com"
    private static final String ALLOWED_DOMAINS_REGEXP = ".*";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String origin = req.getHeader("Origin");
        if (origin != null && origin.matches(ALLOWED_DOMAINS_REGEXP)) {
            resp.addHeader("Access-Control-Allow-Origin", origin);
            if ("options".equalsIgnoreCase(req.getMethod())) {
                resp.setHeader("Allow", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");
                if (origin != null) {
                    String headers = req.getHeader("Access-Control-Request-Headers");
                    String method = req.getHeader("Access-Control-Request-Method");
                    resp.addHeader("Access-Control-Allow-Methods", method);
                    resp.addHeader("Access-Control-Allow-Headers", headers);
                    // optional, only needed if you want to allow cookies.
                    resp.addHeader("Access-Control-Allow-Credentials", "true");
                    resp.setContentType("text/plain");
                }
                resp.getWriter().flush();
                return;
            }
        }

        // Fix ios6 caching post requests
        if ("post".equalsIgnoreCase(req.getMethod())) {
            resp.addHeader("Cache-Control", "no-cache");
        }

        if (filterChain != null) {
            filterChain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}