package woowacourse.shopping.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<T : ViewDataBinding>(
    @LayoutRes private val layoutResourceId: Int,
) : Fragment() {
    private var _binding: T? = null
    val binding get() = requireNotNull(_binding)

    private var toast: Toast? = null
    private var snackBar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initViewCreated()
    }

    abstract fun initViewCreated()

    fun showToastMessage(message: String) {
        toast?.cancel()
        toast = Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        toast = null
        snackBar = null
    }
}
