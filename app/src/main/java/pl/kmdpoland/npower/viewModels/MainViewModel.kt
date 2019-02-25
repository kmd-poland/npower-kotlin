package pl.kmdpoland.npower.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import java.util.*

class MainViewModel : ViewModel() {
    fun buildAuthUri() : Uri {
        return Uri.Builder()
            .scheme("https")
            .authority("dev-910575.oktapreview.com")
            .appendPath("oauth2")
            .appendPath("default")
            .appendPath("v1")
            .appendPath("authorize")
            .appendQueryParameter("client_id", "0oajedfmef0ijCd6s0h7")
            .appendQueryParameter("scope", "openid")
            .appendQueryParameter("response_type", "token")
            .appendQueryParameter("redirect_uri", "npower.kmd.pl://callback")
            .appendQueryParameter("state", "state-"+ UUID.randomUUID())
            .appendQueryParameter("nonce", "foo")
            .build()
    }
}