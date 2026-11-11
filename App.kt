package com.zyranto.app.ui

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlinx.coroutines.delay

private data class Tab(val route: String, val label: String, val icon: ImageVector)

@Composable
fun ZyrantoApp(vm: AppViewModel = viewModel()) {
    val nav = rememberNavController()
    val route = nav.currentBackStackEntryAsState().value?.destination?.route.orEmpty()
    val tabs = listOf(
        Tab("home", "Home", Icons.Filled.Home), Tab("shop", "Shop", Icons.Filled.ShoppingBag),
        Tab("cart", "Cart", Icons.Filled.ShoppingCart), Tab("wishlist", "Wishlist", Icons.Filled.Favorite),
        Tab("account", "Account", Icons.Filled.Person)
    )
    // Automatic sync: re-fetch catalogue (prices/stock/new products) every 60 seconds.
    LaunchedEffect(Unit) { while (true) { delay(60_000); vm.refresh(silent = true) } }

    val openProduct: (Int) -> Unit = { nav.navigate("product/$it") }
    val openShop: (String?) -> Unit = { nav.navigate(if (it == null) "shop" else "shop?cat=${Uri.encode(it)}") }

    Scaffold(
        topBar = { ZyTopBar(vm.cartCount, { nav.navigate("search") }, { nav.navigate("cart") }) },
        bottomBar = {
            NavigationBar(containerColor = Ink) {
                tabs.forEach { t ->
                    NavigationBarItem(
                        selected = route.startsWith(t.route),
                        onClick = { nav.navigate(t.route) { popUpTo("home"); launchSingleTop = true } },
                        icon = { Icon(t.icon, t.label) }, label = { Text(t.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Ink, selectedTextColor = Gold, indicatorColor = Gold,
                            unselectedIconColor = Color.White, unselectedTextColor = Color.White
                        )
                    )
                }
            }
        }
    ) { pad ->
        NavHost(nav, "home", Modifier.padding(pad)) {
            composable("home") { HomeScreen(vm, openProduct, openShop) { nav.navigate("categories") } }
            composable("shop?cat={cat}", listOf(navArgument("cat") { type = NavType.StringType; nullable = true; defaultValue = null })) {
                ShopScreen(vm, it.arguments?.getString("cat"), openProduct)
            }
            composable("categories") { CategoriesScreen(openShop) }
            composable("search") { SearchScreen(vm, openProduct) }
            composable("product/{id}", listOf(navArgument("id") { type = NavType.IntType })) {
                DetailScreen(vm, it.arguments?.getInt("id") ?: -1)
            }
            composable("cart") { CartScreen(vm, { nav.navigate("checkout") }, { openShop(null) }) }
            composable("wishlist") { WishlistScreen(vm, openProduct) }
            composable("checkout") { CheckoutScreen(vm) { nav.navigate("orders") { popUpTo("home") } } }
            composable("account") { AccountScreen({ nav.navigate(it) }) }
            composable("login") { LoginScreen() }
            composable("orders") { OrdersScreen() }
            composable("contact") { ContactScreen() }
        }
    }
}
