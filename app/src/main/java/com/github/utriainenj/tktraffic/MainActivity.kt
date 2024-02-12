package com.github.utriainenj.tktraffic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.utriainenj.tktraffic.ui.theme.TKtrafficTheme
import okhttp3.OkHttpClient
import okhttp3.Request
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val funFont = FontFamily(
    Font(R.font.funfont, FontWeight.Normal)
    // Add other fonts for different weights or styles if available
)



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TKtrafficTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background) {
                    // Center the content
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    ) {
                        // Arrange counters vertically
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            counterL("Lielahti", Modifier.padding(8.dp))
                            counterN("Nekala", Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}

suspend fun fetchTraffic(location: String): String = withContext(Dispatchers.IO) {
    val client = OkHttpClient()

    val request: Request? = when (location) {
        "L" -> {
            // Lielahti
            Request.Builder()
                .url("https://tk.boulderkeskus.com/t/paritus/show_climbers_in/?location=2")
                .build()
        }
        "N" -> {
            // Nekala
            Request.Builder()
                .url("https://tk.boulderkeskus.com/t/paritus/show_climbers_in/?location=1")
                .build()
        }
        else -> null
    }

    if (request != null) {
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string()?.filter { it.isDigit() } ?: throw Exception("Invalid response")
                } else {
                    throw Exception("Request failed with code: ${response.code}")
                }
            }
        } catch (e: Exception) {
            Log.e("fetchTraffic", "Failed to fetch traffic data", e)
            "Shits broke yo"
        }
    } else {
        "Invalid location" // Return a message or handle the case where the location is not supported
    }
}

@Composable
fun counterL(name: String, modifier: Modifier = Modifier) {
    // Initialize the state for holding traffic information
    var trafficText by remember { mutableStateOf("Loading...") }

    // Use LaunchedEffect to fetch data; this will run on composition and not block the UI thread
    LaunchedEffect(key1 = Unit) {
        // Update the trafficText with the result of fetchTraffic
        try {
            trafficText = fetchTraffic("L")
        } catch (e: Exception) {
            trafficText = "Error fetching data"
            Log.e("HALOO", "Error fetching traffic data", e)
        }
    }
    Box(
        modifier = Modifier
            .size(width = 400.dp, height = 200.dp) // Specify size of the Box
            .background(Color.LightGray), // Change the Box color
        contentAlignment = Alignment.Center // Center the content inside the Box
    ) {
    Text(
        text = "Lielahti: " + trafficText,
        color = Color.Black, // Change text color
        style = TextStyle(
            fontFamily = funFont,
            fontSize = 48.sp, // Make text bigger
            fontWeight = FontWeight.Bold // Example of making text bold
        ),
        modifier = modifier
    )
    }
}

@Composable
fun counterN(name: String, modifier: Modifier = Modifier) {
    // Initialize the state for holding traffic information
    var trafficText by remember { mutableStateOf("Loading...") }

    // Use LaunchedEffect to fetch data; this will run on composition and not block the UI thread
    LaunchedEffect(key1 = Unit) {
        // Update the trafficText with the result of fetchTraffic
        try {
            trafficText = fetchTraffic("N")
        } catch (e: Exception) {
            trafficText = "Error fetching data"
            Log.e("HALOO", "Error fetching traffic data", e)
        }
    }
    Box(
        modifier = Modifier
            .size(width = 400.dp, height = 200.dp) // Specify size of the Box
            .background(Color.LightGray), // Change the Box color
        contentAlignment = Alignment.Center // Center the content inside the Box
    ) {
        Text(
            text = "Nekala: " + trafficText,
            color = Color.Black, // Change text color
            style = TextStyle(
                fontFamily = funFont,
                fontSize = 48.sp, // Make text bigger
                fontWeight = FontWeight.Bold // Example of making text bold
            ),
            modifier = modifier
        )
    }
}
