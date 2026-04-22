package com.example.a210509_mrnelson_lab4

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
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.a210509_mrnelson_lab4.ui.theme.*

// --- DATA CLASS & VIEWMODEL ---
data class UserData(
    val name: String = "",
    val dailyGoal: Float = 3200f,
    val currentML: Float = 0f
)

class HyPulseViewModel : ViewModel() {
    var userState by mutableStateOf(UserData())
        private set

    fun updateName(newName: String) {
        userState = userState.copy(name = newName)
    }

    fun addWater(amount: Float) {
        userState = userState.copy(currentML = userState.currentML + amount)
    }
}

// --- MAIN ACTIVITY & NAVIGATION ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A210509_MrNelson_Lab4 {
                val viewModel = remember { HyPulseViewModel() }
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "setup") {
                    composable("setup") { SetupScreen(navController, viewModel) }
                    composable("dashboard") { DashboardScreen(navController, viewModel) }
                    composable("insights") { InsightsScreen(navController, viewModel) }
                }
            }
        }
    }
}

// --- SCREEN 1: SETUP ---
@Composable
fun SetupScreen(navController: NavController, viewModel: HyPulseViewModel) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) CharcoalBlack else CoolGray

    Scaffold(containerColor = backgroundColor) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to HYPULSE", color = ElectricLime, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(16.dp))

            Card(colors = CardDefaults.cardColors(containerColor = MutedSlate), shape = RoundedCornerShape(24.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = viewModel.userState.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = { Text("Enter Your Name", color = TextWhite) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,   // FIX: Text is now visible while typing
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = ElectricLime,
                            unfocusedBorderColor = TextWhite.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { if(viewModel.userState.name.isNotBlank()) navController.navigate("dashboard") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricLime)
            ) {
                Text("GET STARTED", color = CharcoalBlack, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- SCREEN 2: DASHBOARD ---
@Composable
fun DashboardScreen(navController: NavController, viewModel: HyPulseViewModel) {
    val user = viewModel.userState
    val progressPercentage = (user.currentML / user.dailyGoal).coerceIn(0f, 1f)
    val customInput = remember { mutableStateOf("") }
    val isExpanded = remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) CharcoalBlack else CoolGray

    Scaffold(
        bottomBar = { HyPulseBottomNav(navController) },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "Good Evening, ${user.name}!", color = TextWhite.copy(alpha = 0.7f), fontSize = 16.sp)
                    Row {
                        Text(text = "HY", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Light); Text(text = "PULSE", color = ElectricLime, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Image(Icons.Default.Star, null, colorFilter = ColorFilter.tint(ElectricLime), modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))

            // ANIMATED PROGRESS CARD
            Card(
                modifier = Modifier.fillMaxWidth().animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)).clickable { isExpanded.value = !isExpanded.value },
                shape = RoundedCornerShape(32.dp), colors = CardDefaults.cardColors(containerColor = MutedSlate), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(140.dp).clip(CircleShape).background(backgroundColor).border(2.dp, ElectricLime, CircleShape), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.fillMaxSize().graphicsLayer(scaleX = progressPercentage, scaleY = progressPercentage).clip(CircleShape).background(ElectricLime.copy(alpha = 0.4f)))
                        Image(imageVector = if (progressPercentage >= 1f) Icons.Default.SentimentVerySatisfied else Icons.Default.Face, contentDescription = null, modifier = Modifier.size(90.dp), colorFilter = ColorFilter.tint(if (progressPercentage >= 0.8f) backgroundColor else ElectricLime))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        HyPulseStatCard(Modifier.weight(1f), "${(progressPercentage * 100).toInt()}%", "COMPLETE", ElectricLime)
                        HyPulseStatCard(Modifier.weight(1f), "${user.currentML.toInt()}ml", "LOGGED", TextWhite)
                    }
                    if (isExpanded.value) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Daily Goal: ${user.dailyGoal.toInt()}ml\nStatus: ${if(progressPercentage >= 1f) "Hydrated!" else "Keep drinking!"}", color = TextWhite.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // INPUT FORM
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MutedSlate)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("QUICK LOG", color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(250, 500, 1000).forEach { amount ->
                            OutlinedButton(onClick = { viewModel.addWater(amount.toFloat()) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, ElectricLime.copy(alpha = 0.5f)), colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricLime)) {
                                Text("${if (amount >= 1000) amount / 1000 else amount}${if (amount >= 1000) "L" else "ml"}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("CUSTOM WATER LOG", color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = customInput.value, onValueChange = { customInput.value = it }, placeholder = { Text("Enter ML", color = TextWhite.copy(alpha = 0.3f)) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite, focusedBorderColor = ElectricLime))
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { val amount = customInput.value.toFloatOrNull() ?: 0f; viewModel.addWater(amount); customInput.value = "" }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = ElectricLime)) {
                        Text("ADD LOG", color = CharcoalBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// --- SCREEN 3: INSIGHTS ---
@Composable
fun InsightsScreen(navController: NavController, viewModel: HyPulseViewModel) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) CharcoalBlack else CoolGray
    Scaffold(bottomBar = { HyPulseBottomNav(navController) }, containerColor = backgroundColor) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp)) {
            Text("Hydration Insights", color = ElectricLime, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MutedSlate), shape = RoundedCornerShape(24.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Total Intake: ${viewModel.userState.currentML.toInt()}ml", color = TextWhite)
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = TextWhite.copy(alpha = 0.1f))
                    Text("Remaining to Goal: ${(viewModel.userState.dailyGoal - viewModel.userState.currentML).coerceAtLeast(0f).toInt()}ml", color = TextWhite.copy(alpha = 0.6f))
                }
            }
        }
    }
}

// --- NAVIGATION & UTILS ---
@Composable
fun HyPulseBottomNav(navController: NavController) {
    NavigationBar(containerColor = MutedSlate) {
        // HOME
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") },
            selected = false,
            onClick = { navController.navigate("dashboard") }
        )
        // COMMUNITY (Does nothing as requested)
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Community") },
            selected = false,
            onClick = { /* Do Nothing */ }
        )
        // INSIGHTS (The Extra Functional Button)
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, null) },
            label = { Text("Insights") },
            selected = false,
            onClick = { navController.navigate("insights") }
        )
        // SETTINGS (Does nothing as requested)
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, null) },
            label = { Text("Settings") },
            selected = false,
            onClick = { /* Do Nothing */ }
        )
    }
}

@Composable
fun HyPulseStatCard(modifier: Modifier, label: String, subLabel: String, color: Color) {
    Column(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(Color.Black.copy(alpha = 0.2f)).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = subLabel, color = TextWhite.copy(alpha = 0.4f), fontSize = 10.sp)
    }
}

// COLORS
val ElectricLime = Color(0xFFCCFF00); val CharcoalBlack = Color(0xFF121212); val MutedSlate = Color(0xFF2C2C2E); val CoolGray = Color(0xFFA9A9A9); val TextWhite = Color(0xFFFFFFFF)