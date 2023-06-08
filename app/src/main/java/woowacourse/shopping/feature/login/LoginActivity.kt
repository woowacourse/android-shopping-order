package woowacourse.shopping.feature.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.cache.CartCache
import woowacourse.shopping.data.cache.ProductCacheImpl
import woowacourse.shopping.data.datasource.local.TokenSharedPreference
import woowacourse.shopping.databinding.ActivityLoginBinding
import woowacourse.shopping.feature.main.MainActivity
import java.util.Base64

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUser1.setOnClickListener {
            login("a@a.com")
        }

        binding.btnUser2.setOnClickListener {
            login("b@b.com")
        }
    }

    private fun login(email: String) {
        CartCache.clear()
        ProductCacheImpl.clear()
        val encoded = Base64.getEncoder().encodeToString("$email:1234".toByteArray())
        TokenSharedPreference.getInstance(applicationContext)
            .setToken(encoded)
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
    }
}
