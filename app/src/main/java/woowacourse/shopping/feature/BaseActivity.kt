package woowacourse.shopping.feature

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import woowacourse.shopping.feature.login.LoginActivity
import woowacourse.shopping.util.SingleLiveData

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding ?: error("Binding not initialized")

    abstract fun inflateBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflateBinding()
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    protected fun observeToast(toastLiveData: SingleLiveData<String>) {
        toastLiveData.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    protected fun observeNavigationToLogin(navigateToLogin: SingleLiveData<Unit>) {
        navigateToLogin.observe(this) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}