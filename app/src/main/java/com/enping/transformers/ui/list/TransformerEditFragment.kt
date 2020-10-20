package com.enping.transformers.ui.list

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.enping.transformers.R
import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer
import com.enping.transformers.ui.EventObserver
import com.enping.transformers.ui.showToast
import kotlinx.android.synthetic.main.edit_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

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
        setupTransformerDetail()
        setupAction()
        setupErrorMessage()

        if (savedInstanceState == null) {
            vm.load(
                requireArguments().getBoolean(IS_EDIT, false),
                requireArguments().getString(ID, "")
            )
        }
    }

    private fun setupTransformerDetail() {
        vm.transformer.observe(viewLifecycleOwner, Observer {
            if (!this::cachedTransformer.isInitialized) {
                update(it)
                setupListener()
            }
            cachedTransformer = it
        })
    }

    private fun setupAction() {
        vm.isSubmit.observe(viewLifecycleOwner, Observer { isSubmit ->
            if (isSubmit) {
                btn_submit_edit_fragment.text = "Done"
                parentFragmentManager.popBackStack()
            } else {
                btn_submit_edit_fragment.text = "Save"
                btn_submit_edit_fragment.isEnabled = true
            }
        })
    }

    private fun setupErrorMessage() {
        vm.errorEvent.observe(viewLifecycleOwner, EventObserver {
            it.showToast(requireContext())
        })
    }

    private fun setupListener() {
        ed_name_edit_fragment.addTextChangedListener(textWatcher)
        skb_strength_edit_fragment.onRatingChangeListener = { value ->
            vm.edit(cachedTransformer.copy(strength = value))
        }
        skb_intelligence_edit_fragment.onRatingChangeListener = { value ->
            vm.edit(cachedTransformer.copy(intelligence = value))
        }
        skb_speed_edit_fragment.onRatingChangeListener = { value ->
            vm.edit(cachedTransformer.copy(speed = value))
        }
        skb_endurance_edit_fragment.onRatingChangeListener = { value ->
            vm.edit(cachedTransformer.copy(endurance = value))
        }
        skb_rank_edit_fragment.onRatingChangeListener = { value ->
            vm.edit(cachedTransformer.copy(rank = value))
        }
        skb_courage_edit_fragment.onRatingChangeListener = { value ->
            vm.edit(cachedTransformer.copy(courage = value))
        }
        skb_firepower_edit_fragment.onRatingChangeListener = { value ->
            vm.edit(cachedTransformer.copy(firepower = value))
        }
        skb_skill_edit_fragment.onRatingChangeListener = { value ->
            vm.edit(cachedTransformer.copy(skill = value))
        }
        rbg_team_edit_fragment.setOnCheckedChangeListener { _, checkedId ->
            val team =
                if (checkedId == R.id.rb_team_a_edit_fragment) Team.Autobots else Team.Decepticons
            vm.edit(cachedTransformer.copy(team = team.key))
        }
        btn_submit_edit_fragment.setOnClickListener {
            (it as TextView)
            it.isEnabled = false
            it.text = "Uploading..."
            vm.save()
        }
    }

    override fun onDestroyView() {
        ed_name_edit_fragment.removeTextChangedListener(textWatcher)
        super.onDestroyView()
    }

    private fun update(transformer: Transformer) {
        //Prevent moving cursor
        if (ed_name_edit_fragment.text.toString() != transformer.name)
            ed_name_edit_fragment.setText(transformer.name)
        rb_team_a_edit_fragment.isChecked = transformer.enumTeam == Team.Autobots
        rb_team_a_edit_fragment.jumpDrawablesToCurrentState()//Prevent animation when entering
        rb_team_d_edit_fragment.isChecked = transformer.enumTeam == Team.Decepticons
        rb_team_d_edit_fragment.jumpDrawablesToCurrentState()//Prevent animation when entering
        skb_strength_edit_fragment.setRating(transformer.strength)
        skb_intelligence_edit_fragment.setRating(transformer.intelligence)
        skb_speed_edit_fragment.setRating(transformer.speed)
        skb_endurance_edit_fragment.setRating(transformer.endurance)
        skb_rank_edit_fragment.setRating(transformer.rank)
        skb_courage_edit_fragment.setRating(transformer.courage)
        skb_firepower_edit_fragment.setRating(transformer.firepower)
        skb_skill_edit_fragment.setRating(transformer.skill)
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
