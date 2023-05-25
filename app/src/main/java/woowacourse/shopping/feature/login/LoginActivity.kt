package woowacourse.shopping.feature.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.TokenSharedPreference
import woowacourse.shopping.databinding.ActivityLoginBinding
import woowacourse.shopping.feature.main.MainActivity
import java.util.Base64

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            if (login(email, password)) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Invalid User", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(email: String, password: String): Boolean {

        val encoded = Base64.getEncoder().encodeToString("$email:$password".toByteArray())
        TokenSharedPreference.getInstance(applicationContext)
            .setToken(encoded)
        return true
    }
}
