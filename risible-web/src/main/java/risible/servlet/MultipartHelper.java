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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MultipartHelper {
    public static Map parseMulti(HttpServletRequest req) throws FileUploadException {
        Map<String, Object> result = new TreeMap();
        if (!ServletFileUpload.isMultipartContent(req)) {
            return result;
        }

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = upload.parseRequest(req);

        for (FileItem item : items) {
            String fieldName = item.getFieldName();
            if (!result.containsKey(fieldName)) {
                result.put(fieldName, new ArrayList());
            }
            List list = (List) result.get(fieldName);
            if (item.isFormField()) {
                list.add(item.getString());
            } else {
                list.add(item);
            }
        }

        for (Map.Entry<String, Object> entry : result.entrySet()) {
            List v = (List) entry.getValue();
            entry.setValue(v.toArray());
        }

        return result;
    }
}
