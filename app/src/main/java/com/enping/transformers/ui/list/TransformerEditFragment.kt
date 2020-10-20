package com.enping.transformers.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.enping.transformers.R
import kotlinx.android.synthetic.main.edit_fragment.*
import org.koin.android.ext.android.inject

class TransformerEditFragment : Fragment() {

    companion object {
        private const val IS_EDIT = "is_edit"
        private const val ID = "id"
        fun createTransformer() = TransformerEditFragment().apply {
            arguments = Bundle().apply {
                putBoolean(IS_EDIT, false)
                putString(ID, "")
            }
        }

        fun editTransformer(id: String) = TransformerEditFragment().apply {
            arguments = Bundle().apply {
                putBoolean(IS_EDIT, true)
                putString(ID, id)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm: TransformerEditViewModel by inject()
        vm.transformer.observe(viewLifecycleOwner, Observer {
            skb_strength_edit_fragment.setRating(it.strength)
            skb_intelligence_edit_fragment.setRating(it.intelligence)
        })
        vm.load(
            requireArguments().getBoolean(IS_EDIT, false),
            requireArguments().getString(ID, "")
        )
    }

}
