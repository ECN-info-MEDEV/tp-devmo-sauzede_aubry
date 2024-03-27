package com.devmo.locare_devmo
import NotificationViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


//On définit la classe de données notification
data class Notification(val header: String, val content: String, var note: Int)


//Fonction HomeScreen. Composable définissant l'écran d'accueil
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    notifications: List<Notification>,
    onItemClick: (Notification) -> Unit,
    onDeleteClick: (Notification) -> Unit // Callback pour la suppression
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = { TopAppBar(title = {Text(
                text = "Locare",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) }) },
            content = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bienvenue sur l'application Locare. Cliquer sur une notification pour évaluer le professionnel, ou swipe pour supprimer",
                        modifier = Modifier.padding(bottom = 16.dp) // Ajout d'un espace en bas
                    )
                    NotificationList(
                        notifications = notifications,
                        onItemClick = onItemClick,
                        onDeleteClick = onDeleteClick // Passer le callback
                    )
                }
            }
        )
    }
}






//Preview du HomeScreen
@Preview
@Composable
fun HomeScreenPreview() {
    val notifications = listOf(
        Notification("Notification 1", "Contenu de la notification 1", -1),
        Notification("Notification 2", "Contenu de la notification 2", -1)
    )
    HomeScreen(notifications = notifications, onItemClick = {

    }) { /* Do nothing */ }
}

//Preview du deuxième écran
@Preview
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        notification = Notification(
            header = "Notification Header",
            content = "Contenu de la notification",
            note = -1
        ),
        onSendRating = {
        },
        viewModel=NotificationViewModel()
    )
}

//Définit le composable de la liste de notifications
@Composable
fun NotificationList(
    notifications: List<Notification>,
    onItemClick: (Notification) -> Unit,
    onDeleteClick: (Notification) -> Unit
) {
    LazyColumn {
        items(notifications) { notification ->
            NotificationItem(notification = notification, onDeleteClick=onDeleteClick) {
                onItemClick(notification)
            }
        }
    }
}


//Définit le composable d'une unique notification
@Composable
fun NotificationItem(notification: Notification,onDeleteClick: (Notification) -> Unit, onItemClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onItemClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = notification.header, style = MaterialTheme.typography.headlineSmall)
            if (notification.note != -1) {
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < notification.note) Icons.Default.Star else Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (index < notification.note) Color.Yellow else Color.Gray,
                            modifier = Modifier
                                .padding(4.dp)
                        )
                    }
                }
            }
            Text(text = notification.content, style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                DeleteIcon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    onClick = { onDeleteClick(notification) }
                )
            }
        }
    }
}

@Composable
fun DeleteIcon(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick),
        tint = Color.Red
    )
}


//Définit l'écran de notation
@Composable
fun RatingFeedback(
    onSendClicked: () -> Unit,
    viewModel: NotificationViewModel,
    header: String
) {
    var rating by remember { mutableStateOf(0) }
    var feedbackText by remember { mutableStateOf("") }
    var note = rating

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Évaluez cette notification",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < rating) Icons.Default.Star else Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (index < rating) Color.Yellow else Color.Gray,
                    modifier = Modifier
                        .clickable { rating = index + 1; note = rating }
                        .padding(4.dp)
                )
            }
            Log.v("test", note.toString())

        }
        TextField(
            value = feedbackText,
            onValueChange = { feedbackText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            label = { Text("Laissez un avis (optionnel)") },
            maxLines = 3
        )
        Button(
            onClick = {
                onSendClicked()
                viewModel.updateNotification(header, note)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Envoyer")
        }
    }
}



//définit le second écran
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(notification: Notification, onSendRating: () -> Unit, viewModel: NotificationViewModel) {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar(
            title = { Text(text = notification.header) },
            navigationIcon = {
                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )},
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = notification.content, style = MaterialTheme.typography.bodyMedium)
                if (notification.note != -1) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < notification.note) Icons.Default.Star else Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (index < notification.note) Color.Yellow else Color.Gray,
                            )
                        }
                    }
                }
                Text(text = notification.content, style = MaterialTheme.typography.bodyMedium)
                RatingFeedback(
                    onSendClicked = onSendRating,
                    header = notification.header,
                    viewModel = viewModel
                )
            }
        }
    )
}




class MainActivity : AppCompatActivity() {
    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadNotifications(this)

        // Observer les notifications depuis le ViewModel
        viewModel.notifications.observe(this) { notifications ->
            // Mettre à jour l'interface utilisateur avec les nouvelles notifications
            setContent {
                LocareApp(notifications)
            }
        }
    }



    @Composable
    fun LocareApp(notifications: List<Notification>) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    notifications = notifications,
                    onItemClick = { notification ->
                        navController.navigate("detail/${notification.header}/${notification.content}")
                    },
                    onDeleteClick = { notification ->
                        viewModel.deleteNotification(notification.header)
                    })
            }
            composable(
                route = "detail/{header}/{content}",
                arguments = listOf(
                    navArgument("header") { type = NavType.StringType },
                    navArgument("content") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val header = backStackEntry.arguments?.getString("header") ?: ""
                val content = backStackEntry.arguments?.getString("content") ?: ""
                DetailScreen(notification = Notification(header = header, content = content, note=-1), onSendRating = {
                    navController.navigate("home")
                },
                    viewModel = viewModel)
            }
        }
    }

}
