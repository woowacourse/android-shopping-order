package woowacourse.shopping.feature.serversetting

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityServerSettingBinding
import woowacourse.shopping.feature.main.MainActivity

class ServerSettingActivity : AppCompatActivity() {
    lateinit var binding: ActivityServerSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_server_setting)
        setUpView()
    }

    private fun setUpView() {
        binding.btnDeetooServer.setOnClickListener {
            startMain(DEETOO)
        }
        binding.btnEmilServer.setOnClickListener {
            startMain(EMIL)
        }
        binding.btnRoiseServer.setOnClickListener {
            startMain(ROISE)
        }
    }

    private fun startMain(serverUrl: String) {
        if (serverUrl == "") {
            Toast.makeText(this, URL_NOT_EXIST_ERROR, Toast.LENGTH_SHORT).show()
        } else {
            startActivity(MainActivity.getIntent(this, serverUrl))
        }
    }

    companion object {
        private const val URL_NOT_EXIST_ERROR = "URL 이 존재하지 않습니다."
        private const val DEETOO = ""
        private const val EMIL = ""
        private const val ROISE =
            "http://ec2-13-209-67-35.ap-northeast-2.compute.amazonaws.com:8080/"
    }
}
