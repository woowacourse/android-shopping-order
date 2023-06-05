package woowacourse.shopping.ui.selectserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.datasource.retrofit.RetrofitClient
import woowacourse.shopping.databinding.ActivitySelectServerBinding
import woowacourse.shopping.ui.shopping.ShoppingActivity

class SelectServerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectServerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectServerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickEventOnHongsil()
        setClickEventOnMatthew()
    }

    private fun setClickEventOnHongsil() {
        binding.btnSelectServerHongsil.setOnClickListener {
            RetrofitClient.setBaseUrl(HONGSIL_SERVER)
            navigateToShopping()
        }
    }

    private fun setClickEventOnMatthew() {
        binding.btnSelectServerMatthew.setOnClickListener {
            RetrofitClient.setBaseUrl(MATTHEW_SERVER)
            navigateToShopping()
        }
    }

    private fun navigateToShopping() {
        startActivity(ShoppingActivity.from(this))
    }

    companion object {
        private const val HONGSIL_SERVER = "http://3.36.66.250:8080/"
        private const val MATTHEW_SERVER = "http://3.38.132.180:8080/"
    }
}
