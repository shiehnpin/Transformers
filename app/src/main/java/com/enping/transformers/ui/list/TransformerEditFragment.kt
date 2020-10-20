package com.enping.transformers.ui.list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.enping.transformers.R
import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer
import kotlinx.android.synthetic.main.edit_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransformerEditFragment : Fragment() {
    private val vm: TransformerEditViewModel by viewModel()

    lateinit var cachedTransformer: Transformer
    private var textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            vm.edit(cachedTransformer.copy(name = s.toString()))
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.transformer.observe(viewLifecycleOwner, Observer {
            if (!this::cachedTransformer.isInitialized) {
                setupListener()
                update(it)
            }
            cachedTransformer = it
        })
        vm.isSubmit.observe(viewLifecycleOwner, Observer { isSubmit ->
            if (isSubmit) parentFragmentManager.popBackStack()
        })

        if (savedInstanceState == null) {
            vm.load(
                requireArguments().getBoolean(IS_EDIT, false),
                requireArguments().getString(ID, "")
            )
        }
    }

    private fun setupListener() {
        ed_name_edit_fragment.addTextChangedListener(textWatcher)
        skb_strength_edit_fragment.onRatingChangeListener = { value ->
            vm.edit(cachedTransformer.copy(strength = value))
        }
        skb_intelligence_edit_fragment.onRatingChangeListener = { value ->
            vm.edit(cachedTransformer.copy(intelligence = value))
        }
        rbg_team_edit_fragment.setOnCheckedChangeListener { _, checkedId ->
            val team =
                if (checkedId == R.id.rb_team_a_edit_fragment) Team.Autobots else Team.Decepticons
            vm.edit(cachedTransformer.copy(team = team.key))
        }
        btn_submit_edit_fragment.setOnClickListener {
            vm.save()
        }
    }

    override fun onDestroyView() {
        ed_name_edit_fragment.removeTextChangedListener(textWatcher)
        super.onDestroyView()
    }

    //TODO, implement custom view onSaveState/onRestoreState
    private fun update(transformer: Transformer) {
        skb_strength_edit_fragment.setRating(transformer.strength)
        skb_intelligence_edit_fragment.setRating(transformer.intelligence)
    }

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

}
