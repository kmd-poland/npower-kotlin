package pl.kmdpoland.npower.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class RoutePlanFragment : Fragment() {
    companion object {
        fun newInstance() = RoutePlanFragment()
    }
    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var view = inflater.inflate(pl.kmdpoland.npower.R.layout.route_plan_fragment, container, false)
        return view
    }
}