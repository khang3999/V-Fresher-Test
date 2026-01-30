package huukhang3999.vfreshtest.doubledecodekey

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class DecodeViewModel: ViewModel() {
    var step by mutableStateOf(DecodeStep.INIT)
        private set

    var instruction by mutableStateOf("")
        private set

    var cipherText by mutableStateOf("")
        private set

    var key by mutableStateOf("")
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun reset() {
        step = DecodeStep.INIT
        instruction = ""
        cipherText = ""
        key = ""
        error = null
    }
    fun runNextStep(input: String) {
        Log.d("step check", "runNextStep: " + step)
        when (step) {
            DecodeStep.INIT -> {
                base64URLDecode(input)
                    .onSuccess {
                        instruction = it
                        step = DecodeStep.INSTRUCTION_DONE
                    }
                    .onFailure {
                        error = it.message
                        step = DecodeStep.ERROR
                    }
            }

            DecodeStep.INSTRUCTION_DONE -> {
                base64URLDecode("eXhvZmRx")
                    .onSuccess {
                        cipherText = it
                        step = DecodeStep.CIPHER_DONE
                    }
                    .onFailure {
                        error = it.message
                        step = DecodeStep.ERROR
                    }

            }

            DecodeStep.CIPHER_DONE -> {
                caesarDecode(cipherText, 3)
                    .onSuccess {
                        key = it
                        step = DecodeStep.DONE
                    }
                    .onFailure {
                        error = it.message
                        step = DecodeStep.ERROR
                    }
            }
            else -> reset()
        }
    }

    fun runAll(input: String) {
        decodeAll(input)
            .onSuccess {
                instruction = it.instruction
                cipherText = it.cipherText
                key = it.key
                step = DecodeStep.DONE
            }
            .onFailure {
                error = it.message
                step = DecodeStep.ERROR
            }
    }

    // Fun: Decode a Base64URL string
    fun base64URLDecode(input: String): Result<String> {
        return try {
            // Preprocess
            val cleanInput = input.replace("\\s+".toRegex(), "")
            // Decode
            val decoded = android.util.Base64.decode(
                cleanInput,
                android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP
            )
            // Return
            Result.success(String(decoded, Charsets.UTF_8))
        } catch (e: IllegalArgumentException) {
            Result.failure(
                IllegalArgumentException("Invalid Base64URL input")
            )
        }
    }
    // Fun: Check valid cipherText
    fun isValidCipherText(text: String): Boolean {
        return text.all { char ->
            char.isLetter() || char.isWhitespace()
        }
    }

    // Fun: Caesar Decode with shift
    fun caesarDecode(input: String, shift: Int): Result<String> {
        if (!isValidCipherText(input)) {
            return Result.failure(
                IllegalArgumentException("Invalid:  cipherText contains non-alphabet characters")
            )
        }
        // If shift > 26
        val s = shift % 26
        val decoded = input.map { char ->
            when {
                char in 'a'..'z' ->
                    'a' + (char - 'a' - s + 26) % 26

                char in 'A'..'Z' ->
                    'A' + (char - 'A' - s + 26) % 26

                else -> char
            }
        }.joinToString("")
        return Result.success(decoded)
    }

    // Fun: Decode all step
    fun decodeAll(input: String): Result<DecodeState> {
        // Step 1: Decode instruction
        val instruction = base64URLDecode(input)
            .getOrElse { return Result.failure(it) }

        // Step 2: Instruction for next step is "Base64URL-decode "eXhvZmRx" to get cipherText; then Caesar-decode (shift=3) to get KEY"
        // Decode "eXhvZmRx" from Instruction to get cipherText
        val cipherBase64 = "eXhvZmRx"
        val cipherText = base64URLDecode(cipherBase64)
            .getOrElse { return Result.failure(it) }

        // Step 3: Caesar decode
        val key = caesarDecode(cipherText, 3)
            .getOrElse { return Result.failure(it) }

        return Result.success(
            DecodeState(
                instruction = instruction,
                cipherText = cipherText,
                key = key
            )
        )
    }
}
enum class DecodeStep { INIT, INSTRUCTION_DONE, CIPHER_DONE, DONE, ERROR }