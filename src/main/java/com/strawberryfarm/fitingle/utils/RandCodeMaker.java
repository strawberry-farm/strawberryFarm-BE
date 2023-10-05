package com.strawberryfarm.fitingle.utils;

import java.util.Random;

public class RandCodeMaker {
	public static Integer genCertificationNumber() {
		Random rendGenerator = new Random();
		rendGenerator.setSeed(System.currentTimeMillis());
		return rendGenerator.nextInt(1000000) % 1000000;
	}

}
