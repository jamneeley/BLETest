package com.example.bletest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bletest.ui.theme.BLETestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BLETestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Test

                }
            }
        }
    }
}


@Composable
fun TestList() {
    Column {
        TestRow()
        TestRow()
    }
}

@Composable
fun TestRow() {
    Row {
        Text("Test Text")
        Text("Test Text")
        RadioButton(false, onClick = {

        })
    }
}