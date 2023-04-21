package com.shmet.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public final class LicenseUtil {
    private LicenseUtil() {
    }

    public static String getBase64KeyStr(String path) {
        try {
            ClassPathResource classPathResource = new ClassPathResource(path);
            InputStreamReader inReader = new InputStreamReader(classPathResource.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inReader);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PrivateKey getPrivateKey(String priPath, String algo) {
        String base64KeyStr = getBase64KeyStr(priPath);
        if (StrUtil.isNotBlank(base64KeyStr)) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance(algo);
                byte[] decodeKey = Base64.decode(base64KeyStr.getBytes());
                PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decodeKey);
                return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static PublicKey getPublicKey(String pubPath, String algo) {
        String base64KeyStr = getBase64KeyStr(pubPath);
        if (StrUtil.isNotBlank(base64KeyStr)) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance(algo);
                byte[] decodeKey = Base64.decode(base64KeyStr.getBytes());
                X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decodeKey);
                return keyFactory.generatePublic(x509EncodedKeySpec);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static String encrypt(PrivateKey privateKey, PublicKey publicKey, String content) {
        RSA rsa = new RSA(privateKey, publicKey);
        return Base64.encode(rsa.encrypt(content.getBytes(), KeyType.PublicKey));
    }
}
