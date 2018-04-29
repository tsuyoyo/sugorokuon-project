package tsuyogoro.sugorokuon.extension

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

@Suppress("UNCHECKED_CAST")
fun <T : Fragment> FragmentManager.getFocusedFragment(
        position: Int,
        @IdRes pagerViewId: Int
) : T? = findFragmentByTag("android:switcher:$pagerViewId:$position") as? T