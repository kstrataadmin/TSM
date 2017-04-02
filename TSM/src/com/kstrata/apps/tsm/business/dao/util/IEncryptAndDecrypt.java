package com.kstrata.apps.tsm.business.dao.util;

public interface IEncryptAndDecrypt {

	byte[] encrypt(String input) throws Exception;

	String decrypt(byte[] encryptionBytes) throws Exception;

}
