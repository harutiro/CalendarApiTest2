package app.makino.harutiro.calendarapitest2


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class Test3Activity : AppCompatActivity() {

    val TAG = "debag2"
    val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test3)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()


    }




}

//438184272791-ppj1bcan9obtv7lgh7078ue00ntqtev3.apps.googleusercontent.com