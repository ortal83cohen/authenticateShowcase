package com.ortal.authenticate_showcase.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ortal.authenticate_showcase.R


class HomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomePageFragment.newInstance())
                .commitNow()
        }
    }
}