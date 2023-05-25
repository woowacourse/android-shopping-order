package woowacourse.shopping.presentation.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityMainBinding
import woowacourse.shopping.presentation.view.productlist.ProductListActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setButtonClick()
    }

    private fun setButtonClick() {
        binding.btMainTori.setOnClickListener {
            val intent = ProductListActivity.createIntent(this, BASE_URL_TORI, TOKEN)
            startActivity(intent)
        }

        binding.btMainJenna.setOnClickListener {
            val intent = ProductListActivity.createIntent(this, BASE_URL_JENNA, TOKEN)
            startActivity(intent)
        }

        binding.btMainPoi.setOnClickListener {
            val intent = ProductListActivity.createIntent(this, BASE_URL_POI, TOKEN)
            startActivity(intent)
        }
    }

    companion object {
        private const val BASE_URL_TORI = "http://13.209.68.194:8080"
        private const val BASE_URL_JENNA = "http://3.34.178.40:8080"
        private const val BASE_URL_POI = "http://3.39.194.150:8080"

        private const val TOKEN = "a2FuZ3NqOTY2NUBnbWFpbC5jb206MTIzNA=="
    }
}
