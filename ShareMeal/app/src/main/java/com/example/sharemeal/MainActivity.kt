package com.example.sharemeal

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sharemeal.ui.theme.ShareMealTheme
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.graphicsLayer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.Gson
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Ensure Firebase is initialized

        window?.apply {
            statusBarColor = android.graphics.Color.parseColor("#C2FFC7") // Set status bar color
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) // Allows custom status bar
        }

        setContent {

                ShareMealTheme {
                    val auth = FirebaseAuth.getInstance()
                    val currentUser = auth.currentUser

                    if (currentUser != null) {
                        // User is already signed in, navigate to the profile screen or other authenticated screen
                        val navController = rememberNavController()
                        MainScreen()
                    } else {
                        // No user is logged in, navigate to the login screen
                        val navController = rememberNavController()
                        AppNavigation(navController)
                    }
//                val navController = rememberNavController()
//                AppNavigation(navController)
                }
            }
        }

}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        AppNavigation(navController, Modifier.padding(innerPadding))
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Leaderboard,
        NavigationItem.Donate,
        NavigationItem.Profile
    )

    NavigationBar(
        containerColor = Color(0xFF006400) // Dark green color
    ) {
        val currentRoute = navController.currentDestination?.route // Get current route from NavController
        items.forEach { item ->
            val isSelected = currentRoute?.contains(item.route) == true

            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) Color.White else Color.LightGray
                    )
                },// White icon when selected, light gray when unselected

                label = {
                    Text(
                        item.title,
                        color = if (isSelected) Color.White else Color.LightGray // White text when selected, light gray when unselected
                    )
                },
                selected = isSelected, // Set the selected state

