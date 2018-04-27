package tsuyogoro.sugorokuon.setting

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.constant.SearchSongMethod

class SearchSongMethodSettingDialog: DialogFragment() {

    // TODO : listenerの定義

    companion object {
        private const val KEY_SELECTED = "selected"

        fun createDialog(selected: SearchSongMethod) =
            SearchSongMethodSettingDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_SELECTED, selected.name)
                }
            }
    }

    private lateinit var contentView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let {
            val inflater = LayoutInflater.from(context)
            contentView = inflater.inflate(R.layout.dialog_setting_search_song_method, null)

            return AlertDialog.Builder(it)
                .setView(contentView)
                .setPositiveButton(R.string.ok) { dialog, which ->
                    // TODO : listenerへの通知
                }
                .setCancelable(true)
                .create()
        }

        return super.onCreateDialog(savedInstanceState)
    }
}