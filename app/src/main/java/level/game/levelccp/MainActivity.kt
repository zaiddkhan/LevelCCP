package level.game.levelccp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import level.game.ccp.components.TogiCountryCodePicker
import level.game.levelccp.ui.theme.LevelCCPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LevelCCPTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    CountryCodePick()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LevelCCPTheme {
        Greeting("Android")
    }
}

@Composable
fun CountryCodePick() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var phoneNumber by rememberSaveable { mutableStateOf("") }
        var fullPhoneNumber by rememberSaveable { mutableStateOf("") }
        var isNumberValid: Boolean by rememberSaveable { mutableStateOf(false) }

        Spacer(modifier = Modifier.height(100.dp))

        TogiCountryCodePicker(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            onValueChange = { (code, phone), isValid ->
                Log.d("CCP", "onValueChange: $code $phone -> $isValid")
                phoneNumber = phone
                fullPhoneNumber = code + phone
                isNumberValid = isValid
            },
            label = { Text("Phone Number") },
            typography = MaterialTheme.typography
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Full Phone Number: $fullPhoneNumber",
            color = if (!isNumberValid) Color.Red else Color.Green,
        )

        Text(
            text = "Only Phone Number: $phoneNumber",
            color = if (!isNumberValid) Color.Red else Color.Green,
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}