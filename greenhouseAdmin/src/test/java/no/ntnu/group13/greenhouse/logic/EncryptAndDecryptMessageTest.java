package no.ntnu.group13.greenhouse.logic;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class EncryptAndDecryptMessageTest {

  @Test
  public void givenString_whenEncrypt_thenSuccess()
      throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
      BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeySpecException {
    // Test adapted from: https://www.baeldung.com/java-aes-encryption-decryption

    String input = "group13greenhouseEncryptionTest";
    SecretKey key = EncryptAndDecryptMessage.getKeyFromPassword("group13", "123");
    IvParameterSpec ivParameterSpec = EncryptAndDecryptMessage.generateIv();
    String algorithm = "AES/CBC/PKCS5Padding";
    String cipherText = EncryptAndDecryptMessage.encrypt(algorithm, input, key, ivParameterSpec);
    String plainText = EncryptAndDecryptMessage.decrypt(algorithm, cipherText, key, ivParameterSpec);

    Assertions.assertEquals(input, plainText);
  }
}
