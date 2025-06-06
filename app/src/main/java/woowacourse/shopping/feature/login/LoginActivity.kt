package woowacourse.shopping.feature.login

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.account.AccountLocalDataSource
import woowacourse.shopping.data.account.AccountLocalDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
import woowacourse.shopping.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(CartRepositoryImpl(CartRemoteDataSourceImpl()))
    }
    private lateinit var accountLocalDataSource: AccountLocalDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        accountLocalDataSource = AccountLocalDataSourceImpl(this)

        viewModel.loginSuccessEvent.observe(this) { basicKey ->
            accountLocalDataSource.saveBasicKey(
                basicKey = basicKey,
                onComplete = {
                    showToastMessage(getString(R.string.login_alert_success_login))
                    finish()
                },
                onFail = {
                    showToastMessage(getString(R.string.login_alert_save_basic_key_failed))
                },
            )
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
