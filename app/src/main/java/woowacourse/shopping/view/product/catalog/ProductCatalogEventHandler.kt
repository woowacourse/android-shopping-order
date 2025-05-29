package woowacourse.shopping.view.product.catalog

import woowacourse.shopping.view.product.catalog.adapter.LoadMoreViewHolder
import woowacourse.shopping.view.product.catalog.adapter.recent.RecentProductViewHolder
import woowacourse.shopping.view.util.product.ProductViewHolder

interface ProductCatalogEventHandler :
    RecentProductViewHolder.EventHandler,
    ProductViewHolder.EventHandler,
    LoadMoreViewHolder.EventHandler
