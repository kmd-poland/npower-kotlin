package pl.kmdpoland.npower

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import pl.kmdpoland.npower.ui.MainFragment
import pl.kmdpoland.npower.ui.RoutePlanFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val host = NavHostFragment.create(R.navigation.nav_graph)
        supportFragmentManager.beginTransaction().replace(R.id.container, host).setPrimaryNavigationFragment(host).commit()

        if(intent?.data?.scheme == "npower.kmd.pl"){
            val data = intent.data.toString()
            var token = data.subSequence(data.indexOf("access_token=")+13, data.lastIndex)

            supportFragmentManager.popBackStackImmediate()

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RoutePlanFragment.newInstance())
                .commitNow()
        }
        else if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}
