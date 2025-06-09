package woowacourse.shopping.util.recyclerview

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Matchers.allOf

fun nthProductInRecyclerView(
    recyclerViewId: Int,
    position: Int,
    targetViewId: Int,
) = onView(
    allOf(
        withId(targetViewId),
        isDescendantOfA(nthChildOf(withId(recyclerViewId), position)),
    ),
)
