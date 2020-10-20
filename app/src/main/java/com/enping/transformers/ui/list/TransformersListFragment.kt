package com.enping.transformers.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.enping.transformers.R
import com.enping.transformers.ui.MainViewModel
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

        vm.transformers.observe(viewLifecycleOwner, Observer {

        })
        vm.load()
    }

}
