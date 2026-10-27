package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.crypto.CryptoManager
import com.example.data.ChatRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CipherChat", appName)
  }

  @Test
  fun `verify aes gcm encryption and decryption round trip`() {
    val secretMessage = "Top secret zero-knowledge transmission 12345!"
    val encryptedPayload = CryptoManager.encrypt(secretMessage)

    assertNotEquals(secretMessage, encryptedPayload.ciphertext)
    assertEquals("AES-256-GCM", encryptedPayload.algorithm)
    assertEquals(128, encryptedPayload.tagLengthBits)
    assertTrue(encryptedPayload.checksum.isNotBlank())

    val decrypted = CryptoManager.decrypt(encryptedPayload.ciphertext, encryptedPayload.iv)
    assertEquals(secretMessage, decrypted)
  }

  @Test
  fun `verify vault pin hashing and verification`() {
    val defaultPin = "1234"
    val hash = CryptoManager.hashPin(defaultPin)

    assertTrue(CryptoManager.verifyPin("1234", hash))
    assertFalse(CryptoManager.verifyPin("9999", hash))
  }

  @Test
  fun `verify signal style safety numbers generation`() {
    val safetyNumber1 = CryptoManager.generateSafetyNumber("elena_k")
    val safetyNumber2 = CryptoManager.generateSafetyNumber("elena_k")
    val safetyNumber3 = CryptoManager.generateSafetyNumber("other_user")

    assertEquals(safetyNumber1, safetyNumber2)
    assertNotEquals(safetyNumber1, safetyNumber3)
    assertEquals(12, safetyNumber1.split(" ").size)
  }

  @Test
  fun `verify hidden chat isolation in repository`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = ChatRepository(context)

    val allChats = repository.conversations.value
    val hiddenChats = allChats.filter { it.isHidden }
    val publicChats = allChats.filterNot { it.isHidden }

    assertTrue(hiddenChats.isNotEmpty())
    assertTrue(publicChats.isNotEmpty())
    assertFalse(repository.vaultState.value.isUnlocked)

    // Verify vault unlock with default PIN 1234
    val unlockSuccess = repository.unlockVault("1234")
    assertTrue(unlockSuccess)
    assertTrue(repository.vaultState.value.isUnlocked)

    // Verify lock
    repository.lockVault()
    assertFalse(repository.vaultState.value.isUnlocked)
  }
}
