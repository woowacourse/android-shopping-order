package woowacourse.shopping

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.ServerType.JOY_SERVER
import woowacourse.shopping.ServerType.MINT_SERVER
import woowacourse.shopping.ServerType.SUBAN_MOCK_SERVER
import woowacourse.shopping.databinding.ActivityServerSettingBinding
import woowacourse.shopping.feature.product.MainActivity

class ServerSettingActivity : AppCompatActivity() {
    lateinit var binding: ActivityServerSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_server_setting)
        setServerClickListener()
    }

    private fun setServerClickListener() {
        binding.btSubanMockServer.setOnClickListener {
            startMain(SUBAN_MOCK_SERVER.intentKey)
        }

        binding.btMintServer.setOnClickListener {
            startMain(MINT_SERVER.intentKey)
        }

        binding.btJoyServer.setOnClickListener {
            startMain(JOY_SERVER.intentKey)
        }
    }

    private fun startMain(server: String) {
        startActivity(MainActivity.getIntent(this, server))
        finish()
    }
}
