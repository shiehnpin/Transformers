package com.enping.transformers.ui.list

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.enping.transformers.data.BattleStatus
import com.enping.transformers.data.FighterStatus
import com.enping.transformers.data.GameResult
import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MissingAllSparkDialogFragment : DialogFragment() {

    private val vm: TransformersViewModel by sharedViewModel()

    companion object {
        fun show(fragmentManager: FragmentManager) {
            val dialogFragment = MissingAllSparkDialogFragment()
            dialogFragment.show(fragmentManager, "missing_allspark")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage("Couldn't get allspark!")
            .setCancelable(false)
            .setPositiveButton("Retry") { _, _ -> vm.load() }
            .create()

    }
}