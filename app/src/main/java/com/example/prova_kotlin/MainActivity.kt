package com.example.prova_kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prova_kotlin.ui.theme.Prova_kotlinTheme
import com.google.gson.Gson
import android.net.Uri

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Prova_kotlinTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(navController)
                }
            }
        }
    }
}

data class Produto(
    val nome: String,
    val categoria: String,
    val preco: Double,
    val quantidade: Int
)

class Estoque {
    companion object {
        private val listaProdutos = mutableListOf<Produto>()

        fun adicionarProduto(produto: Produto) {
            listaProdutos.add(produto)
        }

        fun calcularValorTotalEstoque(): Double {
            return listaProdutos.sumOf { it.preco * it.quantidade }
        }

        fun calcularQuantidadeTotalProdutos(): Int {
            return listaProdutos.sumOf { it.quantidade }
        }

        fun getListaProdutos(): List<Produto> {
            return listaProdutos
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "cadastroProduto") {
        composable("cadastroProduto") {
            CadastroProdutoScreen(
                onNavigateToList = {
                    navController.navigate("listaProdutos")
                }
            )
        }
        composable("listaProdutos") {
            ListaProdutosScreen(
                onNavigateToDetails = { produto ->
                    val produtoJson = Uri.encode(Gson().toJson(produto))
                    navController.navigate("detalhesProduto/$produtoJson")
                },
                onNavigateToEstatisticas = {
                    navController.navigate("estatisticas")
                },
                onNavigateToCadastro = {
                    navController.navigate("cadastroProduto")
                }
            )
        }
        composable("detalhesProduto/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson")
            val produto = Gson().fromJson(produtoJson, Produto::class.java)
            DetalhesProdutoScreen(produto = produto, navController = navController)
        }
        composable("estatisticas") {
            EstatisticasScreen(navController = navController)
        }
    }
}

@Composable
fun CadastroProdutoScreen(onNavigateToList: () -> Unit) {
    var nomeProduto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = nomeProduto,
            onValueChange = { nomeProduto = it },
            label = { Text("Nome do Produto") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoria") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Quantidade em Estoque") },
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = {
                val precoNumerico = preco.toDoubleOrNull()
                val quantidadeNumerica = quantidade.toIntOrNull()

                if (nomeProduto.isNotEmpty() && categoria.isNotEmpty() && precoNumerico != null && quantidadeNumerica != null) {
                    if (precoNumerico > 0 && quantidadeNumerica > 0) {
                        val produto = Produto(nomeProduto, categoria, precoNumerico, quantidadeNumerica)
                        Estoque.adicionarProduto(produto)
                        Toast.makeText(context, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                        onNavigateToList()
                    } else {
                        Toast.makeText(context, "Preço deve ser maior que 0 e quantidade maior que 1.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Todos os campos são obrigatórios e devem ser válidos.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar e Ir para Lista")
        }
    }
}

@Composable
fun ListaProdutosScreen(onNavigateToDetails: (Produto) -> Unit, onNavigateToEstatisticas: () -> Unit, onNavigateToCadastro: () -> Unit) {
    val produtos = Estoque.getListaProdutos()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(produtos) { produto ->
                ProdutoItem(produto, onNavigateToDetails)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onNavigateToEstatisticas,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Ver Estatísticas")
            }
            Button(
                onClick = onNavigateToCadastro,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Cadastrar Produto")
            }
        }
    }
}

@Composable
fun ProdutoItem(produto: Produto, onNavigateToDetails: (Produto) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = "${produto.nome} (${produto.quantidade} unidades)")
        Button(onClick = { onNavigateToDetails(produto) }) {
            Text(text = "Detalhes")
        }
    }
}

@Composable
fun DetalhesProdutoScreen(produto: Produto, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Detalhes do Produto", style = MaterialTheme.typography.titleLarge)
        Text(text = "Nome: ${produto.nome}")
        Text(text = "Categoria: ${produto.categoria}")
        Text(text = "Preço: R$ ${produto.preco}")
        Text(text = "Quantidade: ${produto.quantidade}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Voltar para Lista")
        }
    }
}

@Composable
fun EstatisticasScreen(navController: NavHostController) {
    val valorTotalEstoque = Estoque.calcularValorTotalEstoque()
    val quantidadeTotalProdutos = Estoque.calcularQuantidadeTotalProdutos()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Estatísticas do Estoque", style = MaterialTheme.typography.titleLarge)
        Text(text = "Valor Total do Estoque: R$ $valorTotalEstoque")
        Text(text = "Quantidade Total de Produtos: $quantidadeTotalProdutos")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Voltar para Lista")
        }
    }
}
