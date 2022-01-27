/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableEncryptableProperties
@SpringBootApplication(scanBasePackages = { "com.collibra.marketplace" })
public class Application {

	/**
	 * Integration starting point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("jasypt.encryptor.algorithm", "PBEWithHmacSHA512AndAES_256");
		System.setProperty("jasypt.encryptor.iv-generator-classname", "org.jasypt.iv.RandomIvGenerator");
		SpringApplication.run(Application.class, args);
	}

}