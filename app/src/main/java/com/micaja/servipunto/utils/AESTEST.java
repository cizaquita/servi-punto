package com.micaja.servipunto.utils;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

/**
 * Aes encryption
 */
public final class AESTEST {

	private static SecretKeySpec secretKey;
	private static byte[] key;
	private static String decryptedString;
	private static String encryptedString;

	public static void setKey(String myKey) {

		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			System.out.println(key.length);
			// sha = MessageDigest.getInstance("SHA-1");
			// key = sha.digest(key);
			// key = Arrays.copyOf(key, 16); // use only first 128 bit
			System.out.println(key.length);
			System.out.println(new String(key, "UTF-8"));
			secretKey = new SecretKeySpec(key, "AES");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static SecretKeySpec AESkeyFromString(String base64) {
		byte[] decode = android.util.Base64.decode(base64, Base64.NO_WRAP);
		try {
			return new SecretKeySpec(decode, "AES");
		} catch (Exception e) {
			return null;
		}
	}

	public static String getDecryptedString() {
		return decryptedString;
	}

	public static void setDecryptedString(String decryptedString) {
		AESTEST.decryptedString = decryptedString;
	}

	public static String getEncryptedString() {
		return encryptedString;
	}

	public static void setEncryptedString(String encryptedString) {
		AESTEST.encryptedString = encryptedString;
	}

	public static String encrypt(String strToEncrypt, String strPssword) {

		AES.setKey(strPssword);
		// AES.encrypt(strToEncrypt.trim());

		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return new String(Base64.encode(
					cipher.doFinal(strToEncrypt.getBytes("UTF-8")),
					Base64.NO_WRAP));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String AESCrypt(String password, SecretKeySpec key) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return (Base64.encodeToString(
					cipher.doFinal(password.getBytes("UTF-8")), Base64.NO_WRAP));
		} catch (Exception e) {
			System.out.println("Error while Crypting: " + e.toString());
		}
		return null;
	}

	public static String encrypt(String strToEncrypt, SecretKey secretKey) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return new String(Base64.encode(
					cipher.doFinal(strToEncrypt.getBytes("UTF-8")),
					Base64.NO_WRAP));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String decrypt(String strToDecrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			setDecryptedString(new String(cipher.doFinal(Base64.decode(
					strToDecrypt.getBytes(), Base64.NO_WRAP))));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}

	public static String AESDecrypt(String ciphertext, SecretKeySpec key) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, key);
			System.out.println("llave que llega "
					+ Base64.encodeToString(key.getEncoded(), Base64.NO_WRAP));
			return (new String(cipher.doFinal(Base64.decode(
					ciphertext.getBytes("UTF-8"), Base64.NO_WRAP))));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return "";

	}
}
