package pl.kmdpoland.npower

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mapbox.mapboxsdk.Mapbox
import pl.kmdpoland.npower.ui.routePlan.RoutePlanFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        setContentView(R.layout.activity_main)

//        val host = NavHostFragment.create(R.navigation.nav_graph)
//        supportFragmentManager.beginTransaction().replace(R.id.container, host).setPrimaryNavigationFragment(host).commit()
//
//        if(intent?.data?.scheme == "npower.kmd.pl"){
//            val data = intent.data.toString()
//            var token = data.subSequence(data.indexOf("access_token=")+13, data.lastIndex)
//
//            supportFragmentManager.popBackStackImmediate()

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RoutePlanFragment.newInstance())
                .commitNow()
//        }
//        else if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, MainFragment.newInstance())
//                .commitNow()
//        }
    }
}
