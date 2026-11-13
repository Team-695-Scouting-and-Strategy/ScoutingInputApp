package com.example.scoutinginputapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scoutinginputapp.ui.theme.ScoutingInputAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScoutingInputAppTheme {
                ScoutingInputAppApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun ScoutingInputAppApp(viewModel: ScoutingViewModel = viewModel()) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.TEAMS) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                when (currentDestination) {
                    AppDestinations.TEAMS -> TeamListScreen(viewModel)
                    AppDestinations.ADD_TEAM -> AddTeamScreen(viewModel) {
                        currentDestination = AppDestinations.TEAMS
                    }
                }
            }
        }
    }
}

@Composable
fun TeamListScreen(viewModel: ScoutingViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(viewModel.teams) { team ->
            TeamCard(team, onUpdate = { viewModel.updateTeam(it) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TeamCard(team: Team, onUpdate: (Team) -> Unit) {
    var showQr by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Team ${team.number}: ${team.name}", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = team.score.toString(),
                onValueChange = { onUpdate(team.copy(score = it.toIntOrNull() ?: 0)) },
                label = { Text("Score") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = team.notes,
                onValueChange = { onUpdate(team.copy(notes = it)) },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { showQr = !showQr }, modifier = Modifier.padding(top = 8.dp)) {
                Text(if (showQr) "Hide QR" else "Generate QR")
            }
            if (showQr) {
                val bitmap = remember(team) { generateQrCode(team.toQrString()) }
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun AddTeamScreen(viewModel: ScoutingViewModel, onTeamAdded: () -> Unit) {
    var number by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Add New Team", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            label = { Text("Team Number") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Team Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (number.isNotBlank() && name.isNotBlank()) {
                    viewModel.addTeam(number, name)
                    onTeamAdded()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Team")
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    TEAMS("Teams", Icons.Default.List),
    ADD_TEAM("Add Team", Icons.Default.Add),
}
