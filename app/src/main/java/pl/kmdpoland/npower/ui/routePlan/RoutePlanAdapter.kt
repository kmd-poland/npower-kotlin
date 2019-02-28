package pl.kmdpoland.npower.ui.routePlan

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import pl.kmdpoland.npower.R
import pl.kmdpoland.npower.data.Visit
import java.text.SimpleDateFormat

class RoutePlanAdapter(val items : List<Visit>, val context: Context) : RecyclerView.Adapter<VisitViewHolder>() {

    val itemSelectedSubject = PublishSubject.create<Visit>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val holder = VisitViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.route_plan_item,
                parent,
                false
            )
        )

        holder.itemView.isClickable = true
        holder.itemView.clicks().subscribe { itemSelectedSubject.onNext(holder.visit) }
        return holder
    }

    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        val item = items.get(position)

        holder.visit = item

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

