package app.makino.harutiro.calendarapitest2

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.AccountPicker


class test2Activity : AppCompatActivity() {

    val REQUEST_PICK_ACCOUNT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)

        findViewById<Button>(R.id.button3).setOnClickListener{
            val accountChooserIntent = AccountPicker.newChooseAccountIntent(null, null, arrayOf(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE), true, getString(R.string.please_select_account), null,
                    null, null)
            startActivityForResult(accountChooserIntent, REQUEST_PICK_ACCOUNT)
        }


    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PICK_ACCOUNT ->  {
                if (resultCode == RESULT_OK) {
                    val mChosenAccountName = data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)

                } else {

                }

            }
        }
    }
}