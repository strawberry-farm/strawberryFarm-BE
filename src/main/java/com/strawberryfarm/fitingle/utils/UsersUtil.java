package com.strawberryfarm.fitingle.utils;

public class UsersUtil {
	public static boolean checkEmailValid(String email) {
		return email.matches("^[a-zA-Z0-9]([-_.]?[0-9A-Za-z])*@[a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$");
	}

	public static boolean checkPasswordValid(String password) {
		return password.matches("^[a-z0-9A-Z~!@#$%^&*()_=+,.?]{6,24}$");
	}
}
