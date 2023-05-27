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
        binding.btnDeetooServer.setOnClickListener { startMain(DEETOO) }
        binding.btnEmilServer.setOnClickListener { startMain(EMIL) }
        binding.btnRoiseServer.setOnClickListener { startMain(ROISE) }
    }

    private fun startMain(server: Server) {
        ServerInfo.changeServer(server)
        Toast.makeText(this, getString(R.string.enter_server_text, server.name), Toast.LENGTH_SHORT)
            .show()
        startActivity(MainActivity.getIntent(this))
    }

    companion object {
        private val DEETOO = Server.Deetoo
        private val EMIL = Server.Emil
        private val ROISE = Server.Roise
    }
}
