package pl.kmdpoland.npower.ui.visit

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
import kotlinx.android.synthetic.main.visit_fragment.*
import pl.kmdpoland.npower.R
import pl.kmdpoland.npower.viewModels.MainViewModel
import pl.kmdpoland.npower.viewModels.RoutePlanViewModel


class VisitFragment : Fragment() {
    companion object {
        fun newInstance() = VisitFragment()
    }

    private lateinit var disposable: CompositeDisposable
    private lateinit var viewModel: RoutePlanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.run {
            viewModel = ViewModelProviders.of(this).get(RoutePlanViewModel::class.java)
        }
    }

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var view = inflater.inflate(pl.kmdpoland.npower.R.layout.visit_fragment, container, false)

        return view
    }

    override fun onResume() {
        super.onResume()

        disposable = CompositeDisposable()

        Glide
            .with(context)
            .load(viewModel.selectedVisit.avatar)
            .apply(RequestOptions().fitCenter().centerCrop())
            .into(image)
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
    }

}