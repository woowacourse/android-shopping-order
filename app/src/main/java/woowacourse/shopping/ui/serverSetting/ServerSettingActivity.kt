package woowacourse.shopping.ui.serverSetting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
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
        binding.btnIoServer.setOnClickListener {
            startMain(SERVER_IO)
        }
        binding.btnJitoServer.setOnClickListener {
            startMain(SERVER_JITO)
        }
    }

    private fun startMain(server: String) {
        startActivity(ShoppingActivity.getIntent(this, server))
        finish()
    }

    companion object {
        const val SERVER_IO = "http://43.200.169.154:8080"
        const val SERVER_JITO = "http://13.125.207.155:8080"
    }
}
