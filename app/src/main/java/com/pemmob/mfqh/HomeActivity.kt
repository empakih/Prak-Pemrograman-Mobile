package com.pemmob.mfqh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pemmob.mfqh.ui.screen.DaftarProdukScreen
import com.pemmob.mfqh.ui.screen.DetailProductScreen
import com.pemmob.mfqh.ui.screen.HubungiKamiScreen
import com.pemmob.mfqh.ui.theme.JualanTheme
import com.pemmob.mfqh.ui.viewmodel.ProductViewModel

class HomeActivity : ComponentActivity() {
    private val productViewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JualanTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "daftar_produk") {
                    composable("daftar_produk") {
                        DaftarProdukScreen(
                            navController = navController,
                            viewModel = productViewModel
                        )
                    }
                    composable(
                        route = "detail/{productId}",
                        arguments = listOf(navArgument("productId") {
                            type = NavType.IntType
                        })
                    ) { backStackEntry ->
                        val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                        DetailProductScreen(
                            productId = productId,
                            navController = navController,
                            viewModel = productViewModel
                        )
                    }
                    composable("hubungi_kami") {
                        HubungiKamiScreen(navController = navController)
                    }
                }
            }
        }
    }
}
