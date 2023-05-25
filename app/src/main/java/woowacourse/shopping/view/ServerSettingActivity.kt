package woowacourse.shopping.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.ShoppingOrderSharedPreference
import woowacourse.shopping.databinding.ActivityServerSelectBinding
import woowacourse.shopping.view.productlist.ProductListActivity

class ServerSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServerSelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    private fun setupView() {
        binding.btnSunghaServer.setOnClickListener {
            startMain(SUNGHA_SERVER)
        }
        binding.btnLoganServer.setOnClickListener {
            startMain(LOGAN_SERVER)
        }
    }

    private fun startMain(server: String) {
        val shoppingOrderSharedPreference = ShoppingOrderSharedPreference(this)
        shoppingOrderSharedPreference.baseUrl = server
        startActivity(Intent(this, ProductListActivity::class.java))
        finish()
    }

    companion object {
        private const val SUNGHA_SERVER = "http://43.200.181.131:8080"
        private const val LOGAN_SERVER = "http://13.125.169.219:8080"
    }
}
