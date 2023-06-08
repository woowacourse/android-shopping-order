package woowacourse.shopping.presentation.ui.productDetail

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import woowacourse.shopping.data.defaultRepository.DefaultProductRepository
import woowacourse.shopping.data.defaultRepository.DefaultShoppingCartRepository
import woowacourse.shopping.databinding.DialogCartBinding
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.ui.shoppingCart.ShoppingCartActivity

class CartDialog : DialogFragment() {
    private lateinit var binding: DialogCartBinding
    private val productRepository: ProductRepository by lazy {
        DefaultProductRepository()
    }
    private val shoppingCartRepository: ShoppingCartRepository by lazy {
        DefaultShoppingCartRepository()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogCartBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        setCounter()
        clickPut()
    }

    override fun onResume() {
        super.onResume()
        setWidth()
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

    private fun clickPut() {
        binding.buttonOrderDialogPut.setOnClickListener {
            shoppingCartRepository.insert(
                callback = { result ->
                    result.onSuccess {
                        startActivity(ShoppingCartActivity.getIntent(requireContext()))
                        requireActivity().finish()
                    }
                    dismiss()
                },
                productId = binding.product?.id ?: 0,
                quantity = binding.customOrderDialogCounter.currentQuantity,
            )
        }
    }

    private fun setCounter() {
        binding.customOrderDialogCounter.setMinValue(1)
        binding.customOrderDialogCounter.setQuantityText(1)
        binding.customOrderDialogCounter.setIncreaseClickListener {
            val increasedQuantity = binding.customOrderDialogCounter.currentQuantity + 1
            binding.customOrderDialogCounter.setQuantityText(increasedQuantity)
            calculateTotalPrice()
        }
        binding.customOrderDialogCounter.setDecreaseClickListener {
            val decreasedQuantity = binding.customOrderDialogCounter.currentQuantity - 1
            binding.customOrderDialogCounter.setQuantityText(decreasedQuantity)
            calculateTotalPrice()
        }
    }

    private fun getData() {
        val productId: Long = arguments?.getLong(PRODUCT_ID) ?: return dismiss()
        productRepository.fetchProduct(
            callback = { result ->
                result
                    .onSuccess { binding.product = it.product }
                    .onFailure { dismiss() }
            },
            id = productId,
        )
    }

    private fun calculateTotalPrice() {
        val price: Int = binding.product?.price ?: 0
        val quantity: Int = binding.customOrderDialogCounter.currentQuantity
        binding.textOrderDialogPrice.text = (price * quantity).toString()
    }

    companion object {
        private const val PRODUCT_ID = "PRODUCT_ID"
        fun makeDialog(productId: Long): CartDialog {
            return CartDialog().apply {
                val bundle = Bundle()
                bundle.putLong("PRODUCT_ID", productId)
                arguments = bundle
            }
        }
    }
}
