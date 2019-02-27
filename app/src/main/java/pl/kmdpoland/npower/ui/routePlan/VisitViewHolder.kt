package pl.kmdpoland.npower.ui.routePlan

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.route_plan_item.view.*
import pl.kmdpoland.npower.data.Visit

class VisitViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val name = view.name
    val address = view.address
    val avatar = view.image

    lateinit var visit: Visit

    init {
    }
}