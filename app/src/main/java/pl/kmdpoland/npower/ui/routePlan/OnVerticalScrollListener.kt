package pl.kmdpoland.npower.ui.routePlan

import androidx.recyclerview.widget.RecyclerView

abstract class OnVerticalScrollListener : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!recyclerView.canScrollVertically(-1)) {
            onScrolledToTop()
        } else if (!recyclerView.canScrollVertically(1)) {
            onScrolledToBottom()
        }
        if (dy < 0) {
            onScrolledUp(dy)
        } else if (dy > 0) {
            onScrolledDown(dy)
        }
    }

    fun onScrolledUp(dy: Int) {
        onScrolledUp()
    }

    fun onScrolledDown(dy: Int) {
        onScrolledDown()
    }

    open fun onScrolledUp() {}

    open fun onScrolledDown() {}

    open fun onScrolledToTop() {}

    open fun onScrolledToBottom() {}
}