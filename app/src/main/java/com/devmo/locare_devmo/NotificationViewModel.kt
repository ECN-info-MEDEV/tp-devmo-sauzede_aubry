import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devmo.locare_devmo.Notification
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class NotificationViewModel : ViewModel() {
    private val _notifications = MutableLiveData<List<Notification>>(emptyList())
    val notifications: LiveData<List<Notification>> = _notifications

    fun loadNotifications(context: Context) {
            val inputStream: InputStream = context.assets.open("datasource.txt")
            val reader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
            val notifications = mutableListOf<Notification>()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val parts = line!!.split(",")
                if (parts.size >= 2) {
                    notifications.add(Notification("Evaluez l'intervention de " + parts[0], parts[1], parts[2].toInt()))
                }
            }
            reader.close()

            _notifications.postValue(notifications)
    }

    fun deleteNotification(header: String) {
        val currentNotifications = _notifications.value.orEmpty().toMutableList()
        currentNotifications.removeAll { it.header == header }
        _notifications.value = currentNotifications
    }

    // Fonction pour mettre à jour la note d'une notification
    fun updateNotification(header: String, newNote: Int) {
        Log.v("test", "dans le viewModel, note vaut "+newNote.toString() )
        val note = newNote
        val updatedNotifications = _notifications.value?.toMutableList() ?: mutableListOf()
        val index = updatedNotifications.indexOfFirst { it.header == header }
        if (index != -1) {
            val newHeader = "Vous avez évalué l'intervention de " + header.substring(26)
            val updatedNotification = updatedNotifications[index].copy(header=newHeader)
            Log.v("test", "valeur de la note "+note.toString())
            updatedNotification.note = note
            updatedNotifications[index] = updatedNotification
            _notifications.postValue(updatedNotifications)
        }
    }
}
