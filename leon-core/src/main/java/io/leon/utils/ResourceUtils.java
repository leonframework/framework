/*
 * Copyright (c) 2011 WeigleWilczek and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/*
 * Copyright (c) 2011 WeigleWilczek and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package io.leon.utils;

import com.google.common.io.CharStreams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ResourceUtils {

    public static String inputStreamToString(InputStream stream) {
        try {
            return CharStreams.toString(new InputStreamReader(stream));
        } catch (IOException e) {
            throw new RuntimeException("Error while converting InputStream to String", e);
        }
    }

    public static InputStream stringToInputStream(String string) {
        return new ByteArrayInputStream(string.getBytes());
    }

}
