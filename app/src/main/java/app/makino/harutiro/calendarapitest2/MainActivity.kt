package app.makino.harutiro.calendarapitest2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import java.io.*
import java.security.GeneralSecurityException
import java.util.*
import java.util.logging.Logger

val GOOGLE_AUTH_RC_SIGN_IN = 1


class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener{

//            CalendarQuickstart.main()

            onClick()
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
                } catch (e: ApiException) {
//                    Logger.error("error",e.toString())
                }
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

    // アクセスする権限のスコープ
    private val SCOPES: List<String> = Collections.singletonList(CalendarScopes.CALENDAR_READONLY)

    // 認証情報
    private const val CREDENTIALS_FILE_PATH = "/credentials.json"

    // 認証情報を取得する処理
    @Throws(IOException::class)
    private fun getCredentials(HTTP_TRANSPORT: HttpTransport): Credential {
        // Load client secrets.
        val `in` = CalendarQuickstart::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
                ?: throw FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH)
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // 認証に利用するオブジェクトを作成
        val flow = GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build()
        // ローカルサーバを8888ポートで立てる
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    // メイン関数
    @Throws(IOException::class, GeneralSecurityException::class)
    @JvmStatic
    fun main() {
        val HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
        // カレンダーAPI呼び出し用のオブジェクトを作成。この時点でアクセストークンがないと認証が必要になる。
        val service = Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build()

        // ここから先はGoogle Calendar APIを叩く処理
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
