package woowacourse.shopping.ui.productdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import woowacourse.shopping.databinding.DialogAddToCartBinding
import woowacourse.shopping.ui.customview.CounterEvent
import woowacourse.shopping.ui.productdetail.uistate.ProductDetailUIState

class AddToCartDialog(
    private val product: ProductDetailUIState,
    private val onClickButton: (Long, Int) -> Unit
) : DialogFragment() {
    private lateinit var binding: DialogAddToCartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddToCartBinding.inflate(LayoutInflater.from(context), container, true)

        binding.counter.binding.counterEvent = makeCounterEvent()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAddToCart.setOnClickListener {
            onClickButton(product.id, binding.counter.binding.count ?: return@setOnClickListener)
        }
    }

    private fun makeCounterEvent() = object : CounterEvent {
        override fun onClickPlus(id: Long) {
            binding.counter.binding.count = binding.counter.binding.count?.plus(1)
        }

        override fun onClickMinus(id: Long) {
            binding.counter.binding.count = binding.counter.binding.count?.minus(1)
        }
    }
}
