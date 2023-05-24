package woowacourse.shopping.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    companion object {
        private const val SERVER_KEY = "server_key"
        fun getIntent(context: Context, serverKey: Int): Intent =
            Intent(context, LoginActivity::class.java).apply {
                putExtra(SERVER_KEY, serverKey)
            }
    }
}
