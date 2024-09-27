package com.example.prductivityapp
import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
        private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.SCHEDULE_EXACT_ALARM,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.VIBRATE
            )
        } else {
            listOf(
                Manifest.permission.SCHEDULE_EXACT_ALARM,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.VIBRATE
            )
        }
    }

    private var currentPermissionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        requestNextPermission()
    }

    private fun requestNextPermission() {
        if (currentPermissionIndex < REQUIRED_PERMISSIONS.size) {
            val permission = REQUIRED_PERMISSIONS[currentPermissionIndex]
            when {
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                    currentPermissionIndex++
                    requestNextPermission()
                }
                permission == Manifest.permission.SCHEDULE_EXACT_ALARM -> {
                    requestScheduleExactAlarmPermission()
                }
                else -> {
                    ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
                }
            }
        } else {
            initializeApp()
        }
    }

    private fun requestScheduleExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                AlertDialog.Builder(this)
                    .setTitle("Exact Alarm Permission Required")
                    .setMessage("This app needs permission to set exact alarms for accurate reminders.")
                    .setPositiveButton("Grant Permission") { _, _ ->
                        startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                    }
                    .setNegativeButton("Skip") { _, _ ->
                        currentPermissionIndex++
                        requestNextPermission()
                    }
                    .show()
            } else {
                currentPermissionIndex++
                requestNextPermission()
            }
        } else {
            currentPermissionIndex++
            requestNextPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                currentPermissionIndex++
                requestNextPermission()
            } else {
                // Permission denied
                showPermissionDeniedDialog(REQUIRED_PERMISSIONS[currentPermissionIndex])
            }
        }
    }

    private fun showPermissionDeniedDialog(permission: String) {
        val permissionName = when (permission) {
            Manifest.permission.POST_NOTIFICATIONS -> "Notifications"
            Manifest.permission.RECEIVE_BOOT_COMPLETED -> "Auto-start"
            Manifest.permission.VIBRATE -> "Vibration"
            else -> "Unknown"
        }

        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("The $permissionName permission is important for app functionality. Would you like to grant it?")
            .setPositiveButton("Grant") { _, _ ->
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
            }
            .setNegativeButton("Skip") { _, _ ->
                currentPermissionIndex++
                requestNextPermission()
            }
            .setCancelable(false)
            .show()
    }

    private fun initializeApp() {
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_tasks -> {
                    loadFragment(TaskFragment.newInstance(sessionManager))
                    true
                }
                R.id.nav_timer -> {
                    loadFragment(TimerFragment())
                    true
                }
                R.id.nav_reminder -> {
                    loadFragment(ReminderFragment.newInstance(sessionManager))
                    true
                }
                else -> false
            }
        }

        loadFragment(TaskFragment.newInstance(sessionManager))
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            currentPermissionIndex++
            requestNextPermission()
        }
    }
}