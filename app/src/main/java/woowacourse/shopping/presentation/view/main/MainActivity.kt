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
    private lateinit var token: Server.Token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setRadioClick()
        setButtonClick()
    }

    private fun setRadioClick() {
        binding.rdogMainUser.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rdobtMainKrrong.id -> {
                    token = Server.Token.KRRONG
                }
                binding.rdobtMainSunny.id -> {
                    token = Server.Token.SUNNY
                }
            }
        }
    }

    private fun setButtonClick() {
        binding.btMainTori.setOnClickListener {
            moveToProductListView(Server.Url.BASE_URL_TORI)
        }

        binding.btMainJenna.setOnClickListener {
            moveToProductListView(Server.Url.BASE_URL_JENNA)
        }

        binding.btMainPoi.setOnClickListener {
            moveToProductListView(Server.Url.BASE_URL_POI)
        }
    }

    private fun moveToProductListView(url: Server.Url) {
        val intent = ProductListActivity.createIntent(this, url, token)
        startActivity(intent)
    }
}
