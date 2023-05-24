package woowacourse.shopping.ui.selectserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivitySelectServerBinding

class SelectServerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectServerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectServerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
