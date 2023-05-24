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
            val intent = ProductListActivity.createIntent(this, BASE_URL_TORI, TOKEN_TORI)
            startActivity(intent)
        }

        binding.btMainJenna.setOnClickListener {
            val intent = ProductListActivity.createIntent(this, BASE_URL_JENNA, TOKEN_JENNA)
            startActivity(intent)
        }

        binding.btMainPoi.setOnClickListener {
            val intent = ProductListActivity.createIntent(this, BASE_URL_POI, TOKEN_POI)
            startActivity(intent)
        }
    }

    companion object {
        private const val BASE_URL_TORI = "http://13.209.68.194:8080"
        private const val BASE_URL_JENNA = "http://43.201.105.220:8080"
        private const val BASE_URL_POI = "http://3.39.194.150:8080"

        private const val TOKEN_TORI = "YUBhLmNvbToxMjM0"
        private const val TOKEN_JENNA = "YUBhLmNvbToxMjM0"
        private const val TOKEN_POI = "a2FuZ3NqOTY2NUBnbWFpbC5jb206MTIzNA=="
    }
}
