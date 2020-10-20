package com.enping.transformers.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.enping.transformers.R
import com.enping.transformers.ui.MainViewModel
import kotlinx.android.synthetic.main.list_fragment.*
import org.koin.android.ext.android.inject

class TransformersListFragment : Fragment() {

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
        val vm: MainViewModel by inject()
        fab_add_transformer_list_fragment.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, TransformerEditFragment.createTransformer())
                .commitNow()
        }
        vm.transformers.observe(viewLifecycleOwner, Observer {
        })
        vm.isLoaded.observe(viewLifecycleOwner, Observer { isLoaded ->
            fab_add_transformer_list_fragment.isEnabled = isLoaded
        })
        vm.load()
    }

}
