package woowacourse.shopping.ui.selectserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.datasource.local.AuthInfoLocalDataSourceImpl
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.databinding.ActivitySelectServerBinding
import woowacourse.shopping.ui.shopping.ShoppingActivity

class SelectServerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectServerBinding
    private val authInfoLocalDataSourceImpl: AuthInfoLocalDataSourceImpl by lazy {
        AuthInfoLocalDataSourceImpl.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectServerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickEventOnHongsil()
        setClickEventOnMatthew()
    }

    private fun setClickEventOnHongsil() {
        binding.btnSelectServerHongsil.setOnClickListener {
            authInfoLocalDataSourceImpl.getAuthInfo()
                ?.let { it -> RetrofitClient.getInstance(HONGSIL_SERVER, it) }
            navigateToShopping()
        }
    }

    private fun setClickEventOnMatthew() {
        binding.btnSelectServerMatthew.setOnClickListener {
            authInfoLocalDataSourceImpl.getAuthInfo()
                ?.let { it -> RetrofitClient.getInstance(MATTHEW_SERVER, it) }
            navigateToShopping()
        }
    }

    private fun navigateToShopping() {
        startActivity(ShoppingActivity.from(this))
    }

    companion object {
        private const val HONGSIL_SERVER = "http://3.36.66.250:8080/"
        private const val MATTHEW_SERVER = "http://3.36.66.250:8080/"
    }
}
