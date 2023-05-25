package woowacourse.shopping

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.ServerType.JOY_SERVER
import woowacourse.shopping.ServerType.MINT_SERVER
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
        binding.btMintServer.setOnClickListener {
            startMain(MINT_SERVER.url)
        }

        binding.btJoyServer.setOnClickListener {
            startMain(JOY_SERVER.url)
        }
    }

    private fun startMain(server: String) {
        startActivity(MainActivity.getIntent(this, server))
        finish()
    }
}
