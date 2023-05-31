package woowacourse.shopping.presentation.serversettings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.shoppingpref.ShoppingOrderSharedPreference
import woowacourse.shopping.databinding.ActivityServerSelectBinding
import woowacourse.shopping.presentation.productlist.ProductListActivity

class ServerSelectActivity : AppCompatActivity() {
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
        shoppingOrderSharedPreference.userInfo = USER_ID
        startActivity(ProductListActivity.getIntent(this))
        finish()
    }

    companion object {
        private const val SUNGHA_SERVER = "http://43.200.181.131:8080"
        private const val LOGAN_SERVER = "http://13.125.169.219:8080"
        private const val USER_ID = "Basic YmVyQGJlci5jb206MTIzNA=="
    }
}
