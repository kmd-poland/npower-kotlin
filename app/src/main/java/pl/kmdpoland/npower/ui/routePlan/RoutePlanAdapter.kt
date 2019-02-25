package pl.kmdpoland.npower.ui.routePlan

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pl.kmdpoland.npower.R
import pl.kmdpoland.npower.data.Visit
import java.text.SimpleDateFormat

class RoutePlanAdapter(val items : Array<Visit>, val context: Context) : RecyclerView.Adapter<VisitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        return VisitViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.route_plan_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        val item = items.get(position)

        val formatter = SimpleDateFormat("HH:mm")

        holder?.name?.text = "${formatter.format(item.startTime)} - ${item.firstName} ${item.lastName}"
        holder?.address.text = item.address

        Glide
            .with(context)
            .load(item.avatar)
            .into(holder.avatar)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

