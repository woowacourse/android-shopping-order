package woowacourse.shopping.feature.serverSetting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.datasource.local.auth.TokenSharedPreference
import woowacourse.shopping.data.datasource.remote.ServerInfo
import woowacourse.shopping.databinding.ActivityServerSettingBinding
import woowacourse.shopping.feature.main.MainActivity
import java.util.Base64

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
                login()
                startActivity(Intent(this@ServerSettingActivity, MainActivity::class.java))
            }
            secondServerBtn.setOnClickListener {
                ServerInfo.setBaseUrl("onekong")
                login()
                startActivity(Intent(this@ServerSettingActivity, MainActivity::class.java))
            }
            thirdServerBtn.setOnClickListener {
                ServerInfo.setBaseUrl("jamie")
                login()
                startActivity(Intent(this@ServerSettingActivity, MainActivity::class.java))
            }
        }
    }

    private fun login(): Boolean {
        val encoded = Base64.getEncoder().encodeToString("$email:$password".toByteArray())
        TokenSharedPreference.getInstance(applicationContext)
            .setToken(encoded)
        return true
    }

    companion object {
        private const val email = "a@a.com"
        private const val password = "1234"
    }
}
