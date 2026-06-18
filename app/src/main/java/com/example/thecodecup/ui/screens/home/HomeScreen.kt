package com.example.thecodecup.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thecodecup.ui.theme.DarkBlue
import com.example.thecodecup.utils.ScreenWrapper

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToProfile : () -> Unit = { }
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = {
                onNavigateToProfile()
            },
            modifier = Modifier
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "User Profile",
                tint = DarkBlue
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ScreenWrapper {
        HomeScreen()
    }
}