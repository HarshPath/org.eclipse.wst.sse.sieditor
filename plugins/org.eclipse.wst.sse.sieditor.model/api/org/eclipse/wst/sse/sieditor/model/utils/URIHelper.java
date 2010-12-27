/*******************************************************************************
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Emil Simeonov - initial API and implementation.
 *    Dimitar Donchev - initial API and implementation.
 *    Dimitar Tenev - initial API and implementation.
 *    Nevena Manova - initial API and implementation.
 *    Georgi Konstantinov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import org.eclipse.core.runtime.URIUtil;

public final class URIHelper {
    private static final String ENC = "UTF-8"; //$NON-NLS-1$

    public static URI createEncodedURI(String location) throws UnsupportedEncodingException, URISyntaxException {
        return URIUtil.fromString(location);
    }

    public static URI createEncodedURI(org.eclipse.emf.common.util.URI uri) throws UnsupportedEncodingException,
            URISyntaxException {
        return createEncodedURI(uri.toString());
    }

    public static String decodeURI(java.net.URI uri) throws UnsupportedEncodingException {
        return decodeURI(uri.toString());
    }

    public static String decodeURI(String locationUri) throws UnsupportedEncodingException {
        return URLDecoder.decode(locationUri, ENC);
    }
    
    public static org.eclipse.emf.common.util.URI convert(URI uri) {
        return org.eclipse.emf.common.util.URI.createURI(uri.toString());
    }

    private URIHelper() {
    }
}
