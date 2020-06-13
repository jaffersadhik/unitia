package com.winnovature.unitia.util.misc;

import java.math.BigInteger;

public class TestcaseUserName {

	private static final char[] BASE_ARRAY = "abcdefghijklmnopqrstuvwxyz0987654321~@#$%^&*()_+-=".toCharArray();

	private static final BigInteger BASELENGTH = new BigInteger(String.valueOf(BASE_ARRAY.length));

	private static final BigInteger ZERO = new BigInteger("0");

	public static String encryptString(String input) {
		input = scrambleAndShuffle(input);
		BigInteger bi = new BigInteger(input);
		final StringBuilder sb = new StringBuilder(1);
		do {
			BigInteger remIndex = bi.remainder(BASELENGTH);
			sb.insert(0, BASE_ARRAY[remIndex.intValue()]);
			bi = bi.divide(BASELENGTH);
		} while (bi.compareTo(ZERO) == 1);
		return sb.toString();
	}

	public static String decryptString(String encryptedVL) {
		char[] shortURL = encryptedVL.toCharArray();
		BigInteger id = new BigInteger("0");
		int j = 0;
		for (int i = shortURL.length - 1; i >= 0; i--) {
			int v = getIndex(shortURL[i]);
			BigInteger d = new BigInteger(Integer.toString(v));
			if (j > 0) {
				BigInteger m = BASELENGTH.pow(j);
				d = m.multiply(new BigInteger("" + v));
			}
			id = id.add(d);
			j++;
		}
		String res = id.toString();
		return unScramble(res);
	}

	private static int getIndex(char a) {
		for (int i = 0; i < BASE_ARRAY.length; i++) {
			if (BASE_ARRAY[i] == a)
				return i;
		}
		return 0;
	}

	private static String scrambleAndShuffle(String input) {
		String head = input.substring(0, 1);
		input = input.substring(1);

		char[] enc = shuffle(input.toCharArray());
		enc = rotateGivenArrayRightBy(enc, 3);
		enc = shuffle(enc);
		enc = rotateGivenArrayLeftBy(enc, 3);
		enc = shuffle(enc);
		enc = rotateGivenArrayRightBy(enc, 2);

		String result = head + new String(enc);
		return result;
	}

	private static String unScramble(String input) {
		String head = input.substring(0, 1);
		input = input.substring(1);

		char[] dec = rotateGivenArrayLeftBy(input.toCharArray(), 2);
		dec = shuffle(dec);
		dec = rotateGivenArrayRightBy(dec, 3);
		dec = shuffle(dec);
		dec = rotateGivenArrayLeftBy(dec, 3);
		dec = shuffle(dec);

		String result = head + new String(dec);
		return result;
	}

	private static char[] shuffle(char[] chars) {
		int k = chars.length - 1;
		for (int i = 0; i < chars.length / 2; i++) {
			char c = chars[i];
			chars[i] = chars[k];
			chars[k] = c;
			k--;
		}
		return chars;
	}

	private static char[] rotateGivenArrayRightBy(char[] input, int k) {
		int n = input.length - 1;
		while (k > 0) {
			char temp = input[n];
			for (int i = 0; i < n; i++) {
				input[n - i] = input[n - i - 1];
			}
			input[0] = temp;
			k--;
		}
		return input;
	}

	private static char[] rotateGivenArrayLeftBy(char[] input, int k) {
		int n = input.length - 1;
		while (k > 0) {
			char temp = input[0];
			for (int i = 0; i < n; i++) {
				input[i] = input[i + 1];
			}
			input[n] = temp;
			k--;
		}
		return input;
	}

	public static void main(String args[]) {

	
	}


}
