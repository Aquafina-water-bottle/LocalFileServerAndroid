package com.rayrtheii.localfileserver

import android.Manifest.permission
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

class MainActivity : AppCompatActivity() {
    class NotificationsPermissionDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the Builder class for convenient dialog construction
            val activity = activity as MainActivity?
            val builder = AlertDialog.Builder(
                activity!!
            )
            builder.setMessage(R.string.dialog_notif_perm_info)
                .setPositiveButton("Ok") { _, _ -> // ASSUMPTION: NotificationsPermissionDialogFragment is only created
                    // on API level >= 33
                    activity.requestPermissionLauncher!!.launch(permission.POST_NOTIFICATIONS)
                }
                .setNegativeButton("Cancel") { _, _ -> activity.startServiceWithoutNotifications() }
            // Create the AlertDialog object and return it
            return builder.create()
        }
    }

    private var notificationManager: NotificationManager? = null
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar support
        val toolbar = findViewById<Toolbar>(R.id.materialToolbar)
        setSupportActionBar(toolbar)
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "Local File Server",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val tempManager = getSystemService(NotificationManager::class.java)
        tempManager.createNotificationChannel(notificationChannel)
        notificationManager = tempManager

        // this cannot be put inside attemptGrantNotifyPermissions, because it is called by
        // a onClickListener and crashes the app: https://stackoverflow.com/a/67582633
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean? ->
                if (!isGranted!!) {
                    Toast.makeText(
                        this,
                        "Attempting to start server without notification...",
                        Toast.LENGTH_LONG
                    ).show()
                }
                startService()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            // open settings
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun attemptGrantNotificationPermissions() {
        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher, as an instance variable.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // there's nothing else we can do on older SDK versions
            startServiceWithoutNotifications()
            return
        }

        // NOTE: I can't find anything about this in the actual documentation, but an
        // explanation for shouldShowRequestPermissionRationale is shown below
        // (taken from: https://stackoverflow.com/a/39739972):
        //
        // This method returns true if the app has requested this permission previously and the
        // user denied the request. Note: If the user turned down the permission request in the
        // past and chose the Don't ask again option in the permission request system dialog,
        // this method returns false.
        if (shouldShowRequestPermissionRationale(permission.POST_NOTIFICATIONS)) {
            // Explain that notifications are "needed" to display the server
            NotificationsPermissionDialogFragment().show(
                this.supportFragmentManager,
                "post_notifications_dialog"
            )
        } else {
            // Directly ask for the permission.
            requestPermissionLauncher!!.launch(permission.POST_NOTIFICATIONS)
        }
    }

    fun startServiceWithoutNotifications() {
        Toast.makeText(
            this,
            "Attempting to start server without notification...",
            Toast.LENGTH_LONG
        ).show()
        startService()
    }

    fun startService() {
        val serviceIntent = Intent(this, Service::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun startServiceBtn(view: View?) {
        val notificationsEnabled = notificationManager!!.areNotificationsEnabled()
        if (notificationsEnabled) {
            startService()
        } else {
            attemptGrantNotificationPermissions()
        }
    }

    fun stopServiceBtn(view: View?) {
        val serviceIntent = Intent(this, Service::class.java)
        stopService(serviceIntent)
    }

    companion object {
        const val CHANNEL_ID = "RayrTheIILocalFileServer"
    }
}