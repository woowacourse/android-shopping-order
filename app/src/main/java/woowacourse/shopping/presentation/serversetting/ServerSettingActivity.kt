package woowacourse.shopping.presentation.serversetting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.PreferenceUtil
import woowacourse.shopping.databinding.ActivityServerSettingBinding
import woowacourse.shopping.presentation.productlist.ProductListActivity
import java.util.Base64

class ServerSettingActivity : AppCompatActivity() {
    private val binding: ActivityServerSettingBinding by lazy {
        ActivityServerSettingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setupView()
    }

    private fun encodeCredentialsToBase64(userName: String, password: String): String {
        val credentials = "$userName:$password"
        val encoder: Base64.Encoder = Base64.getEncoder()
        return encoder.encodeToString(credentials.toByteArray())
    }

    private fun setupView() {
        binding.btnGlanServer.setOnClickListener {
            startMain(SERVER_GLAN)
        }
        binding.btnGrayServer.setOnClickListener {
            startMain(SERVER_GRAY)
        }
    }

    private fun startMain(baseUrl: String) {
        ApiClient.baseUrl = baseUrl
        startActivity(ProductListActivity.getIntent(this))
        PreferenceUtil(this).setString(
            AUTHORIZATION_TOKEN,
            encodeCredentialsToBase64(TEST_ID, TEST_PASSWORD)
        )
        finish()
    }

    companion object {
        private const val SERVER_GLAN = "http://13.125.163.216:8080/"
        private const val SERVER_GRAY = "http://54.180.83.161:8080/"
        const val AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN"

        private const val TEST_ID = "scott@gmail.com"
        private const val TEST_PASSWORD = "1234"
    }
}
