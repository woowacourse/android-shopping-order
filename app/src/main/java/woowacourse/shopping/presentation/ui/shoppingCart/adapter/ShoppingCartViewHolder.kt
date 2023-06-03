package woowacourse.shopping.presentation.ui.shoppingCart.adapter

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemShoppingCartProductBinding
import woowacourse.shopping.domain.model.Operator
import woowacourse.shopping.domain.model.ProductInCart

class ShoppingCartViewHolder(
    listener: ShoppingCartClickListener,
    private val binding: ItemShoppingCartProductBinding,
) :
    RecyclerView.ViewHolder(binding.root) {

    private val handler = Handler(Looper.getMainLooper())
    private var token: Long = 0

    private fun performDebounceTask(task: () -> Unit) {
        val runnable = Runnable { task() }
        handler.removeCallbacksAndMessages(token)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            handler.postDelayed(runnable, token, 500)
        } else {
            handler.postAtTime(runnable, token, SystemClock.currentThreadTimeMillis() + 500)
        }
    }

    init {
        binding.root.setOnClickListener {
            listener.clickItem(bindingAdapterPosition)
        }
        binding.buttonCartProductDelete.setOnClickListener {
            listener.clickDelete(bindingAdapterPosition)
        }
        binding.customShoppingCartCounter.setIncreaseClickListener {
            listener.clickChangeQuantity(bindingAdapterPosition, Operator.INCREASE)
            performDebounceTask { listener.applyChangedQuantity(bindingAdapterPosition) }
        }
        binding.customShoppingCartCounter.setDecreaseClickListener {
            listener.clickChangeQuantity(bindingAdapterPosition, Operator.DECREASE)
            performDebounceTask { listener.applyChangedQuantity(bindingAdapterPosition) }
        }
        binding.checkCartProduct.setOnClickListener {
            listener.checkItem(bindingAdapterPosition, (it as CheckBox).isChecked)
        }
        binding.customShoppingCartCounter.setMinValue(1)
    }

    fun bind(product: CartProduct) {
        binding.product = product
        binding.customShoppingCartCounter.setQuantityText(product.cartItem.quantity)
    }

    companion object {
        fun getView(parent: ViewGroup): ItemShoppingCartProductBinding {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ItemShoppingCartProductBinding.inflate(layoutInflater, parent, false)
        }
    }
}
