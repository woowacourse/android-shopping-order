package woowacourse.shopping.presentation.product.catalog.viewHolder

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LifecycleOwner
import woowacourse.shopping.databinding.CartActionLayoutBinding
import woowacourse.shopping.presentation.cart.CartActivity
import woowacourse.shopping.presentation.product.catalog.CatalogViewModel

internal class CartActionViewHolder(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: CatalogViewModel,
    private val cartLauncher: ActivityResultLauncher<Intent>,
) {
    private val binding: CartActionLayoutBinding =
        CartActionLayoutBinding.inflate(LayoutInflater.from(context))

    val rootView = binding.root

    init {
        binding.lifecycleOwner = lifecycleOwner
        binding.viewModel = viewModel

        navigateToCart()
    }

    fun navigateToCart() {
        binding.imageViewCart.setOnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            cartLauncher.launch(intent)
        }
    }
}
