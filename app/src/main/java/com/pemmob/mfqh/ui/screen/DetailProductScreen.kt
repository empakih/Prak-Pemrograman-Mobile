package com.pemmob.mfqh.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pemmob.mfqh.R
import com.pemmob.mfqh.data.model.Product
import com.pemmob.mfqh.ui.viewmodel.ProductUiState
import com.pemmob.mfqh.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailProductScreen(
    productId: Int,
    navController: NavController? = null,
    viewModel: ProductViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var quantity by rememberSaveable { mutableStateOf(1) }

    when (val state = uiState) {
        is ProductUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ProductUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is ProductUiState.Success -> {
            val product = state.products.find { it.id == productId }

            StatelessDetailProduct(
                product = product,
                quantity = quantity,
                onQuantityChange = { quantity = it },
                onBackClick = { navController?.popBackStack() },
                onAddToCartClick = {
                    Toast.makeText(context, "Dimasukkan: $quantity", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatelessDetailProduct(
    product: Product?,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onBackClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(id = R.drawable.back_icon), contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (product != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = "https://pemmob-if.web.app/img/${product.img}",
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.dummy_product),
                    placeholder = painterResource(id = R.drawable.dummy_product)
                )
                Column(modifier = Modifier.padding(all = 16.dp)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rp ${if (product.price % 1.0 == 0.0) product.price.toInt() else product.price}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Deskripsi",
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = product.description ?: "")
                    Text(text = "Stok: ${product.stock}")
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Jumlah Beli")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalIconButton(
                                onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                                enabled = quantity > 1
                            ) {
                                Text(text = "-")
                            }
                            Text(
                                text = quantity.toString(),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            FilledTonalIconButton(
                                onClick = { if (quantity < product.stock) onQuantityChange(quantity + 1) },
                                enabled = quantity < product.stock
                            ) {
                                Text(text = "+")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onAddToCartClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = product.stock > 0 && quantity > 0
                    ) {
                        Text(text = "Tambah ke Keranjang")
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Produk tidak ditemukan")
            }
        }
    }
}

// Kompatibilitas jika dipanggil dengan nama ProductDetailScreen
@Composable
fun ProductDetailScreen(navController: NavController, productId: Int, viewModel: ProductViewModel) {
    DetailProductScreen(productId = productId, navController = navController, viewModel = viewModel)
}
