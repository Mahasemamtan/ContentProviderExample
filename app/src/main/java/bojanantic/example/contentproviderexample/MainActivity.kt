package bojanantic.example.contentproviderexample

import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


private const val TAG = "MainActivity"
private const val REQUEST_CODE_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {

//    private var readGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val hasReadContactsPermission = ContextCompat.checkSelfPermission(this, READ_CONTACTS)
        Log.d(TAG, ".onCreate: checkSelfPermission returned $hasReadContactsPermission")

//        if (hasReadContactsPermission == PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, ".onCreate: permission granted!")
////            readGranted = true // TODO: Don't do this!!!
//        } else {
//            Log.d(TAG, ".onCreate: requesting permission")
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(READ_CONTACTS),
//                REQUEST_CODE_READ_CONTACTS
//            )
//        }

        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, ".onCreate: requesting permission")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_CONTACTS),
                REQUEST_CODE_READ_CONTACTS
            )
        }

        fab.setOnClickListener { view ->
            Log.d(TAG, "fab .onClick: starts")
//            if (readGranted) {
            if(ContextCompat.checkSelfPermission(this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

                /* This is basically the SQL query where contentResolver returns the Cursor, and for the
            * parameter is take URI, which points to the correct data source, and app does not need to
            * know anything about it.
            * projection is the array of the strings containing the name of the columns we want to
            * display, but we are asking only for DISPLAY_NAME_PRIMARY
            * selection is WHERE keyword in SQL
            * selectionArgs array of values to replace the placeholders in the selection string
            * Last entry is the Sort order, and we are sorting by the Primary Key  */

                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    projection,
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                )

                val contacts = ArrayList<String>()
                cursor?.use {
                    while (it.moveToNext()) {
                        contacts.add(it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                    }
                }

                val adatper = ArrayAdapter<String>(this, R.layout.contact_detail, R.id.name, contacts)
                contact_names.adapter = adatper
            } else {
                Snackbar.make(view, "fab button is disabled", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Grant Access", {
                        Log.d(TAG,"Snackbar .onClick: starts")
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this, READ_CONTACTS)) {
                            Log.d(TAG, "Snackbar .onClick calling equestPermissions")
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(READ_CONTACTS),
                                REQUEST_CODE_READ_CONTACTS
                            )
                        } else {
                            // User has permanently denaid the permission, take them to the Settings
                            Log.d(TAG, "Snackbar .onClick: launcing Settings")
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", this.packageName, null)
                            Log.d(TAG, "Snackbar .onClick: uri is $uri")
                            intent.data = uri
                            this.startActivity(intent)
                        }
                        Toast.makeText(it.context, "Snackbar action clicked", Toast.LENGTH_SHORT).show()
                    }).show()
            }
            Log.d(TAG, "fab .onClick: ends")
        }
        Log.d(TAG, ".onCreate: ends")
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        Log.d(TAG, ".onRequestPermissionsResult: starts")
//        when (requestCode) {
//            REQUEST_CODE_READ_CONTACTS -> {
////                readGranted = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                  if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                        Log.d(TAG, ".onRequestPermissionsResult: permission granted")
//                        // Permission granted, do the contacts related task.
////                        true
//                    } else {
//                        Log.d(TAG, ".onRequestPermissionsResult: permission rejected")
//                        // Permission denied, cancel the action that requires this permission.
////                        false
//                    }
////                fab.isEnabled = readGranted
//            }
//        }
//        Log.d(TAG, ".onRequestPermissionsResult: ends")
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
