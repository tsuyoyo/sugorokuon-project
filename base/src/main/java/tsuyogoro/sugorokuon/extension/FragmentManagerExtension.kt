package tsuyogoro.sugorokuon.extension

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

@Suppress("UNCHECKED_CAST")
fun <T : androidx.fragment.app.Fragment> androidx.fragment.app.FragmentManager.getFocusedFragment(
        position: Int,
        @IdRes pagerViewId: Int
) : T? = findFragmentByTag("android:switcher:$pagerViewId:$position") as? T