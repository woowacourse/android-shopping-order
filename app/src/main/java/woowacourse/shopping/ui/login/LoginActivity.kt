package woowacourse.shopping.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.datasource.local.AuthInfoDataSourceImpl
import woowacourse.shopping.data.datasource.remote.LoginDataSourceImpl
import woowacourse.shopping.data.repository.LoginRepositoryImpl
import woowacourse.shopping.databinding.ActivityLoginBinding
import woowacourse.shopping.ui.login.contract.LoginContract
import woowacourse.shopping.ui.login.contract.LoginPresenter

class LoginActivity : AppCompatActivity(), LoginContract.View {
    private val loginPresenter: LoginPresenter by lazy { initPresenter() }
    private lateinit var binding: ActivityLoginBinding

    override fun getLoginState() {
        TODO("Not yet implemented")
    }

    private fun initPresenter() = LoginPresenter(
        intent.getIntExtra(SERVER_KEY, ERROR_VALUE),
        this,
        LoginRepositoryImpl(
            AuthInfoDataSourceImpl.getInstance(this),
            LoginDataSourceImpl(),
        ),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickEventOnLogin()
    }

    private fun setClickEventOnLogin() {
        binding.LoginBtn.setOnClickListener {
            // binding.inputEmail.text
            // binding.inputPassword.text
            // UIModel로 묶기
            loginPresenter.postAuthInfo()
        }
    }

    companion object {
        private const val SERVER_KEY = "server_key"
        private const val ERROR_VALUE = 500
        fun getIntent(context: Context, serverKey: Int): Intent =
            Intent(context, LoginActivity::class.java).apply {
                putExtra(SERVER_KEY, serverKey)
            }
    }
}
