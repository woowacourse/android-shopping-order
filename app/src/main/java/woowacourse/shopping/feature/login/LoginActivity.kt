package woowacourse.shopping.feature.login

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import woowacourse.shopping.R
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
import woowacourse.shopping.databinding.ActivityLoginBinding
import kotlin.getValue

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel = LoginViewModel(CartRepositoryImpl(CartRemoteDataSourceImpl()))
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.loginSuccessEvent.observe(this) { basicKey ->
            val sharedPreferences = this.getSharedPreferences("AccountInfo", Context.MODE_PRIVATE)
            sharedPreferences.edit { putString("basicKey", basicKey) }
            showToastMessage(getString(R.string.login_alert_success_login))
            finish()
        }
        viewModel.loginErrorEvent.observe(this) { loginError ->
            when (loginError) {
                LoginError.Network -> showToastMessage(getString(R.string.login_alert_network_error))
                LoginError.NotFound -> showToastMessage(getString(R.string.login_alert_wrong_account))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
