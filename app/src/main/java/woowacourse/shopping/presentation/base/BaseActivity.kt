package woowacourse.shopping.presentation.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity<T : ViewDataBinding>(
    @LayoutRes private val layoutResourceId: Int,
) : AppCompatActivity() {
    private var _binding: T? = null
    val binding get() = requireNotNull(_binding)

    private var toast: Toast? = null
    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, layoutResourceId)
        binding.lifecycleOwner = this@BaseActivity
        initCreateView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        toast = null
        snackBar = null
    }

    abstract fun initCreateView()

    fun showToastMessage(message: String) {
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    fun showSnackBar(
        message: String,
        action: Snackbar.() -> Unit = {},
    ) {
        snackBar?.dismiss()
        snackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).apply { action() }
        snackBar?.show()
    }
}
