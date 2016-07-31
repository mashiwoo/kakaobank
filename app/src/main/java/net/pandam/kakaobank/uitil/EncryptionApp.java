package net.pandam.kakaobank.uitil;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;


import android.util.Base64;

import net.pandam.kakaobank.global.Constants;


public class EncryptionApp
{
	public static String getValue(String value)
 	{
 		EncryptionApp encryption = new EncryptionApp();
 		return encryption.decrypt(value, Constants.STRING_KEY);
 	}
	
	public static String getEnValue(String value)
 	{
 		EncryptionApp encryption = new EncryptionApp();
 		return encryption.encrypt(value, Constants.STRING_KEY);
 	}

	public String decrypt(String encryptedText, String keyString)
	{
		try
		{
			byte[] keyBytes = keyString.getBytes();
			DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
			SecretKey key = factory.generateSecret(keySpec);

			Cipher cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.DECRYPT_MODE, key);

			byte[] decodedText = Base64.decode(encryptedText.getBytes(), Base64.DEFAULT);
			byte[] decrypted = cipher.doFinal(decodedText);
			return new String(decrypted, "ASCII");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
	

	  public String encrypt(String encryptedText, String keyString)
	  {
	    try
	    {
	      byte[] arrayOfByte1 = keyString.getBytes("ASCII");
	      DESedeKeySpec localDESedeKeySpec = new DESedeKeySpec(arrayOfByte1);
	      SecretKeyFactory localSecretKeyFactory = SecretKeyFactory.getInstance("DESede");
	      SecretKey localSecretKey = localSecretKeyFactory.generateSecret(localDESedeKeySpec);
	      byte[] arrayOfByte2 = encryptedText.getBytes("ASCII");
	      Cipher localCipher = Cipher.getInstance("DESede");
	      localCipher.init(1, localSecretKey);
	      byte[] arrayOfByte3 = localCipher.doFinal(arrayOfByte2);
	      return Base64.encodeToString(arrayOfByte3, Base64.DEFAULT);
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	    return null;
	  }

}
