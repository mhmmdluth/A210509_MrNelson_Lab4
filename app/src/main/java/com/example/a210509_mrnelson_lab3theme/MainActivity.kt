package com.example.a210509_mrnelson_lab3theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a210509_mrnelson_lab3theme.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A210509_MrNelson_Lab3ThemeTheme {
                HyPulseDashboardScreen()
            }
        }
    }
}

@Composable
fun HyPulseDashboardScreen() {
    val isDark = isSystemInDarkTheme()
    val currentLitersML = remember { mutableStateOf(1500f) }
    val customInput = remember { mutableStateOf("") }
    val isExpanded = remember { mutableStateOf(false) }

    val dailyGoal = 3200f
    val progressPercentage = (currentLitersML.value / dailyGoal).coerceIn(0f, 1f)
    val backgroundColor = if (isDark) CharcoalBlack else CoolGray

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { HyPulseBottomNav() },
        containerColor = backgroundColor
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. HEADER
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "Good Evening, Luthfi !", color = TextWhite.copy(alpha = 0.7f), fontSize = 16.sp)
                    Row {
                        Text(text = "HY", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Light)
                        Text(text = "PULSE", color = ElectricLime, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Image(Icons.Default.Star, null, colorFilter = ColorFilter.tint(ElectricLime), modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. ANIMATED EXPANDABLE PROGRESS CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                    .clickable { isExpanded.value = !isExpanded.value },
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MutedSlate),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(backgroundColor)
                            .border(2.dp, ElectricLime, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(scaleX = progressPercentage, scaleY = progressPercentage)
                                .clip(CircleShape)
                                .background(ElectricLime.copy(alpha = 0.4f))
                        )
                        Image(
                            imageVector = if (progressPercentage >= 1f) Icons.Default.SentimentVerySatisfied else Icons.Default.Face,
                            contentDescription = null,
                            modifier = Modifier.size(90.dp),
                            colorFilter = ColorFilter.tint(if (progressPercentage >= 0.8f) backgroundColor else ElectricLime)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        HyPulseStatCard(Modifier.weight(1f), "${(progressPercentage * 100).toInt()}%", "COMPLETE", ElectricLime)
                        HyPulseStatCard(Modifier.weight(1f), "${currentLitersML.value.toInt()}ml", "LOGGED", TextWhite)
                    }

                    if (isExpanded.value) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Daily Goal: 3,200ml\nStatus: " + if(progressPercentage >= 1f) "Hydrated!" else "Keep drinking!",
                            color = TextWhite.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. INPUT FORM CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MutedSlate)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("QUICK LOG", color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(250, 500, 1000).forEach { amount ->
                            OutlinedButton(
                                onClick = { currentLitersML.value += amount },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, ElectricLime.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricLime)
                            ) {
                                Text("${if (amount >= 1000) amount / 1000 else amount}${if (amount >= 1000) "L" else "ml"}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("CUSTOM WATER LOG", color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customInput.value,
                        onValueChange = { customInput.value = it },
                        placeholder = { Text("Enter ML", color = TextWhite.copy(alpha = 0.3f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite, focusedBorderColor = ElectricLime)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val amount = customInput.value.toFloatOrNull() ?: 0f
                            currentLitersML.value += amount
                            customInput.value = ""
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricLime)
                    ) {
                        Text("ADD LOG", color = CharcoalBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun HyPulseBottomNav() {
    Row(modifier = Modifier.fillMaxWidth().background(MutedSlate).padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceAround) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Home, null, tint = ElectricLime)
            Text("Home", color = ElectricLime, fontSize = 10.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Person, null, tint = TextWhite.copy(alpha = 0.5f))
            Text("Community", color = TextWhite.copy(alpha = 0.5f), fontSize = 10.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Settings, null, tint = TextWhite.copy(alpha = 0.5f))
            Text("Settings", color = TextWhite.copy(alpha = 0.5f), fontSize = 10.sp)
        }
    }
}

@Composable
fun HyPulseStatCard(modifier: Modifier, label: String, subLabel: String, color: Color) {
    Column(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(Color.Black.copy(alpha = 0.2f)).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = subLabel, color = TextWhite.copy(alpha = 0.4f), fontSize = 10.sp)
    }
}
