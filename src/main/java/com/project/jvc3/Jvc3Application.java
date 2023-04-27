package com.project.jvc3;

import com.project.jvc3.security.key.KeyFileUtils;
import com.project.jvc3.security.key.KeyGenerator;
import com.project.jvc3.security.key.KeyStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;

@EnableJpaAuditing
@SpringBootApplication
public class Jvc3Application {

    public static void main(String[] args) {
        String privateKeyPath = "private_key.pem";
        String publicKeyPath = "public_key.pem";
        Path privateKeyFile = Paths.get(privateKeyPath);
        Path publicKeyFile = Paths.get(publicKeyPath);

        try {
            KeyPair keyPair;

            if (Files.exists(privateKeyFile) && Files.exists(publicKeyFile)) {
                // 파일에서 키 쌍을 불러오기
                System.out.println("파일 존재");
                keyPair = KeyFileUtils.loadKeyPair(privateKeyPath, publicKeyPath);
            } else {
                // 새 키 쌍을 생성하고 파일에 저장
                System.out.println("파일 생성");
                keyPair = KeyGenerator.generateKeyPair();
                KeyFileUtils.saveKeyPair(privateKeyPath, publicKeyPath, keyPair.getPrivate(), keyPair.getPublic());
            }

            KeyStore.initialize(keyPair);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("키 생성및 저장에 실패하였습니다.");
            System.exit(1);
        }

        SpringApplication.run(Jvc3Application.class, args);
    }

}
