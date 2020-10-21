package com.enping.transformers.ui.list

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.enping.transformers.R
import com.enping.transformers.ui.edit.TransformerEditFragment
import com.enping.transformers.ui.showToast
import com.enping.transformers.ui.util.EventObserver
import kotlinx.android.synthetic.main.list_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TransformersListFragment : Fragment() {
    private lateinit var adapter: TransformerAdapter
    private val vm: TransformersViewModel by sharedViewModel()

    companion object {
        fun newInstance() = TransformersListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupActions()
        setupRecyclerView()
        setupErrorMessage()
        vm.isLoaded.observe(viewLifecycleOwner, Observer { isLoaded ->
            if(isLoaded) enableActions(isLoaded)
        })
        vm.load()
    }

    private fun setupErrorMessage() {
        vm.errorEvent.observe(viewLifecycleOwner,
            EventObserver {
                if (it is MissingAllSparkException) {
                    MissingAllSparkDialogFragment.show(parentFragmentManager)
                } else {
                    it.showToast(requireContext())
                }
            })
    }

    private fun setupActions() {
        btn_fight_list_fragment.setOnClickListener {
            vm.startWar()
        }
        btn_create_list_fragment.setOnClickListener {
            navCreateTransformer()
        }
    }

    private fun enableActions(isLoaded: Boolean) {
        btn_fight_list_fragment.isEnabled = isLoaded
        btn_create_list_fragment.isEnabled = isLoaded
    }

    private fun setupRecyclerView() {
        adapter = TransformerAdapter()
        adapter.removeClickListener = { id ->
            vm.delete(id)
        }
        adapter.editClickListener = { id ->
            navEditTransformer(id)
        }
        vm.transformers.observe(viewLifecycleOwner, Observer {
            adapter.update(it)
        })
        vm.warEvent.observe(viewLifecycleOwner, EventObserver {
            GameResultDialogFragment.show(parentFragmentManager, it)
        })
        rv_transformers_list_fragment.adapter = adapter
        rv_transformers_list_fragment.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                HORIZONTAL
            )
        )
        rv_transformers_list_fragment.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

    private fun navCreateTransformer() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, TransformerEditFragment.createTransformer())
            .addToBackStack(null)
            .commit()
    }

    private fun navEditTransformer(id: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, TransformerEditFragment.editTransformer(id))
            .addToBackStack(null)
            .commit()
    }
}


