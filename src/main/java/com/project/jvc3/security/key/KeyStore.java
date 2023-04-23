package com.project.jvc3.security.key;

import java.security.KeyPair;

public class KeyStore {
    private static KeyStore instance;
    private KeyPair keyPair;

    public KeyStore(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public static void initialize(KeyPair keyPair) {
        if (instance == null) {
            instance = new KeyStore(keyPair);
        }
    }

    public static KeyStore getInstance() {
        if (instance == null) {
            throw new IllegalStateException("KeyStore의 키가 존재하지 않습니다");
        }
        return instance;
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }
}
