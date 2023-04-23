package com.project.jvc3.security.key;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyFileUtils {
    public static void saveKeyPair(String privateKeyPath, String publicKeyPath, PrivateKey privateKey, PublicKey publicKey) throws IOException {
        String privateKeyPem = PemUtils.keyToPem(privateKey, "RSA PRIVATE KEY");
        String publicKeyPem = PemUtils.keyToPem(publicKey, "RSA PUBLIC KEY");

        Files.write(Paths.get(privateKeyPath), privateKeyPem.getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(publicKeyPath), publicKeyPem.getBytes(StandardCharsets.UTF_8));
    }

    public static KeyPair loadKeyPair(String privateKeyPath, String publicKeyPath) throws Exception {
        String privateKeyPem = new String(Files.readAllBytes(Paths.get(privateKeyPath)), StandardCharsets.UTF_8);
        String publicKeyPem = new String(Files.readAllBytes(Paths.get(publicKeyPath)), StandardCharsets.UTF_8);

        PrivateKey privateKey = PemUtils.pemToPrivateKey(privateKeyPem);
        PublicKey publicKey = PemUtils.pemToPublicKey(publicKeyPem);

        return new KeyPair(publicKey, privateKey);
    }
}