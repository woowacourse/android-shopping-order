package woowacourse.shopping.ui.serverSetting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.service.RetrofitClient
import woowacourse.shopping.databinding.ActivityServerSettingBinding
import woowacourse.shopping.ui.shopping.ShoppingActivity

class ServerSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServerSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_server_setting)
        setupView()
    }

    private fun setupView() {
        val token = "YUBhLmNvbToxMjM0"
        binding.btnIoServer.setOnClickListener {
            RetrofitClient.getInstance(SERVER_IO, "BASIC $token")
            startMain()
        }
        binding.btnJitoServer.setOnClickListener {
            RetrofitClient.getInstance(SERVER_JITO, "BASIC $token")
            startMain()
        }
    }

    private fun startMain() {
        startActivity(ShoppingActivity.getIntent(this))
        finish()
    }

    companion object {
        private const val SERVER_IO = "http://43.200.169.154:8080"
        private const val SERVER_JITO = "http://13.125.207.155:8080"
    }
}
