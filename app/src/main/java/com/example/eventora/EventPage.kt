package com.example.eventora


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun EventPage(event: Event, snackbarHostState: SnackbarHostState, loggedIn: Boolean, registered: Boolean) {
    val dataStore = UserPreferences(LocalContext.current)
    val userId by dataStore.getUserId.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()

    LazyColumn {
        item {
            EventHeader(event)
            EventDetails(event)
            EventActions(event, snackbarHostState, loggedIn, registered, userId, coroutineScope)
        }
    }
}

@Composable
private fun EventHeader(event: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        AsyncImage(
            model = event.image,
            contentDescription = "Event Picture",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
    }
}

@Composable
private fun EventDetails(event: Event) {
    Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        Column {
            Text(
                text = event.title,
                style = TextStyle(fontSize = 27.sp),
                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
            )
            Text(
                text = event.organiser,
                style = TextStyle(fontSize = 18.sp, color = Color.Gray),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = event.description,
                style = TextStyle(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Divider()
            Text(
                text = "Date: ${event.event_date}",
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                style = TextStyle(fontSize = 20.sp)
            )
            Divider()
            Text(
                text = "Location: ${event.location}",
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                style = TextStyle(fontSize = 20.sp)
            )
            Divider()
            Text(
                text = "Quota: ${event.quota}",
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                style = TextStyle(fontSize = 20.sp)
            )
        }
    }
}

@Composable
private fun EventActions(
    event: Event,
    snackbarHostState: SnackbarHostState,
    loggedIn: Boolean,
    registered: Boolean,
    userId: String?,
    coroutineScope: CoroutineScope
) {
    if (loggedIn) {
        if (!registered) {
            JoinEventButton(event, snackbarHostState, userId, coroutineScope)
        } else {
            UnregisterButton(event, snackbarHostState, userId, coroutineScope)
        }
    }
}

@Composable
private fun JoinEventButton(
    event: Event,
    snackbarHostState: SnackbarHostState,
    userId: String?,
    coroutineScope: CoroutineScope
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val res = userId?.let { KtorClient.joinEvent(event._id, it) }
                        if (res != null) {
                            snackbarHostState.showSnackbar(res)
                        } else {
                            snackbarHostState.showSnackbar("Error. Quota is full.")
                        }
                    }
                },
                shape = RoundedCornerShape(5.dp)
            ) {
                Text("Join Event")
            }
        }
    }
}

@Composable
private fun UnregisterButton(
    event: Event,
    snackbarHostState: SnackbarHostState,
    userId: String?,
    coroutineScope: CoroutineScope
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val res = userId?.let { KtorClient.unRegister(event._id, it) }
                        if (res == "Unregistered" || res == "Error!") {
                            snackbarHostState.showSnackbar(res)
                        }
                    }
                },
                shape = RoundedCornerShape(5.dp)
            ) {
                Text("Unregister")
            }
        }
    }
}