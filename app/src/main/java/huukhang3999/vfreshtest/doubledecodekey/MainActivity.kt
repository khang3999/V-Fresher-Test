package huukhang3999.vfreshtest.doubledecodekey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import huukhang3999.vfreshtest.doubledecodekey.ui.theme.DoubleDecodeKeyTheme

class MainActivity : ComponentActivity() {
    val input: String = """
        QmFzZTY0VVJMLWRlY29kZSAiZVhodlptUngiIHRvIGdldCBjaXBoZXJU
        ZXh0OyB0aGVuIENhZXNhci1kZWNvZGUgKHNoaWZ0PTMpIHRvIGdldCBL
        RVk
    """.trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val result = decodeAll(input)

        setContent {
            DoubleDecodeKeyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp),
                    ) {
                        Text(
                            text = "V-Fresher 2026",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 36.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                        Column (modifier = Modifier.background(Color.LightGray).padding(10.dp)) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                                        append("Input: ")
                                    }
                                    append(input)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    ,
                            )
                        }

                        Text(
                            text = "Result:",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding( 10.dp),
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                        Column(
                            modifier = Modifier
                        ) {
                            when {
                                // Failed
                                result.isFailure -> {
                                    ErrorText(
                                        message = result.exceptionOrNull()?.message
                                            ?: "Unknown error"
                                    )
                                }
                                // Success
                                result.isSuccess -> {
                                    val data = result.getOrThrow()
                                    DecodeIntermediate(data.instruction, modifier = Modifier)
                                    DecodeKey(data.key, modifier = Modifier)
                                }
                            }
                        }

                    }

                }
            }
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

@Composable
fun ErrorText(message: String) {
    Text(
        text = "Error: $message",
        color = Color.Red,
        fontSize = 20.sp,
        modifier = Modifier.padding(top = 12.dp)
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun DecodeIntermediate(text: String, modifier: Modifier = Modifier) {
    Text(
        text = "Intermediate Result (after decode): $text",
        modifier = modifier.padding(10.dp),
        fontSize = 18.sp
    )
}

@Composable
fun DecodeKey(text: String, modifier: Modifier = Modifier) {
    Text(
        text = "Decoded KEY (final): $text",
        modifier = modifier.padding(10.dp),
        fontSize = 18.sp
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DoubleDecodeKeyTheme {
        Greeting("Android")
    }
}

data class DecodeState(
    val instruction: String,
    val cipherText: String,
    val key: String
)