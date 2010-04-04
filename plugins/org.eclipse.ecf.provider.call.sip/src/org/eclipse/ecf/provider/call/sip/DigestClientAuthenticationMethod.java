/***********************************************************************************
 * Copyright (c) 2009 Harshana Eranga Martin and others. All rights reserved. This 
 * program and the accompanying materials are made available under the terms of 
 * the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Harshana Eranga Martin <harshana05@gmail.com> - initial API and implementation
************************************************************************************/
package org.eclipse.ecf.provider.call.sip;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestClientAuthenticationMethod implements ClientAuthenticationMethod {

	private String realm;
	private String userName;
	private String uri;
	private String nonce;
	private String password;
	private String method;
	private String cnonce;
	private MessageDigest messageDigest;

	/**
	* to hex converter
	*/
	private static final char[] toHex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	/**
	 * convert an array of bytes to an hexadecimal string
	 * @return a string
	 * @param b bytes array to convert to a hexadecimal
	 * string
	 */

	public static String toHexString(byte b[]) {
		int pos = 0;
		char[] c = new char[b.length * 2];
		for (int i = 0; i < b.length; i++) {
			c[pos++] = toHex[(b[i] >> 4) & 0x0F];
			c[pos++] = toHex[b[i] & 0x0f];
		}
		return new String(c);
	}

	public void initialize(String realm, String userName, String uri, String nonce, String password, String method, String cnonce, String algorithm) throws Exception {
		if (realm == null)
			throw new Exception("The realm parameter is null"); //$NON-NLS-1$
		this.realm = realm;
		if (userName == null)
			throw new Exception("The userName parameter is null"); //$NON-NLS-1$
		this.userName = userName;
		if (uri == null)
			throw new Exception("The uri parameter is null"); //$NON-NLS-1$
		this.uri = uri;
		if (nonce == null)
			throw new Exception("The nonce parameter is null"); //$NON-NLS-1$
		this.nonce = nonce;
		if (password == null)
			throw new Exception("The password parameter is null"); //$NON-NLS-1$
		this.password = password;
		if (method == null)
			throw new Exception("The method parameter is null"); //$NON-NLS-1$
		this.method = method;
		this.cnonce = cnonce;
		if (algorithm == null)
			throw new Exception("The algorithm parameter is null"); //$NON-NLS-1$
		try {
			messageDigest = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("DEBUG, DigestClientAuthenticationMethod, initialize(): " + //$NON-NLS-1$
					"ERROR: Digest algorithm does not exist."); //$NON-NLS-1$
			throw new Exception("ERROR: Digest algorithm does not exist."); //$NON-NLS-1$
		}
	}

	/** 
	 * generate the response
	 */
	public String generateResponse() {
		if (userName == null) {
			System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(): " + "ERROR: no userName parameter"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		if (realm == null) {
			System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(): " + "ERROR: no realm parameter"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}

		System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(): " + "Trying to generate a response for the user: " + userName + " , with " + "the realm: " + realm); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		if (password == null) {
			System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(): " + "ERROR: no password parameter"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		if (method == null) {
			System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(): " + "ERROR: no method parameter"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		if (uri == null) {
			System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(): " + "ERROR: no uri parameter"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		if (nonce == null) {
			System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(): " + "ERROR: no nonce parameter"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		if (messageDigest == null) {
			System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(): " + "ERROR: the algorithm is not set"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}

		/*******    GENERATE RESPONSE      ************************************/
		System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(), userName:" + userName + "!"); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(), realm:" + realm + "!"); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(), password:" + password + "!"); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(), uri:" + uri + "!"); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(), nonce:" + nonce + "!"); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(), method:" + method + "!"); //$NON-NLS-1$ //$NON-NLS-2$
		// A1
		String A1 = userName + ":" + realm + ":" + password; //$NON-NLS-1$ //$NON-NLS-2$
		byte mdbytes[] = messageDigest.digest(A1.getBytes());
		String HA1 = toHexString(mdbytes);
		System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(), HA1:" + HA1 + "!"); //$NON-NLS-1$ //$NON-NLS-2$
		//A2
		String A2 = method.toUpperCase() + ":" + uri; //$NON-NLS-1$
		mdbytes = messageDigest.digest(A2.getBytes());
		String HA2 = toHexString(mdbytes);
		System.out.println("DEBUG, DigestClientAuthenticationMethod, generateResponse(), HA2:" + HA2 + "!"); //$NON-NLS-1$ //$NON-NLS-2$
		//KD
		String KD = HA1 + ":" + nonce; //$NON-NLS-1$
		if (cnonce != null) {
			if (cnonce.length() > 0)
				KD += ":" + cnonce; //$NON-NLS-1$
		}
		KD += ":" + HA2; //$NON-NLS-1$
		mdbytes = messageDigest.digest(KD.getBytes());
		String response = toHexString(mdbytes);

		System.out.println("DEBUG, DigestClientAlgorithm, generateResponse():" + " response generated: " + response); //$NON-NLS-1$ //$NON-NLS-2$

		return response;
	}
}
