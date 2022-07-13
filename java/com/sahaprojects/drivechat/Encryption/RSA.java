package com.sahaprojects.drivechat.Encryption;


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {

    KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey;
    byte[] encryptedBytes, decryptedBytes;
    Cipher encryptcipher, decryptcipher;
    String encrypted, decrypted;





    public String Encrypt (String plain)  {

        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
            encryptcipher = Cipher.getInstance("RSA");
            encryptcipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encryptedBytes = encryptcipher.doFinal(plain.getBytes());

            encrypted = bytesToString(encryptedBytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return encrypted;
    }

    public String Decrypt (String result) {


        try {
            decryptcipher=Cipher.getInstance("RSA");
            decryptcipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedBytes = decryptcipher.doFinal(stringToBytes(result));
            decrypted = new String(decryptedBytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return decrypted;


    }

    public  String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }

    public  byte[] stringToBytes(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }
}