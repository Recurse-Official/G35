package com.example.sharemeal

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import androidx.navigation.navArgument


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

    NavigationBar {
        val currentRoute = navController.currentDestination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
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
        composable(NavigationItem.Donate.route) { DonatePage() }
        composable(NavigationItem.Profile.route) { ProfilePage() }
        composable("login") { LoginScreen(navController) }
        composable("registration") { RegistrationScreen(navController) }
    }
}

@Composable
fun DonatePage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC2FFC7)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Welcome to the Donate Page", style = MaterialTheme.typography.headlineMedium)
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
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(initialAlpha = 0.3f) + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut()
    ) {
        val availableFoodList = remember { mutableStateListOf<AvailableFood>() }
        LaunchedEffect(Unit) {
            try {
                val response = RetrofitClient.availableFoodApi.getNearbyFood()
                availableFoodList.addAll(response)
            } catch (e: Exception) {
                e.printStackTrace() // Log the error for debugging
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFC2FFC7))
        ) {
            // Add the header as a standalone item
            item {
                Header()
            }

            // Add the Top5Section as another item
            item {
                Top5Section()
            }

            item {
                Heading()
            }

            // Add the list of available food
            items(availableFoodList) { foodItem ->
                AvailableFoodCard(foodItem)
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
            modifier = Modifier.padding(8.dp) // Reduced padding inside the card
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "          Names",
                    style = TextStyle(
                        fontSize = 14.sp,  // Reduced font size
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Plates",
                    style = TextStyle(
                        fontSize = 14.sp,  // Reduced font size
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.weight(1f)

                )
            }
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            val topUsers = listOf(
                Triple("1. Samantha", "https://cdn.usegalileo.ai/stability/5ff44fdb-6a31-4421-ab15-d4280fee8b3b.png", "120"),
                Triple("2. Alex", "https://cdn.usegalileo.ai/stability/54856942-75c2-4cdc-9fe5-dcb15f1c301b.png", "95"),
                Triple("3. Emily", "https://cdn.usegalileo.ai/stability/b128d5b1-eded-4af1-b137-fa49cb28a81a.png", "85"),
                Triple("4. Andrew", "https://cdn.usegalileo.ai/stability/ba184ee3-bcc5-48db-80e7-0b598613b265.png", "70"),
                Triple("5. Michael", "https://cdn.usegalileo.ai/stability/8e85a4ee-97a0-48ee-a42f-b708c92a1e40.png", "60")
            )

            topUsers.forEachIndexed { index, (name, imageUrl, plates) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp) // Reduced vertical padding
                        .background(
                            if (index == 0) Color(0xFFE8F5E9)
                            else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(6.dp), // Reduced inner padding for each row
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = name,
                        modifier = Modifier
                            .size(32.dp) // Reduced image size
                            .clip(CircleShape)
                            .background(Color.Transparent),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp)) // Reduced spacing
                    Text(
                        text = name,
                        style = TextStyle(
                            color = if (index == 0) Color(0xFF388E3C) else Color.Black,
                            fontSize = 12.sp, // Reduced font size
                            fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                        ),
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        text = plates,
                        style = TextStyle(
                            color = if (index == 0) Color(0xFF388E3C) else Color.Black,
                            fontSize = 12.sp, // Reduced font size
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

