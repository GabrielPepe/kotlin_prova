package com.example.prova_kotlin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prova_kotlin.ui.theme.Prova_kotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Prova_kotlinTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CadastroProdutoScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }

    companion object {
        val listaProdutos = mutableListOf<Produto>()
    }
}

@Composable
fun CadastroProdutoScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var nomeProduto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }

    Column(
        modifier = modifier
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
                if (nomeProduto.isEmpty() || categoria.isEmpty() || preco.isEmpty() || quantidade.isEmpty()) {
                    Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
                } else {
                    val precoNumerico = preco.toDoubleOrNull()
                    val quantidadeNumerica = quantidade.toIntOrNull()

                    if (precoNumerico == null || quantidadeNumerica == null) {
                        Toast.makeText(context, "Preço e Quantidade devem ser números válidos", Toast.LENGTH_SHORT).show()
                    } else {
                        val produto = Produto(nomeProduto, categoria, precoNumerico, quantidadeNumerica)
                        MainActivity.listaProdutos.add(produto)

                        Toast.makeText(context, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show()

                        nomeProduto = ""
                        categoria = ""
                        preco = ""
                        quantidade = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }
    }
}

data class Produto(
    val nome: String,
    val categoria: String,
    val preco: Double,
    val quantidade: Int
)

@Preview(showBackground = true)
@Composable
fun CadastroProdutoScreenPreview() {
    Prova_kotlinTheme {
        CadastroProdutoScreen()
    }
}
