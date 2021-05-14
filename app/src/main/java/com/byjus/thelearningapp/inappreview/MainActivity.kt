package com.byjus.thelearningapp.inappreview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.byjus.thelearningapp.inappreview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.review.setOnClickListener {
            (application as MainApplication).ratingManager.showRating(this).subscribe({
                Toast.makeText(this, "rated", Toast.LENGTH_SHORT).show()
            },  {
                Toast.makeText(this, "error "+it.message, Toast.LENGTH_SHORT).show()
            })
        }
    }
}