//                selected = currentRoute == item.route,
                colors = NavigationBarItemColors(
                    selectedIconColor = Color.Green,  // Green when selected
                    unselectedIconColor = Color.Gray, // Optional: Change the unselected icon color
                    disabledIconColor = Color.Gray,   // Optional: Change disabled icon color (if needed)
                    selectedIndicatorColor = Color.Green, // Optional: Customize selected indicator color
                    selectedTextColor = Color.Green, // Optional: Customize selected text color
                    unselectedTextColor = Color.Gray, // Optional: Customize unselected text color
                    disabledTextColor = Color.Gray   // Set disabled text color
                ),
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Leaderboard : NavigationItem("leaderboard", Icons.Default.Home, "Home")
    object Donate : NavigationItem("donate", Icons.Default.ShoppingCart, "Donate")
    object Profile : NavigationItem("profile", Icons.Default.Person, "Profile")
}
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = NavigationItem.Leaderboard.route, modifier = modifier) {
        composable(NavigationItem.Leaderboard.route) { LeaderboardScreen(navController) }
        composable(NavigationItem.Donate.route) { DonatePage(navController) }
        composable(NavigationItem.Profile.route) { ProfilePage() }
        composable("home") { LeaderboardScreen(navController) } // Add HomeScreen here
        composable("login") { LoginScreen(navController) }
        composable("registration") { RegistrationScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonatePage(navController: NavHostController) {
    // Create a Scrollable Column
    var foodType by remember { mutableStateOf("Veg") }
    var foodTypeMenuExpanded by remember { mutableStateOf(false) }
    var foodTitle by remember { mutableStateOf("") }
    var foodAvailable by remember { mutableStateOf("") }
    var servings by remember { mutableStateOf("") }
    var preparedDate by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }
    var addressLine1 by remember { mutableStateOf("") }
    var addressLine2 by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    val context = LocalContext.current // Required to show a Toast

    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC2FFC7)),
        contentAlignment = Alignment.Center
    ) {
        // Scrollable Column
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                // Heading: Donate Food


                Box(
                    modifier = Modifier
                        .fillMaxWidth()  // Take the full width
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Donate Food",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.Center) // Align horizontally in the box
                    )
                }
            }


            item {
                // Food Type Heading and Drop Down
                Text(
                    text = "Food Type",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
//                var foodType by remember { mutableStateOf("Veg") }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = foodType == "Veg",
                            onClick = { foodType = "Veg" }
                        )
                        Text(
                            text = "Veg",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = foodType == "Non-Veg",
                            onClick = { foodType = "Non-Veg" }
                        )
                        Text(
                            text = "Non-Veg",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
            item {
                // Food Title Heading and Text
                Text(
                    text = "Food Title",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                TextField(
                    value = foodTitle,
                    onValueChange = { foodTitle = it },
                    label = { Text("Enter food title") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                // Food Available Heading and Text Box
                Text(
                    text = "Food Available",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                TextField(
                    value = foodAvailable,
                    onValueChange = { foodAvailable = it },
                    label = { Text("Enter available food quantity") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                // Number of Servings
                Text(
                    text = "Number of Servings",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                TextField(
                    value = servings,
                    onValueChange = { servings = it },
                    label = { Text("Enter number of servings") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                // Prepared Date
                Text(
                    text = "Prepared Date",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                TextField(
                    value = preparedDate,
                    onValueChange = { preparedDate = it },
                    label = { Text("Enter prepared date") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                // Expiration Date
                Text(
                    text = "Expiration Date",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                TextField(
                    value = expirationDate,
                    onValueChange = { expirationDate = it },
                    label = { Text("Enter expiration date") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Divider(
                    modifier = Modifier.padding(top = 20.dp),  // Optional padding
                    thickness = 1.dp,  // Set the thickness of the divider
                    color = Color.Gray  // Set the color of the divider (adjust as needed)
                )
            }
            item {
                // Address Heading

                Box(
                    modifier = Modifier
                        .fillMaxWidth()  // Take the full width
                        .padding(bottom = 16.dp, top = 16.dp)
                ) {
                    Text(
                        text = "Address",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.Center) // Align horizontally in the box
                    )
                }

            }
            item {
                // Address Line 1
                Text(
                    text = "Address Line 1",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                TextField(
                    value = addressLine1,
                    onValueChange = { addressLine1 = it },
                    label = { Text("Address Line 1") },
                    modifier = Modifier.fillMaxWidth().background(Color.White),

                    )
            }
            item {
                // Address Line 2
                Text(
                    text = "Address Line 2",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                TextField(
                    value = addressLine2,
                    onValueChange = { addressLine2 = it },
                    label = { Text("Address Line 2") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                // City
                Text(
                    text = "City",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                TextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                // State
                Text(
                    text = "State",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                TextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("State") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                // Country
                Text(
                    text = "Country",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                TextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                // Postal Code
                Text(
                    text = "Postal code",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                TextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    label = { Text("Postal Code") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                // Donate Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val servingsInt = servings.toIntOrNull() ?: 0 // Default to 0 if invalid
                            if (servingsInt is Int ) { // Replace `1` with your actual user_id logic
                                println("Both servings and user_id are integers")
                            }
                                println("Type of servings: ${servingsInt::class.simpleName}")
                                println("Type of user_id: ${1::class.simpleName}") // Replace `1` with actual user_id logic

                                // Create the donation object with form data
                            val donation = FoodDonation(
                                user_id = 1, // Assuming user_id is 1 or dynamically fetched based on the logged-in user
                                food_type = foodType,
                                food_title = foodTitle,
                                food_available = foodAvailable,
                                num_servings = servingsInt,
                                prepared_date = preparedDate,
                                expiration_date = expirationDate,
                                address_1 = addressLine1,
                                address_2 = addressLine2,
                                city = city,
                                state = state,
                                country = country,
                                postal_code = postalCode,
                                latitude = 17.453001,
                                longitude = 78.395264
                            )
                            val gson = Gson()
                            val jsonPayload = gson.toJson(donation)
                            println("Payload: $jsonPayload")


                            // Submit the donation using the Retrofit API
                            addDonation(donation, onSuccess = {
                                // Handle success (e.g., show a success message or navigate to another screen)
                                navController.navigate("home") {
                                    popUpTo("donate") { inclusive = true } // Clear DonatePage from backstack
                                }
                                Toast.makeText(context, "Donation added successfully!", Toast.LENGTH_SHORT).show()
                                println("Donation added successfully!")

                            }, onError = { errorMessage ->
                                // Handle error (e.g., show an error message)
                                println("Error: $errorMessage")
                                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()

                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Donate")
                }
            }
            }
        }
    }



@Composable
fun ProfilePage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC2FFC7)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Welcome to the Profile Page", style = MaterialTheme.typography.headlineMedium)
    }
}



@Composable
fun LeaderboardScreen(navController: NavController) {
    val availableFoodList = remember { mutableStateListOf<AvailableFood>() }
    val isRefreshing = remember { mutableStateOf(false) }

    // Fetch data initially on first composition
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.availableFoodApi.getNearbyFood()
            availableFoodList.clear()
            availableFoodList.addAll(response)
        } catch (e: Exception) {
            e.printStackTrace() // Handle error
        }
    }

    // Fetch data when refreshing is triggered
    LaunchedEffect(isRefreshing.value) {
        if (isRefreshing.value) {
            try {
                val response = RetrofitClient.availableFoodApi.getNearbyFood()
                availableFoodList.clear()
                availableFoodList.addAll(response)
            } catch (e: Exception) {
                e.printStackTrace() // Handle error
            } finally {
                isRefreshing.value = false // Reset refreshing state
            }
        }
    }

    // Define a rotation animation
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
        )
    )

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(initialAlpha = 0.3f) + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut()
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing.value),
            onRefresh = {
                isRefreshing.value = true // Trigger refresh
            },
            indicator = { state, trigger ->
                if (state.isRefreshing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(trigger)
                            .background(Color(0xFFE0F7FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh, // Default Material Icon
                            contentDescription = "Refreshing",
                            modifier = Modifier
                                .size(40.dp)
                                .graphicsLayer { rotationZ = rotationAngle },
                            tint = Color.Blue
                        )
                    }
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFC2FFC7))
            ) {
                // Add the header
                item { Header() }

                // Add Top 5 Section
                item { Top5Section() }

                item{Heading()}

                // Add available food items
                items(availableFoodList) { foodItem ->
                    AvailableFoodCard(foodItem)
                }
            }
        }
    }
}



@Composable
fun Header() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally in the Column
    ) {
        Text(
            text = "Hi Saiteja!",
            style = TextStyle(
                color = Color(0xFF526E48),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.align(Alignment.Start).padding(top = 10.dp) // Space above the title
        )
        Spacer(modifier = Modifier.height(20.dp))
        // Leaderboard title below the profile row
        Text(
            text = "Weekly Leaderboard",
            style = TextStyle(
                color = Color(0xFF526E48),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(top = 10.dp) // Space above the title
        )
    }
}

@Composable
fun Top5Section() {
    val leaderboardUsers = remember { mutableStateListOf<LeaderboardUser>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.leaderboardApi.getLeaderboard()
            leaderboardUsers.clear()
            leaderboardUsers.addAll(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to fetch leaderboard data", Toast.LENGTH_SHORT).show()
        }
    }
    val imageUrls = listOf(
        "https://cdn.usegalileo.ai/stability/5ff44fdb-6a31-4421-ab15-d4280fee8b3b.png",
        "https://cdn.usegalileo.ai/stability/54856942-75c2-4cdc-9fe5-dcb15f1c301b.png",
        "https://cdn.usegalileo.ai/stability/b128d5b1-eded-4af1-b137-fa49cb28a81a.png",
        "https://cdn.usegalileo.ai/stability/ba184ee3-bcc5-48db-80e7-0b598613b265.png",
        "https://cdn.usegalileo.ai/stability/8e85a4ee-97a0-48ee-a42f-b708c92a1e40.png"
    )


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "          Names",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Plates",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                    modifier = Modifier.weight(1f)
                )
            }
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            leaderboardUsers.forEachIndexed { index, user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .background(
                            if (index == 0) Color(0xFFE8F5E9) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrls.getOrElse(index) { "" }), // Dynamically set the image URL based on the index
                        contentDescription = user.name,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = user.name,
                        style = TextStyle(
                            color = if (index == 0) Color(0xFF388E3C) else Color.Black,
                            fontSize = 12.sp,
                            fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                        ),
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        text = user.plates.toString(),
                        style = TextStyle(
                            color = if (index == 0) Color(0xFF388E3C) else Color.Black,
                            fontSize = 12.sp,
                            fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}


@Composable
fun Heading() {
    Column(
        modifier = Modifier
            .fillMaxWidth() // Take full width
            .padding(16.dp), // Padding around the text
        horizontalAlignment = Alignment.CenterHorizontally // Center the text horizontally
    ) {
        Text(
            text = "Food Near You",
            style = TextStyle(
                fontSize = 24.sp,  // Adjust the font size as needed
                fontWeight = FontWeight.Bold,  // Make the text bold
                color = Color.Black // Change text color if needed
            )
        )
    }
}



@Composable
fun AvailableFoodCard(food: AvailableFood) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            // Left Column (Text and Description)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = food.food_title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Description: ",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) // Bold the heading
                )
                Text(text = food.food_available)
                Spacer(modifier = Modifier.height(8.dp))

                // Prepared Date
                Text(
                    text = "Prepared Date: ",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = food.prepared_date ?: "Not available")
                Spacer(modifier = Modifier.height(8.dp))

                // Expiration Date
                Text(
                    text = "Expiration Date: ",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = food.expiration_date ?: "Not available")
                Spacer(modifier = Modifier.height(8.dp))

                // Distance
                Text(
                    text = "Distance: ${String.format("%.2f", food.distance)} km", // Correct string interpolation
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )


                // Distance value with 2 decimal places
//                Text(
//                    text = String.format("%.2f", food.distance) + " km", // Format to 2 decimal places
//                    style = MaterialTheme.typography.bodyMedium
//                )
            }


            // Right Column (Image placeholder for now)
            Image(
                painter = painterResource(id = R.drawable.loginpage),  // Use a placeholder image
                contentDescription = "Food image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray) // Placeholder background color
            )
        }
    }
}






fun saveUserDataToLocal(context: Context, user: UserResponse) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    prefs.edit().apply {
        putInt("id", user.id)
        putString("email", user.email)
        putString("username", user.username)
        putString("full_name", user.full_name)
        putString("phone", user.phone)
        putString("user_type", user.user_type)
        putString("status", user.status)
        apply()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance() // FirebaseAuth instance
    val firestore = FirebaseFirestore.getInstance() // Firestore instance
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Google Sign-In setup
    val googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1074130231273-0e9tclp3s8t2dn9rfgrp4e071l79bls4.apps.googleusercontent.com") // Replace with your client ID
            .requestEmail()
            .build()
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val email = account.email
                val displayName = account.displayName

                if (email != null) {
                    // Check if the user exists in Firestore
                    val userQuery = firestore.collection("users").whereEqualTo("email", email)
                    userQuery.get().addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.documents.isNotEmpty()) {
                            // User exists
                            Toast.makeText(
                                context,
                                "Google Login successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("leaderboard")
                        } else {
                            // User does not exist
                            Toast.makeText(
                                context,
                                "User does not exist in Firebase. Please sign up.",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("registration")
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(
                            context,
                            "Error checking user existence: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Sign-in failed: ${e.message}")
            Toast.makeText(context, "Sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFC2FFC7))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Share Meal!",
            color = Color(0xFF526E48), // Adjust the color to suit your design
            fontSize = 24.sp, // Set the font size for emphasis
            fontWeight = FontWeight.Bold, // Make the text bold
            textAlign = TextAlign.Center, // Center align the text
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp) // Add spacing below the text
        )
        Box(
            modifier = Modifier
                .width(220.dp)
                .height(220.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Green)
        ) {
            Image(
                painter = painterResource(id = R.drawable.loginpage),
                contentDescription = "Registration Banner",
                modifier = Modifier
                    .size(213.dp) // Reduce the size of the image
                    .align(Alignment.Center) // Center the image inside the Box
                    .clip(RoundedCornerShape(16.dp))
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Email Text Field
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFF0F2F4),  // Background color of the TextField
//                textColor = Color(0xFF62825D)        // Color of the text the user enters
            ),
            textStyle = TextStyle(color = Color(0xFF62825D)) // Color of the text being entered

        )


        // Password Text Field
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFF0F2F4)
            ),
            visualTransformation = PasswordVisualTransformation() // Hide password input
        )

        // Login and Signup Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp), // Add spacing between buttons
            horizontalAlignment = Alignment.CenterHorizontally // Center align the buttons
        ){
            Button(
                onClick = {
                    val normalizedEmail = email.trim().lowercase()

                    // 1. Check for empty fields
                    if (normalizedEmail.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // 2. Login using Firebase Authentication with email and password
                    auth.signInWithEmailAndPassword(normalizedEmail, password)
                        .addOnCompleteListener { loginTask ->
                            if (loginTask.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Login successful!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Call fetchUserDetails directly with the correct parameters
                                fetchUserDetails(
                                    email = normalizedEmail,
                                    onSuccess = { user ->
                                        // Save user locally or update UI
                                        saveUserDataToLocal(context, user)
                                        Log.d("Login", "User fetched successfully: $user")

                                        // Show a toast with user's name or email for confirmation
                                        val welcomeMessage = "Welcome ${user.full_name ?: user.email}!\n" +
                                                "Phone: ${user.phone ?: "Not provided"}"
                                        Toast.makeText(context, welcomeMessage, Toast.LENGTH_LONG).show()
                                        navController.navigate("leaderboard")
                                    },
                                    onError = { errorMessage ->
                                        Log.d("login","$errorMessage")
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                // Handle login failure
                                Toast.makeText(context, "Login failed!", Toast.LENGTH_SHORT).show()
                            }
                        }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp), // Set a fixed height for buttons
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1980E6))
            ) {
                Text(text = "Login", color = Color.White)
            }

//            Spacer(modifier = Modifier.width(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
                Text(text = "OR", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray)
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
            }
            AndroidView(
                factory = { context ->
                    com.google.android.gms.common.SignInButton(context).apply {
                        setSize(com.google.android.gms.common.SignInButton.SIZE_WIDE)
                        setOnClickListener {
                            googleSignInClient.signOut().addOnCompleteListener {
                                launcher.launch(googleSignInClient.signInIntent)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )// Match height with other buttons            )


        }

        Spacer(modifier = Modifier.height(18.dp))


        Text(
            text = "Don't have an account? Sign Up",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable {
                    navController.navigate("registration")
                },
            textAlign = TextAlign.Center
        )


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isGoogleSignedIn by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Helper function for input validation
    fun validateInputs(): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isGoogleSignedIn && password.length < 6) {
            Toast.makeText(context, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!phoneNumber.matches(Regex("^[1-9][0-9]{9}\$"))) {
            Toast.makeText(context, "Phone number must be 10 digits starting with 1-9", Toast.LENGTH_SHORT).show()
            return false
        }
        if (fullName.isBlank()) {
            Toast.makeText(context, "Full Name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        // Row for Back Button at the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Centering the Box in the remaining space
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(220.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Green)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.loginpage),
                    contentDescription = "Registration Banner",
                    modifier = Modifier
                        .size(213.dp) // Reduce the size of the image
                        .align(Alignment.Center) // Center the image inside the Box
                        .clip(RoundedCornerShape(16.dp))
                )
            }


            Spacer(modifier = Modifier.height(16.dp))
            // Input fields
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isGoogleSignedIn,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF0F2F4)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isGoogleSignedIn,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF0F2F4)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF0F2F4)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (!isGoogleSignedIn) {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFF0F2F4)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (validateInputs()) {
                        isLoading = true

                        // Prepare user data
                        val user = mapOf(
                            "fullName" to fullName,
                            "phoneNumber" to phoneNumber,
                            "email" to email
                        )

                        // Check if it's a Google Sign-In
                        if (isGoogleSignedIn) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                // Store in Firestore
                                firestore.collection("users").document(userId)
                                    .set(user)
                                    .addOnSuccessListener {
                                        // Also send data to friend's backend
                                        sendToBackend(fullName, phoneNumber, email, password) { success, message ->
                                            isLoading = false
                                            if (success) {
                                                Toast.makeText(
                                                    context,
                                                    "User registered successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navController.navigate("login")
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to store in backend: $message",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Failed to save user details in Firestore: ${it.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                isLoading = false
                                Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Normal email/password registration
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val userId = auth.currentUser?.uid
                                        if (userId != null) {
                                            // Store in Firestore
                                            firestore.collection("users").document(userId)
                                                .set(user)
                                                .addOnSuccessListener {
                                                    // Also send data to friend's backend
                                                    sendToBackend(fullName, phoneNumber, email, password) { success, message ->
                                                        isLoading = false
                                                        if (success) {
                                                            Toast.makeText(
                                                                context,
                                                                "User registered successfully",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            navController.navigate("login")
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Failed to store in backend: $message",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            Log.d("backend","$message")
                                                        }
                                                    }
                                                }
                                                .addOnFailureListener {
                                                    isLoading = false
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to save user details in Firestore: ${it.message}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                }
                                        }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Failed to register: ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1980E6))
            ) {
                Text(
                    text = if (isLoading) "Registering..." else "Continue",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            // Google Sign-In Integration
            val googleSignInClient = GoogleSignIn.getClient(
                context,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("1074130231273-0e9tclp3s8t2dn9rfgrp4e071l79bls4.apps.googleusercontent.com")
                    .requestEmail()
                    .build()
            )

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    if (idToken != null) {
                        val credential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    // Check if user already exists in Firestore
                                    val userId = auth.currentUser?.uid
                                    if (userId != null) {
                                        firestore.collection("users").document(userId).get()
                                            .addOnSuccessListener { document ->
                                                if (document.exists()) {
                                                    // User exists
                                                    Toast.makeText(
                                                        context,
                                                        "Account already exists. Please log in.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    // Populate fields for new registration
                                                    email = account.email ?: ""
                                                    fullName = account.displayName ?: ""
                                                    isGoogleSignedIn = true
                                                    Toast.makeText(
                                                        context,
                                                        "Google Sign-In successful. Please complete the form.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                            .addOnFailureListener {
                                                Log.e(
                                                    "Firestore",
                                                    "Error checking user existence: ${it.message}"
                                                )
                                                Toast.makeText(
                                                    context,
                                                    "Error checking account: ${it.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        authTask.exception?.message ?: "Authentication failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } catch (e: ApiException) {
                    Log.e("GoogleSignIn", "Sign-in failed: ${e.message}")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
                Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Gray
                )
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
            }
            AndroidView(
                factory = { context ->
                    com.google.android.gms.common.SignInButton(context).apply {
                        setSize(com.google.android.gms.common.SignInButton.SIZE_WIDE)
                        setOnClickListener {
                            googleSignInClient.signOut().addOnCompleteListener {
                                launcher.launch(googleSignInClient.signInIntent)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

