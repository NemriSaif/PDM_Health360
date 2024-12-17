package tn.esprit.projet_.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import tn.esprit.projet_.model.Article

@Composable
fun ArticleScreen(
    articles: List<Article>,
    onArticleClick: (Article) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(articles.size) { index ->
            val article = articles[index]
            ArticleItem(article = article, onClick = { onArticleClick(article) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
@Composable
fun ArticleItem(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            when {
                article.imageResId != null -> {
                    // Use resource ID if available
                    Image(
                        painter = painterResource(id = article.imageResId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
                article.imageUrl != null -> {
                    // Use URL if resource ID is not available
                    Image(
                        painter = rememberAsyncImagePainter(article.imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
                else -> {
                    // Fallback for missing image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color.LightGray),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(text = "No Image", color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = article.title,
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = article.description,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
