package com.enping.transformers.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.enping.transformers.R
import com.enping.transformers.ui.list.TransformersListFragment
import retrofit2.HttpException
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {

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

fun Throwable.showToast(context: Context) {
    when (this) {
        is IllegalStateException ->
            Toast.makeText(context, this.message, Toast.LENGTH_SHORT).show()
        is HttpException ->
            Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show()
        is UnknownHostException ->
            Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
        else ->
            Toast.makeText(context, this.message, Toast.LENGTH_SHORT).show()
    }
}

