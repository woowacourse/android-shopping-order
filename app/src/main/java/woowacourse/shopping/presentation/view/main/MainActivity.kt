package woowacourse.shopping.presentation.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
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
            val intent = ProductListActivity.createIntent(this, Server.BASE_URL_TORI)
            startActivity(intent)
        }

        binding.btMainJenna.setOnClickListener {
            val intent = ProductListActivity.createIntent(this, Server.BASE_URL_JENNA)
            startActivity(intent)
        }

        binding.btMainPoi.setOnClickListener {
            val intent = ProductListActivity.createIntent(this, Server.BASE_URL_POI)
            startActivity(intent)
        }
    }
}
