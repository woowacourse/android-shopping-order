package woowacourse.shopping.ui.selectserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivitySelectServerBinding
import woowacourse.shopping.ui.login.LoginActivity

class SelectServerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectServerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectServerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickEventOnHongsil()
        setClickEventOnMatthew()
    }

    private fun setClickEventOnHongsil() {
        binding.btnSelectServerHongsil.setOnClickListener {
            navigateToLogin(HONGSIL_SERVER)
        }
    }

    private fun setClickEventOnMatthew() {
        binding.btnSelectServerMatthew.setOnClickListener {
            navigateToLogin(MATTHEW_SERVER)
        }
    }

    private fun navigateToLogin(serverKey: Int) {
        val intent = LoginActivity.getIntent(this, serverKey)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val HONGSIL_SERVER = 0
        private const val MATTHEW_SERVER = 1
    }
}
