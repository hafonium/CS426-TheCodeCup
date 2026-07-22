package com.example.thecodecup.ui.core.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.thecodecup.domain.models.FoodModel
import com.example.thecodecup.ui.components.buttons.BackButton
import com.example.thecodecup.ui.theme.CoffeeBlue
import com.example.thecodecup.ui.theme.CoffeeCard
import com.example.thecodecup.ui.theme.CoffeeNavy

@Composable
fun FoodCategoryBrowser(
    foods: List<FoodModel>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    onBackToCategories: () -> Unit,
    onFoodSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = foods.map { it.category.trim() }
        .filter { it.isNotEmpty() }
        .distinct()
    val categoryFoods = selectedCategory?.let { category ->
        foods.filter { it.category.trim() == category }
    }.orEmpty()

    Column(modifier.fillMaxSize()) {
        if (selectedCategory == null) {
            Text("Choose a category", fontWeight = FontWeight.SemiBold, color = CoffeeNavy, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(categories, key = { it }) { category ->
                    CategoryCard(category) { onCategorySelected(category) }
                }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButton(onClick = onBackToCategories)
                Text(
                    text = selectedCategory,
                    modifier = Modifier.padding(start = 12.dp),
                    fontWeight = FontWeight.SemiBold,
                    color = CoffeeNavy,
                    fontSize = 18.sp
                )
            }
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(categoryFoods, key = { it.id }) { food ->
                    FoodCard(food) { onFoodSelected(food.id) }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(category: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(CoffeeCard, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.LocalCafe, contentDescription = null, tint = CoffeeBlue)
        Spacer(Modifier.height(10.dp))
        Text(category, color = CoffeeNavy, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FoodCard(food: FoodModel, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(176.dp)
            .background(CoffeeCard, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = food.imageUrl,
                contentDescription = food.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().padding(4.dp)
            )
        }
        Text(food.name, color = CoffeeNavy, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("$${"%.2f".format(food.price)}", color = CoffeeBlue, fontSize = 13.sp)
    }
}
