package com.micaja.servipunto.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Formatter;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.util.Base64;


public final class KeyGenEncryption {

/**
         * read Public Key From File
         *
         * @return PublicKey
         * @throws IOException
         */
        public PublicKey readPublicKeyFromFile() throws IOException {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                InputStream is =  getClass().getResourceAsStream("/assets/BrokerPublic.key");//getResources().openRawResource(R.raw.brokerpublic);
                ois = new ObjectInputStream(is);
                BigInteger modulus = (BigInteger) ois.readObject();
                BigInteger exponent = (BigInteger) ois.readObject();

                //Get Public Key
                RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
                KeyFactory fact = KeyFactory.getInstance("RSA");
                PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);

                return publicKey;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ois != null) {
                    ois.close();
                    if (fis != null) {
                        fis.close();
                    }
                }
            }
            return null;
        }

        public String encryptData(String data) throws IOException {
            byte[] dataToEncrypt = data.getBytes("UTF-8");
            byte[] encryptedData = null;
            String encryptedString = null;
            try {
                PublicKey pubKey = this.readPublicKeyFromFile();
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, pubKey);
                encryptedData = cipher.doFinal(dataToEncrypt);
                encryptedString = Base64.encodeToString(encryptedData,Base64.NO_WRAP);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return encryptedString;
        }

        public String generarClave() {

            String stringkey = null;
            try {
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(128);
                SecretKey secretKey = keyGen.generateKey();
                stringkey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);

            } catch (NoSuchAlgorithmException e) {
            }

            return stringkey;
        }

        public String sha1(String password) {
            String sha1 = "";
            try {
                MessageDigest crypt = MessageDigest.getInstance("SHA-1");
                crypt.reset();
                crypt.update(password.getBytes("UTF-8"));
                sha1 = byteToHex(crypt.digest());
            } catch (NoSuchAlgorithmException e) {
                Logger.getAnonymousLogger().severe(e.getMessage());
            } catch (UnsupportedEncodingException e) {
                Logger.getAnonymousLogger().severe(e.getMessage());
            }
            return sha1;
        }

        private String byteToHex(final byte[] hash) {
            Formatter formatter = new Formatter();
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            String result = formatter.toString();
            formatter.close();
            return result;
        }

}
