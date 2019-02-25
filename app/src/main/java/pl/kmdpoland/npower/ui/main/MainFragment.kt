package pl.kmdpoland.npower.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.view.*
import pl.kmdpoland.npower.R
import pl.kmdpoland.npower.viewModels.MainViewModel


class MainFragment : Fragment() {
    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var disposable: CompositeDisposable
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.run {
            viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        }
    }

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var view = inflater.inflate(pl.kmdpoland.npower.R.layout.main_fragment, container, false)

        Glide
            .with(context)
            .load(R.drawable.lake)
            .apply(RequestOptions().fitCenter().centerCrop())
            .into(view.imageView)

        return view
    }

    override fun onResume() {
        super.onResume()

        disposable = CompositeDisposable()

        button_login
            .clicks()
            .subscribe {
                var intent = CustomTabsIntent.Builder()
                    .build()

                intent.launchUrl(this.activity, viewModel.buildAuthUri())
            }
            .addTo(disposable)
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
    }

}