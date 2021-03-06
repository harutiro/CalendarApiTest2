package app.makino.harutiro.calendarapitest2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.security.GeneralSecurityException
import java.util.*


val GOOGLE_AUTH_RC_SIGN_IN = 1


class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener{



            runBlocking(Dispatchers.IO){
                CalendarQuickstart.main()
            }


//            onClick()
        }

        findViewById<Button>(R.id.button2).setOnClickListener{
            val intent = Intent(this, Test3Activity::class.java)
            startActivity(intent)

        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(R.string.gcp_client_id.toString())
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()



    }

    fun onClick() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, GOOGLE_AUTH_RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_AUTH_RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result!!.isSuccess) {
                try {
                    val account: GoogleSignInAccount = result.signInAccount as GoogleSignInAccount
                    val idToken = account.idToken

                    Log.d("debug2", "${account.displayName}")
                    Log.d("debug2", idToken.toString())

                } catch (e: ApiException) {
                    Log.e("error", e.toString())
                }
            }else {
                Log.d("debug2", "else")

            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }


}

object CalendarQuickstart {
    private const val APPLICATION_NAME = "Google Calendar API Java Quickstart"
    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    private const val TOKENS_DIRECTORY_PATH = "tokens"

    // ???????????????????????????????????????
    private val SCOPES: List<String> = Collections.singletonList(CalendarScopes.CALENDAR_READONLY)

    // ????????????
    private const val CREDENTIALS_FILE_PATH = "/credentials.json"

    // ?????????????????????????????????
    @Throws(IOException::class)
    private fun getCredentials(HTTP_TRANSPORT: HttpTransport): Credential {
        // Load client secrets.
        val `in` = CalendarQuickstart::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
                ?: throw FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH)
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // ????????????????????????????????????????????????

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build()
        // ????????????????????????8888?????????????????????
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        //BAG: java.lang.NoClassDefFoundError: Failed resolution of: Ljava/awt/Desktop;
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user").setAccessToken("")
    }

    // ???????????????
    @Throws(IOException::class, GeneralSecurityException::class)
    @JvmStatic
    fun main() {
        val HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
        // ???????????????API??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        val service: Calendar = Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build()

        // ??????????????????Google Calendar API???????????????
        val now = DateTime(System.currentTimeMillis())
        val events: Events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()
        val items: List<Event> = events.getItems()
        if (items.isEmpty()) {
            println("No upcoming events found.")
        } else {
            println("Upcoming events")
            for (event in items) {
                var start: DateTime = event.getStart().getDateTime()
                if (start == null) {
                    start = event.getStart().getDate()
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start)
            }
        }
    }
}
