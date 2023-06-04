package woowacourse.shopping.presentation.ui.shoppingCart

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import woowacourse.shopping.data.defaultRepository.DefaultChargeRepository
import woowacourse.shopping.databinding.DialogOrderBinding
import woowacourse.shopping.domain.repository.ChargeRepository
import woowacourse.shopping.domain.util.WoowaResult
import woowacourse.shopping.presentation.ui.myPage.MyPageActivity

class OrderDialog : DialogFragment() {
    private lateinit var binding: DialogOrderBinding
    private val chargeRepository: ChargeRepository by lazy { DefaultChargeRepository() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogOrderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchChange()
        binding.buttonShoppingCartRecharge.setOnClickListener { goToRecharge() }
    }

    override fun onResume() {
        super.onResume()
        setWidth()
    }

    private fun fetchChange() {
        val orderAmount: Long = arguments?.getLong(ORDER_AMOUNT) ?: return dismiss()
        chargeRepository.fetchCharge { result ->
            when (result) {
                is WoowaResult.SUCCESS -> setOrderState(orderAmount, result.data)
                is WoowaResult.FAIL -> dismiss()
            }
        }
    }

    private fun setOrderState(orderAmount: Long, change: Long) {
        if (change - orderAmount < 0) {
            setOrderImpossible()
            return
        }
        setOrderPossible()
    }

    private fun setOrderImpossible() {
        binding.textShoppingCartLack.visibility = View.VISIBLE
        binding.textShoppingCartLackValue.visibility = View.VISIBLE
        binding.buttonShoppingCartOrder.visibility = View.GONE
        binding.buttonShoppingCartRecharge.visibility = View.VISIBLE
    }

    private fun setOrderPossible() {
        binding.textShoppingCartLack.visibility = View.GONE
        binding.textShoppingCartLackValue.visibility = View.GONE
        binding.buttonShoppingCartOrder.visibility = View.VISIBLE
        binding.buttonShoppingCartRecharge.visibility = View.GONE
    }

    private fun goToRecharge() {
        startActivity(Intent(requireContext(), MyPageActivity::class.java))
        dismiss()
    }

    private fun order() {
    }

    private fun setWidth() {
        val windowManager: WindowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (windowManager.currentWindowMetrics.bounds.width() * 0.95).toInt()
        } else {
            (windowManager.defaultDisplay.width * 0.95).toInt()
        }
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        private const val ORDER_AMOUNT = "ORDER_AMOUNT"

        fun makeDialog(orderAmount: Long): OrderDialog {
            return OrderDialog().apply {
                val bundle = Bundle()
                bundle.putLong(ORDER_AMOUNT, orderAmount)
                arguments = bundle
            }
        }
    }
}
