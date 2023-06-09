package woowacourse.shopping.presentation.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.preference.UserPreference
import woowacourse.shopping.databinding.ActivityMainBinding
import woowacourse.shopping.presentation.view.productlist.ProductListActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val user: UserPreference by lazy {
        UserPreference.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setEnableButton(false)
        setRadioClick()
        setButtonClick()
    }

    private fun setRadioClick() {
        binding.rdogMainUser.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rdobtMainKrrong.id -> {
                    user.token = KRRONG
                    setEnableButton(true)
                }
                binding.rdobtMainSunny.id -> {
                    user.token = SUNNY
                    setEnableButton(true)
                }
                else -> {
                    setEnableButton(false)
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

    private fun setEnableButton(isEnabled: Boolean) {
        binding.btMainTori.isEnabled = isEnabled
        binding.btMainJenna.isEnabled = isEnabled
        binding.btMainPoi.isEnabled = isEnabled
    }

    private fun moveToProductListView(url: Server.Url) {
        val intent = ProductListActivity.createIntent(this, url)
        startActivity(intent)
    }

    companion object {
        private const val KRRONG = "a2FuZ3NqOTY2NUBnbWFpbC5jb206MTIzNA=="
        private const val SUNNY = "eWlzMDkyNTIxQGdtYWlsLmNvbToxMjM0"
    }
}
