package com.example.bletest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bletest.model.Person
import com.example.bletest.ui.theme.BLETestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BLETestTheme {
                Scaffold { innerPadding ->
                    TestList(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
private fun TestList(modifier: Modifier) {
    var selectedPerson: Person? by rememberSaveable { mutableStateOf(null) }
    val people = listOf(
        Person(name = "Janice", age = 30),
        Person(name = "Steve", age = 71),
        Person(name = "Barbara", 54),
        Person(name = "Ryan", age = 3),
        Person(name = "Mike", 46),
        Person(name = "Jimmy", age = 91),
        Person(name = "Fred", 44),
        )

    Column(modifier) {
        people.forEach { person ->
            PersonSelectionRow(
                person,
                isSelected = (selectedPerson == person),
                onPersonSelected = { person ->
                    selectedPerson = person
                }
            )
        }
    }
}

@Composable
fun PersonSelectionRow(
    person: Person,
    isSelected: Boolean,
    onPersonSelected: (Person) -> Unit
    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPersonSelected(person) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(person.name)
            Text("${person.age}")
        }

        RadioButton(
            selected = isSelected,
            onClick = { onPersonSelected(person) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TestListPreview() {
    BLETestTheme {
        Scaffold { innerPadding ->
            TestList(modifier = Modifier.padding(innerPadding))
        }
    }
}