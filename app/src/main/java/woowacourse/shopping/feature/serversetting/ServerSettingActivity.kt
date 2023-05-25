package woowacourse.shopping.feature.serversetting

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityServerSettingBinding
import woowacourse.shopping.feature.main.MainActivity
import woowacourse.shopping.user.Server
import woowacourse.shopping.user.ServerInfo

class ServerSettingActivity : AppCompatActivity() {
    lateinit var binding: ActivityServerSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_server_setting)
        setUpView()
    }

    private fun setUpView() {
        binding.btnDeetooServer.setOnClickListener {
            ServerInfo.changeServer(DEETOO)
            startMain(DEETOO.url)
        }
        binding.btnEmilServer.setOnClickListener {
            ServerInfo.changeServer(EMIL)
            startMain(EMIL.url)
        }
        binding.btnRoiseServer.setOnClickListener {
            ServerInfo.changeServer(ROISE)
            startMain(ROISE.url)
        }
    }

    private fun startMain(serverUrl: String) {
        if (serverUrl == "") {
            Toast.makeText(this, URL_NOT_EXIST_ERROR, Toast.LENGTH_SHORT).show()
        } else {
            startActivity(MainActivity.getIntent(this))
        }
    }

    companion object {
        private const val URL_NOT_EXIST_ERROR = "URL 이 존재하지 않습니다."
        private val DEETOO = Server.Deetoo
        private val EMIL = Server.Emil
        private val ROISE = Server.Roise
    }
}
