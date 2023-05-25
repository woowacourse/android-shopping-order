package woowacourse.shopping.feature.serverSetting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.service.ServerInfo
import woowacourse.shopping.databinding.ActivityServerSettingBinding
import woowacourse.shopping.feature.login.LoginActivity

class ServerSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServerSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpView()
    }

    private fun setUpView() {
        binding.apply {
            firstServerBtn.text = "모디 서버"
            secondServerBtn.text = "콩하나 서버"
            thirdServerBtn.text = "제이미 서버"

            firstServerBtn.setOnClickListener {
                ServerInfo.setBaseUrl("modi")
                startActivity(Intent(this@ServerSettingActivity, LoginActivity::class.java))
            }
            secondServerBtn.setOnClickListener {
                ServerInfo.setBaseUrl("onekong")
                startActivity(Intent(this@ServerSettingActivity, LoginActivity::class.java))
            }
            thirdServerBtn.setOnClickListener {
                ServerInfo.setBaseUrl("jamie")
                startActivity(Intent(this@ServerSettingActivity, LoginActivity::class.java))
            }
        }
    }
}
