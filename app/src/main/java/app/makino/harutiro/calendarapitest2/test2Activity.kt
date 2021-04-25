package app.makino.harutiro.calendarapitest2

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.HttpURLConnection
import java.net.URL


class test2Activity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    var mHelper: OAuthHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)
//        mHelper = OAuthHelper(this, "oauth2:" + UrlshortenerScopes.URLSHORTENER, this)
        mHelper!!.startAuth(false)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // アカウント選択や認証画面から返ってきた時の処理をOAuthHelperで受け取る
        mHelper!!.onActivityResult(requestCode, resultCode, data)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        val id = item.itemId
////        return if (id == R.id.action_settings) {
////            true
////        } else super.onOptionsItemSelected(item)
//    }

    fun getAuthToken(authToken: String?) {
        /*
        * TODO ここにAPIリクエストを書く
        * TODO ここでは例として、Google Url ShortenerでURLを短縮している
        */
        object : AsyncTask<Void?, Void?, Boolean>() {
            var mResult = false
            protected fun doInBackground(vararg voids: Void): Boolean {
                mResult = true
                try {
                    // POST URLの生成
                    val builder = Uri.Builder()
                    builder.path("https://www.googleapis.com/urlshortener/v1/url")
                    // AccountManagerで取得したAuthTokenをaccess_tokenパラメータにセットする
                    builder.appendQueryParameter("access_token", authToken)
                    val postUrl = Uri.decode(builder.build().toString())
                    val jsonRequest = JSONObject()
                    jsonRequest.put("longUrl", "http://www.google.co.jp/")
                    val url = URL(postUrl)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doInput = true
                    conn.doOutput = true
                    val ps = PrintStream(conn.outputStream)
                    ps.print(jsonRequest.toString())
                    ps.close()

                    // POSTした結果を取得
                    val `is` = conn.inputStream
                    val reader = BufferedReader(InputStreamReader(`is`))
                    var s: String
                    var postResponse = ""
                    while (reader.readLine().also { s = it } != null) {
                        postResponse += """
                        $s
                        
                        """.trimIndent()
                    }
                    reader.close()
                    Log.v(TAG, postResponse)
                    val shortenInfo = JSONObject(postResponse)
                    // エラー判定
                    if (shortenInfo.has("error")) {
                        Log.e(TAG, postResponse)
                        mResult = false
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    mResult = false
                } catch (e: JSONException) {
                    e.printStackTrace()
                    mResult = false
                }
                Log.v(TAG, "shorten finished.")
                return mResult
            }

            override fun onPostExecute(result: Boolean) {
                if (result) return
                Log.v(TAG, "再認証")
                mHelper!!.startAuth(true)
            }

            override fun doInBackground(vararg params: Void?): Boolean {
                TODO("Not yet implemented")
            }
        }.execute()
    }

}
