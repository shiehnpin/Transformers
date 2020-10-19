package com.enping.transformers.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.enping.transformers.R
import com.enping.transformers.ui.list.TransformersListFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    val vm: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TransformersListFragment.newInstance())
                .commitNow()
        }
    }
}


