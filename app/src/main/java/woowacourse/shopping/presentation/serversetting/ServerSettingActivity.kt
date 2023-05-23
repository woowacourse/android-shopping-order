package woowacourse.shopping.presentation.serversetting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.App
import woowacourse.shopping.databinding.ActivityServerSettingBinding
import woowacourse.shopping.presentation.productlist.ProductListActivity

class ServerSettingActivity : AppCompatActivity() {
    private val binding: ActivityServerSettingBinding by lazy {
        ActivityServerSettingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setupView()
    }

    private fun setupView() {
        binding.btnGlanServer.setOnClickListener {
            startMain(SERVER_GLAN)
        }
        binding.btnGrayServer.setOnClickListener {
            startMain(SERVER_GRAY)
        }
    }

    private fun startMain(server: String) {
        App.serverUrl = server
        startActivity(ProductListActivity.getIntent(this))
        finish()
    }

    companion object {
        private const val SERVER_GLAN = "http://13.125.163.216:8080/"
        private const val SERVER_GRAY = "http://54.180.83.161:8080/"
    }
}
