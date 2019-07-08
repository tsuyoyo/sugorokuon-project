package tsuyogoro.sugorokuon.onboarding

import android.os.Bundle
import android.support.annotation.IntRange
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tsuyogoro.sugorokuon.base.R

class TutorialFragment : Fragment() {

    companion object {

        const val BUNDLE_KEY_INDEX = "index"

        fun create(@IntRange(from = 0, to = 2) index: Int) = TutorialFragment().apply {
            arguments = Bundle().apply {
                putInt(BUNDLE_KEY_INDEX, index)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(
            selectLayout(arguments?.getInt(BUNDLE_KEY_INDEX) ?: 0), container, false)

    private fun selectLayout(index: Int) =
            when (index) {
                0 -> R.layout.fragment_tutorial_page01
                1 -> R.layout.fragment_tutorial_page02
                else -> R.layout.fragment_tutorial_page03
            }

}