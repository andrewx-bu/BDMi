package com.example.bdmi.ui.custom_lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.bdmi.UserViewModel
import com.example.bdmi.data.repositories.CustomList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistsScreen(userViewModel: UserViewModel, onListClick: (Pair<String, String>) -> Unit) {
    val userId = userViewModel.userInfo.collectAsState().value?.userId


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watchlists") },
                actions = {
                    AddListButton(userId.toString())
                }
            )
        }
    ) { innerPadding ->
        WatchlistList(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun WatchlistList(modifier: Modifier) {

}

@Composable
fun AddListButton(userId: String, onClick: (CustomList) -> Unit = {}) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
7
    IconButton(
        onClick = { showDialog = true }
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add List")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Update List Info") },
            text = {
                Column {
                    Text("Name:")
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                    )
                    Text("Description:")
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                    )
                    // TODO: Add switch for public/private
                    Text("Public:")
                    Switch(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onClick(
                        CustomList(
                            name = name,
                            description = description,
                            isPublic = isPublic
                        )
                    )
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    name = ""
                    description = ""
                    isPublic = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}