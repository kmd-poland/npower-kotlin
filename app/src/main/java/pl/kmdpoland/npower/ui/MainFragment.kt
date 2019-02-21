package pl.kmdpoland.npower.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.main_fragment.view.*
import java.util.*

class MainFragment : Fragment() {
    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var view = inflater.inflate(pl.kmdpoland.npower.R.layout.main_fragment, container, false)

        view.button_login
            .clicks()
            .subscribe {

                var uri = Uri.Builder()
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
                    .appendQueryParameter("state", "state-"+UUID.randomUUID())
                    .appendQueryParameter("nonce", "foo")
                    .build()

                var intent = CustomTabsIntent.Builder()
                    .build()

                intent.launchUrl(this.activity, uri)

            }

        return view
    }

}