package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun MainHubView(viewModel: ChatFlowViewModel) {
    val currentScreen = viewModel.currentScreen

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                is AppScreen.Landing -> LandingAuthScreen(viewModel)
                else -> AuthenticatedHubLayout(viewModel, screen)
            }
        }
    }
}

// ==========================================
// 1. LANDING & AUTHORIZATION SCREEN
// ==========================================
@Composable
fun LandingAuthScreen(viewModel: ChatFlowViewModel) {
    var email by remember { mutableStateOf("admin@chatflow.ai") }
    var password by remember { mutableStateOf("password") }
    var name by remember { mutableStateOf("SaaS Developer") }
    var isRegister by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        Color(0xFF021B14), // Elegant integration of the deep dark emerald-950/20 vibe
                        DarkBackground
                    )
                )
            )
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Logo",
                tint = WhatsappGreen,
                modifier = Modifier
                    .size(72.dp)
                    .drawBehind {
                        drawCircle(
                            color = WhatsappGreen.copy(alpha = 0.15f),
                            radius = size.maxDimension * 0.75f
                        )
                    }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "ChatFlow AI",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "WhatsApp SaaS AI Automation Engine",
                style = MaterialTheme.typography.bodyMedium,
                color = WhatsappGreen,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Auth Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.85f)),
                border = BorderStroke(1.dp, Color(0xFF1E293B).copy(alpha = 0.7f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRegister) "Create Account" else "Developer Portal Login",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = if (isRegister) "Start automating campaigns and AI flows" else "Access your channels and analytics logs",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8), // slate-400
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 20.dp)
                    )

                    val textColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhatsappGreen,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedLabelColor = WhatsappGreen,
                        unfocusedLabelColor = Color(0xFF64748B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color(0xFFCBD5E1),
                        focusedLeadingIconColor = WhatsappGreen,
                        unfocusedLeadingIconColor = Color(0xFF475569)
                    )

                    // Sandbox Mode vs Live Mode Selection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!viewModel.isLiveMode) WhatsappGreen.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { viewModel.isLiveMode = false }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Mock Sandbox", color = if (!viewModel.isLiveMode) WhatsappGreen else Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (viewModel.isLiveMode) WhatsappGreen.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { viewModel.isLiveMode = true }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Live Server", color = if (viewModel.isLiveMode) WhatsappGreen else Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (viewModel.isLiveMode) {
                        OutlinedTextField(
                            value = viewModel.backendBaseUrl,
                            onValueChange = { viewModel.backendBaseUrl = it },
                            label = { Text("Express Server Base URL") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = WhatsappGreen) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("backend_url_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textColors,
                            singleLine = true
                        )
                    }

                    if (isRegister) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Business / Owner Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = WhatsappGreen) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_name_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textColors
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Login Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = WhatsappGreen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input"),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = textColors
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Security Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = WhatsappGreen) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = textColors
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.login(email, name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsappGreen),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isRegister) "Register Business" else "Authorize Secure Entry",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A), // Slate 900
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF0F172A))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { isRegister = !isRegister }) {
                        Text(
                            text = if (isRegister) "Already registered? Login to Console" else "Deploy New Team? Create Account",
                            color = WhatsappGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "POWERED BY GEMINI AI • REACT WEB-BACKEND ARCHITECTURE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ==========================================
// 2. MAIN NAVIGATED HUB FRAME
// ==========================================
@Composable
fun AuthenticatedHubLayout(viewModel: ChatFlowViewModel, selectedScreen: AppScreen) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    if (isTablet) {
        // Sidebar Layout
        Row(modifier = Modifier.fillMaxSize()) {
            SidebarNavigationRail(viewModel, selectedScreen)
            VerticalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                ScreenSelector(viewModel, selectedScreen)
            }
        }
    } else {
        // Mobile layout
        Scaffold(
            bottomBar = { SimpleBottomBar(viewModel, selectedScreen) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                ScreenSelector(viewModel, selectedScreen)
            }
        }
    }
}

// Sidebar panel for wide screens
@Composable
fun SidebarNavigationRail(viewModel: ChatFlowViewModel, selectedScreen: AppScreen) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(260.dp)
            .background(DarkSurface)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 4.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = null, tint = WhatsappGreen, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "ChatFlow AI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = "Enterprise Client",
                    fontSize = 11.sp,
                    color = WhatsappGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu Items using guaranteed core icons
        val items = listOf(
            NavigationItem("Dashboard", Icons.Default.Home, AppScreen.Dashboard),
            NavigationItem("QR Connection", Icons.Default.Share, AppScreen.Connection),
            NavigationItem("Live Chats", Icons.Default.Email, AppScreen.LiveChats),
            NavigationItem("Automation Rules", Icons.Default.Edit, AppScreen.AutomationRules),
            NavigationItem("Broadcast campaigns", Icons.Default.PlayArrow, AppScreen.BulkMessaging),
            NavigationItem("Analytics Hub", Icons.Default.Star, AppScreen.Analytics),
            NavigationItem("Contacts", Icons.Default.Person, AppScreen.Contacts),
            NavigationItem("Settings", Icons.Default.Settings, AppScreen.Settings),
            NavigationItem("Admin Deck", Icons.Default.Info, AppScreen.Admin)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(items) { item ->
                val isActive = item.screen::class == selectedScreen::class
                val background = if (isActive) WhatsappTealSecondary.copy(alpha = 0.3f) else Color.Transparent
                val border = if (isActive) BorderStroke(1.dp, WhatsappGreen.copy(alpha = 0.3f)) else null
                val textColor = if (isActive) Color.White else Color.White.copy(alpha = 0.7f)
                val iconColor = if (isActive) WhatsappGreen else Color.White.copy(alpha = 0.5f)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.currentScreen = item.screen },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = background),
                    border = border
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(item.icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = item.label,
                            color = textColor,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(WhatsappTealSecondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = viewModel.currentUserName.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = viewModel.currentUserName,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = viewModel.currentUserPlan,
                    color = WhatsappGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            IconButton(onClick = { viewModel.logout() }) {
                Icon(Icons.Default.Close, contentDescription = "Log out", tint = Color.Red.copy(alpha = 0.8f))
            }
        }
    }
}

// Bottom navigation using core icons
@Composable
fun SimpleBottomBar(viewModel: ChatFlowViewModel, selectedScreen: AppScreen) {
    val items = listOf(
        NavigationItem("Status", Icons.Default.Share, AppScreen.Connection),
        NavigationItem("Chats", Icons.Default.Email, AppScreen.LiveChats),
        NavigationItem("Rules", Icons.Default.Edit, AppScreen.AutomationRules),
        NavigationItem("Campaigns", Icons.Default.PlayArrow, AppScreen.BulkMessaging),
        NavigationItem("More", Icons.Default.Menu, AppScreen.Dashboard)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        items.forEach { item ->
            val isActive = item.screen::class == selectedScreen::class
            NavigationBarItem(
                selected = isActive,
                onClick = { viewModel.currentScreen = item.screen },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = WhatsappGreen,
                    selectedTextColor = WhatsappGreen,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = WhatsappTealSecondary.copy(alpha = 0.2f)
                )
            )
        }
    }
}

data class NavigationItem(val label: String, val icon: ImageVector, val screen: AppScreen)

@Composable
fun ScreenSelector(viewModel: ChatFlowViewModel, screen: AppScreen) {
    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
        },
        label = "DashboardTab"
    ) { current ->
        when (current) {
            is AppScreen.Dashboard -> DashboardOverviewScreen(viewModel)
            is AppScreen.Connection -> WhatsAppConnectionScreen(viewModel)
            is AppScreen.LiveChats -> LiveChatsScreen(viewModel)
            is AppScreen.AutomationRules -> AutomationRulesScreen(viewModel)
            is AppScreen.BulkMessaging -> BulkMessagingScreen(viewModel)
            is AppScreen.Analytics -> AnalyticsScreen(viewModel)
            is AppScreen.Contacts -> ContactsScreen(viewModel)
            is AppScreen.Settings -> SettingsScreen(viewModel)
            is AppScreen.Admin -> AdminPanelScreen(viewModel)
            else -> DashboardOverviewScreen(viewModel)
        }
    }
}

// ==========================================
// 3. DASHBOARD OVERVIEW SCREEN
// = =========================================
@Composable
fun DashboardOverviewScreen(viewModel: ChatFlowViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val rContacts by viewModel.contacts.collectAsStateWithLifecycle()
    val ruleList by viewModel.automationRules.collectAsStateWithLifecycle()
    val campaignList by viewModel.campaigns.collectAsStateWithLifecycle()

    // Immersive Pulse Dot Animation for active gateway feedback
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                // Apply a deep emerald gradient overlay to map the bg-gradient-to-b from-emerald-950/20 in the spec
                val headerBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF022C22).copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = 300.dp.toPx()
                )
                drawRect(brush = headerBrush)
            }
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // 1. IMMERSIVE BRAND HEADER WITH PULSING PIN
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(WhatsappGreen.copy(alpha = pulseAlpha))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ChatFlow AI",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "AUTOMATION ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = WhatsappGreen.copy(alpha = 0.82f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            // Connection Quick Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (viewModel.isConnected) WhatsappGreen.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.1f))
                    .border(1.dp, if (viewModel.isConnected) WhatsappGreen.copy(alpha = 0.25f) else Color.Red.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (viewModel.isConnected) WhatsappGreen else Color.Red)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (viewModel.isConnected) "ONLINE" else "OFFLINE",
                        color = if (viewModel.isConnected) WhatsappGreen else Color.Red,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Welcome greetings area
        Text(
            text = "Welcome, ${viewModel.currentUserName}!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "WhatsApp SaaS AI Gateway Status Dashboard (Mobile Native)",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF94A3B8), // slate-400
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 2. ACTIVE INSTANCE PREMIUM CARD WITH DETAILED PHONE PREVIEW
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, Color(0xFF1E293B).copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(54.dp)) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(WhatsappGreen.copy(alpha = 0.12f))
                                .border(1.dp, WhatsappGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = WhatsappGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        // Absolute indicator badge on active device container
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(if (viewModel.isConnected) WhatsappGreen else Color.Red)
                                .border(2.dp, Color(0xFF0F172A), CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (viewModel.isConnected) "Active WhatsApp Instance" else "Connection Offline",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (viewModel.isConnected) "+1 (555) 012-9984" else "Session Disconnected",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF475569) // slate-600
                )
            }
        }

        // 3. AI STATUS DAEMON NOTIFICATION BAR
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, WhatsappGreen.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = WhatsappGreen)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AIChatflow Daemon Status",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Text(
                        text = viewModel.aiStatusMessage,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Text(
            text = "Active SaaS Channel Performance Metrics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        val totalSent = messages.filter { !it.isReceived }.size
        val totalReceived = messages.filter { it.isReceived }.size

        // 4. STATS TILES FEATURING ROUNDED 3XL WITH GLASS EFFECTS
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardStatsCard(title = "Total API Transacted", count = "${messages.size}", info = "$totalSent Sent • $totalReceived Recv", icon = Icons.Default.ArrowForward, color = WhatsappGreen, modifier = Modifier.weight(1f))
                DashboardStatsCard(title = "Campaigns Run", count = "${campaignList.size}", info = "${campaignList.filter { it.status == "Completed" }.size} Completed", icon = Icons.Default.PlayArrow, color = NeonBlueAccent, modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardStatsCard(title = "Active Rules", count = "${ruleList.size}", info = "${ruleList.filter { it.isAiEnabled }.size} AI-Agents", icon = Icons.Default.Edit, color = WhatsappGreen, modifier = Modifier.weight(1f))
                DashboardStatsCard(title = "Contacts Count", count = "${rContacts.size}", info = "Client Database Pool", icon = Icons.Default.Person, color = Color.White, modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. CUSTOMER SENTIMENT PULSE PANEL
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 420.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, Color(0xFF1E293B).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Customer Sentiment Pulse",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = "Realtime telemetry of incoming chats",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B), // slate-500
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SentimentPieChart(messages)
                }
            }
        }
    }
}

@Composable
fun DashboardStatsCard(title: String, count: String, info: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp), // .rounded-3xl as in the HTML design
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, Color(0xFF1E293B).copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .size(22.dp)
                    .align(Alignment.End)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title, 
                fontSize = 11.sp, 
                color = Color(0xFF94A3B8), // slate-400
                fontWeight = FontWeight.Bold
            )
            Text(
                text = count, 
                fontSize = 26.sp, 
                color = Color.White, 
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = WhatsappGreen,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = info, 
                    fontSize = 11.sp, 
                    color = WhatsappGreen, 
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
fun SentimentPieChart(messages: List<ChatMessage>) {
    val total = messages.size
    val positives = messages.filter { it.sentiment == "Positive" }.size
    val negatives = messages.filter { it.sentiment == "Negative" }.size
    val neutrals = total - positives - negatives

    val posPerc = if (total > 0) (positives.toFloat() / total.toFloat()) else 0.40f
    val negPerc = if (total > 0) (negatives.toFloat() / total.toFloat()) else 0.15f
    val neuPerc = if (total > 0) (neutrals.toFloat() / total.toFloat()) else 0.45f

    Canvas(
        modifier = Modifier
            .size(120.dp)
            .padding(8.dp)
    ) {
        var startAngle = 0f
        val sweepPos = posPerc * 360f
        val sweepNeg = negPerc * 360f
        val sweepNeu = 360f - sweepPos - sweepNeg

        // Positive
        drawArc(
            color = SentimentPositive,
            startAngle = startAngle,
            sweepAngle = sweepPos,
            useCenter = false,
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
        )
        startAngle += sweepPos

        // Negative
        drawArc(
            color = SentimentNegative,
            startAngle = startAngle,
            sweepAngle = sweepNeg,
            useCenter = false,
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
        )
        startAngle += sweepNeg

        // Neutral
        drawArc(
            color = SentimentNeutral,
            startAngle = startAngle,
            sweepAngle = sweepNeu,
            useCenter = false,
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        HorizontalSentimentIndicator("Satisfied Clients (Positive)", positives, posPerc, SentimentPositive)
        HorizontalSentimentIndicator("Neutral Feedbacks", neutrals, neuPerc, SentimentNeutral)
        HorizontalSentimentIndicator("Negative / Unresolved", negatives, negPerc, SentimentNegative)
    }
}

@Composable
fun HorizontalSentimentIndicator(label: String, count: Int, percentage: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
        }
        Text("${(percentage * 100).toInt()}% ($count)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
    }
}

// ==========================================
// 4. WHATSAPP QR CONNECTION SCREEN
// ==========================================
@Composable
fun WhatsAppConnectionScreen(viewModel: ChatFlowViewModel) {
    val connection = viewModel.connectionStatus
    val lazyLogs = viewModel.connectionLogs

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "WhatsApp Web Connection",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Initiate and manage highly stable multi-device web-socket clients",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF94A3B8), // slate-400
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Connection Environment Config Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Linking Gateway Profile", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text(
                            text = if (viewModel.isLiveMode) "Express.js Node API connected" else "Simulated sandbox client context",
                            color = if (viewModel.isLiveMode) WhatsappGreen else Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = viewModel.isLiveMode,
                        onCheckedChange = {
                            viewModel.isLiveMode = it
                            viewModel.addLog("[System] Toggled Gateway Mode: " + if(it) "Live Express Backend" else "Mock Sandbox")
                            if (it) {
                                viewModel.startLiveStatusPoller()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WhatsappGreen,
                            checkedTrackColor = WhatsappGreen.copy(alpha = 0.35f),
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }
                
                if (viewModel.isLiveMode) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = viewModel.backendBaseUrl,
                        onValueChange = { viewModel.backendBaseUrl = it },
                        label = { Text("Express Server Base URL") },
                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = WhatsappGreen, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("backend_url_connection_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedBorderColor = WhatsappGreen,
                            unfocusedBorderColor = Color(0xFF1E293B)
                        ),
                        singleLine = true
                    )
                }
            }
        }

        // CONNECTION METHOD TABS - Visible when disconnected to plan the gateway coupling method
        if (connection == "DISCONNECTED") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                // QR Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (viewModel.connectionMethod == "QR") WhatsappGreen.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { viewModel.connectionMethod = "QR" }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = if (viewModel.connectionMethod == "QR") WhatsappGreen else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "QR Scan Matrix",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (viewModel.connectionMethod == "QR") WhatsappGreen else Color(0xFF94A3B8)
                        )
                    }
                }

                // Phone Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (viewModel.connectionMethod == "PAIRING") WhatsappGreen.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { viewModel.connectionMethod = "PAIRING" }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = if (viewModel.connectionMethod == "PAIRING") WhatsappGreen else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Link Phone Code",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (viewModel.connectionMethod == "PAIRING") WhatsappGreen else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // MAIN COUPLING CLIENT CARD (QR VIEW OR THE INTERACTIVE PIN BLOCKS)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.45f)),
            border = BorderStroke(1.dp, Color(0xFF1E293B).copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Connection Status Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            when (connection) {
                                "CONNECTED" -> WhatsappGreen.copy(alpha = 0.12f)
                                "DISCONNECTED" -> Color.Red.copy(alpha = 0.10f)
                                else -> NeonBlueAccent.copy(alpha = 0.15f)
                            }
                        )
                        .border(
                            1.dp,
                            when (connection) {
                                "CONNECTED" -> WhatsappGreen.copy(alpha = 0.25f)
                                "DISCONNECTED" -> Color.Red.copy(alpha = 0.20f)
                                else -> NeonBlueAccent.copy(alpha = 0.35f)
                            },
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (connection == "CONNECTED") "ACTIVE NODE LIVE" else connection,
                        color = when (connection) {
                            "CONNECTED" -> WhatsappGreen
                            "DISCONNECTED" -> Color.Red
                            else -> NeonBlueAccent
                        },
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // The Display coupling socket window
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF070A13))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    when (connection) {
                        "DISCONNECTED" -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (viewModel.connectionMethod == "QR") Icons.Default.Share else Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = Color(0xFF475569),
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (viewModel.connectionMethod == "QR") "Ready to Scan QR" else "Enter Phone Link",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (viewModel.connectionMethod == "QR") "Generate a master secure session matrix" else "Receive an 8-character secure auth PIN code",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        "GENERATING_QR" -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = WhatsappGreen, strokeWidth = 3.dp, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(18.dp))
                                Text(
                                    text = "Initializing Socket Handshake",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Hashing cryptographic key-pairs",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        "QR_READY" -> {
                            if (viewModel.connectionMethod == "QR") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { viewModel.completeQRScanSimulation() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text("CLICK BOX TO SIMULATE SCAN", fontSize = 10.sp, color = WhatsappGreen, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Box(modifier = Modifier.size(120.dp)) {
                                            Canvas(modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.White)) {
                                                val matrixSize = 5
                                                val step = size.width / matrixSize
                                                for (i in 0 until matrixSize) {
                                                    for (j in 0 until matrixSize) {
                                                        if ((i + j) % 2 == 0 || (i == 0 && j == 0) || (i == matrixSize - 1 && j == 0) || (i == 0 && j == matrixSize - 1)) {
                                                            drawRect(
                                                                color = Color.Black,
                                                                topLeft = Offset(i * step, j * step),
                                                                size = Size(step, step)
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            // Scanning glowing laser animation line!
                                            val infiniteTransition = rememberInfiniteTransition(label = "laser")
                                            val scanY by infiniteTransition.animateFloat(
                                                initialValue = 0f,
                                                targetValue = 120f,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(2000, easing = LinearEasing),
                                                    repeatMode = RepeatMode.Reverse
                                                ),
                                                label = "laserY"
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(2.5.dp)
                                                    .offset(y = scanY.dp)
                                                    .background(
                                                        Brush.horizontalGradient(
                                                            colors = listOf(Color.Transparent, WhatsappGreen, Color.Transparent)
                                                        )
                                                    )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Tap Matrix to Match & Link", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    }
                                }
                            } else {
                                val pairingPin = viewModel.pairingCodeValue ?: "A3F987KP"
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { viewModel.completeQRScanSimulation() }
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("ENTER PAIRING CODE ON PHONE", fontSize = 10.sp, color = WhatsappGreen, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(18.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val cleanPin = pairingPin.replace("-", "")
                                        val firstHalf = cleanPin.take(4)
                                        val secondHalf = cleanPin.drop(4)

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            firstHalf.forEach { char ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp, 36.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0xFF1E293B).copy(alpha = 0.8f))
                                                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(6.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = char.toString(),
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = "—",
                                            color = Color(0xFF475569),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            secondHalf.forEach { char ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp, 36.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0xFF1E293B).copy(alpha = 0.8f))
                                                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(6.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = char.toString(),
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))
                                    Text("How to link: Open Linked Devices ->", fontSize = 10.sp, color = Color(0xFF64748B))
                                    Text("Link with Phone -> Enter this manual code", fontSize = 10.sp, color = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Tap Code to Simulate AutoMatch", fontSize = 10.sp, color = WhatsappGreen, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                        "CONNECTING" -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = NeonBlueAccent, strokeWidth = 3.dp, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(18.dp))
                                Text(
                                    text = "Authenticating Handshake",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Exchanging encryption handshakes",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        "CONNECTED" -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(WhatsappGreen.copy(alpha = 0.12f))
                                        .border(1.dp, WhatsappGreen.copy(alpha = 0.35f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = WhatsappGreen,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Link Established Successfully",
                                    color = WhatsappGreen,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Active multi-device bridge connected.",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Monitoring inbound communication channels",
                                    color = Color(0xFF475569),
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // LOWER INTERACTION AREA
                if (connection == "DISCONNECTED") {
                    if (viewModel.connectionMethod == "PAIRING") {
                        val textColors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhatsappGreen,
                            unfocusedBorderColor = Color(0xFF1E293B),
                            focusedLabelColor = WhatsappGreen,
                            unfocusedLabelColor = Color(0xFF64748B),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color(0xFFCBD5E1),
                            focusedLeadingIconColor = WhatsappGreen,
                            unfocusedLeadingIconColor = Color(0xFF475569)
                        )
                        OutlinedTextField(
                            value = viewModel.pairingPhoneNumber,
                            onValueChange = { viewModel.pairingPhoneNumber = it },
                            label = { Text("Phone Number (with Country Code)") },
                            placeholder = { Text("+1 (555) 012-9984") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = textColors
                        )
                    }

                    Button(
                        onClick = { viewModel.initiateWhatsAppConnect() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("generate_qr_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsappGreen),
                        shape = RoundedCornerShape(14.dp),
                        enabled = true
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (viewModel.connectionMethod == "QR") "Generate Connection QR" else "Generate Link PIN Code",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                        }
                    }
                } else if (connection == "CONNECTED") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.testLatencyPing() },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, Color(0xFF475569).copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Diagnostic Ping", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            }
                        }

                        Button(
                            onClick = { viewModel.disconnectWhatsApp() },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("disconnect_status_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Kill Session", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                    }
                } else {
                    Button(
                        onClick = { viewModel.disconnectWhatsApp() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Reset Connection Handshake", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // TERMINAL CONSOLE STDOUT LOG MONITOR
        Text(
            text = "Active Gateway Live stdout Console",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070A13)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            val scrollState = rememberScrollState()
            LaunchedEffect(lazyLogs.size) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CONSOLE STDOUT MONITOR",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF475569)
                    )
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (connection == "CONNECTED") WhatsappGreen else Color.Gray))
                }

                Spacer(modifier = Modifier.height(4.dp))

                lazyLogs.forEach { logLine ->
                    val textColor = when {
                        logLine.contains("[Baileys]") -> WhatsappGreen
                        logLine.contains("[System]") || logLine.contains("[Diagnose]") -> NeonBlueAccent
                        logLine.contains("[Error]") -> Color(0xFFEF4444)
                        else -> Color(0xFFCBD5E1)
                    }
                    Text(
                        text = logLine,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = textColor,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }

        // DETAILED DIAGNOSTICS STATS CARD (Visible only code path or detailed when linked)
        if (connection == "CONNECTED") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.25f)),
                border = BorderStroke(1.dp, Color(0xFF1E293B).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Session Diagnostics & Metrics", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Socket Latency", color = Color(0xFF64748B), fontSize = 10.sp)
                            Text("${viewModel.latencyMs}ms", color = if (viewModel.latencyMs < 50) WhatsappGreen else NeonBlueAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(modifier = Modifier.weight(1.5f)) {
                            Text("Bound Device Model", color = Color(0xFF64748B), fontSize = 10.sp)
                            Text(viewModel.connectedDeviceModel, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Memory Load", color = Color(0xFF64748B), fontSize = 10.sp)
                            Text("${viewModel.memoryUsageMb} MB", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // CORE DAEMON CONFIGURATOR SETTINGS
        Text(
            text = "Gateway Engineering Parameters",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.35f)),
            border = BorderStroke(1.dp, Color(0xFF1E293B).copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // WebSocket engine dropdown indicator
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Active Engine Module", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text("Switch the core WhatsApp bridging framework", color = Color(0xFF64748B), fontSize = 11.sp)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Baileys API", "Puppeteer", "WAPI").forEach { engine ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (viewModel.selectedEngine.contains(engine)) WhatsappGreen.copy(alpha = 0.15f) else Color.Transparent)
                                    .border(1.dp, if (viewModel.selectedEngine.contains(engine)) WhatsappGreen else Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                    .clickable {
                                        if (connection == "DISCONNECTED") {
                                            viewModel.selectedEngine = if (engine == "WAPI") "WAPI Cloud Webhook" else if (engine == "Puppeteer") "Puppeteer Web" else "Baileys API"
                                            viewModel.addLog("[System] Gateway node engine set to: ${viewModel.selectedEngine}")
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = engine,
                                    color = if (viewModel.selectedEngine.contains(engine)) WhatsappGreen else Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF1E293B))

                // Persistent Session
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cookie Persistent Session Storage", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text("Secures linkage secrets to bypass scan rebuild", color = Color(0xFF64748B), fontSize = 11.sp)
                    }
                    Switch(
                        checked = viewModel.isSessionPersistent,
                        onCheckedChange = {
                            if (connection == "DISCONNECTED") {
                                viewModel.isSessionPersistent = it
                                viewModel.addLog("[System] Toggled Session Persistence: $it")
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WhatsappGreen,
                            checkedTrackColor = WhatsappGreen.copy(alpha = 0.35f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF1E293B))

                // Auto Read Receipts
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Simulate Inbound Read-Receipts", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text("Send double blue tick web-socket triggers automatically", color = Color(0xFF64748B), fontSize = 11.sp)
                    }
                    Switch(
                        checked = viewModel.autoReadReceipts,
                        onCheckedChange = {
                            viewModel.autoReadReceipts = it
                            viewModel.addLog("[System] Toggled Auto Read-Receipts acknowledgement: $it")
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WhatsappGreen,
                            checkedTrackColor = WhatsappGreen.copy(alpha = 0.35f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF1E293B))

                // Reconnect heartbeat delay
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Reconnect Heartbeat Delay", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text("Retry frequency when network limits is detected", color = Color(0xFF64748B), fontSize = 11.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("5s", "10s", "30s").forEach { interval ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (viewModel.reconnectInterval.contains(interval)) WhatsappGreen.copy(alpha = 0.15f) else Color.Transparent)
                                    .border(1.dp, if (viewModel.reconnectInterval.contains(interval)) WhatsappGreen else Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.reconnectInterval = if (interval == "5s") "5 seconds" else if (interval == "10s") "10 seconds" else "30 seconds"
                                        viewModel.addLog("[System] Reconnect timeout modified list: ${viewModel.reconnectInterval}")
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = interval,
                                    color = if (viewModel.reconnectInterval.contains(interval)) WhatsappGreen else Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Icon(imageVector: ImageVector, contentDescription: String?, sizeChange: androidx.compose.ui.unit.Dp, tint: Color) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier.size(sizeChange)
    )
}

// ==========================================
// 5. LIVE CHATS SCREEN (WITH SIMULATION)
// ==========================================
@Composable
fun LiveChatsScreen(viewModel: ChatFlowViewModel) {
    val contactsList by viewModel.contacts.collectAsStateWithLifecycle()
    val allMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val isWide = configuration.screenWidthDp >= 600

    val activeContact = viewModel.selectedContact

    if (isWide) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(width = 0.5.dp, color = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ChatsLeftListPanel(
                    contacts = contactsList,
                    allMessages = allMessages,
                    selectedContact = activeContact,
                    onContactSelected = { viewModel.selectedContact = it }
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (activeContact != null) {
                    ChatViewPanel(
                        viewModel = viewModel,
                        contact = activeContact,
                        messages = allMessages.filter { it.contactPhoneNumber == activeContact.phoneNumber || it.sender == activeContact.phoneNumber }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Email, contentDescription = null, sizeChange = 64.dp, tint = WhatsappGreen.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("WhatsApp Live Console", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Text("Select an open conversation thread to reply or trigger automate bots", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            if (activeContact != null) {
                ChatViewPanel(
                    viewModel = viewModel,
                    contact = activeContact,
                    messages = allMessages.filter { it.contactPhoneNumber == activeContact.phoneNumber || it.sender == activeContact.phoneNumber },
                    onBack = { viewModel.selectedContact = null }
                )
            } else {
                ChatsLeftListPanel(
                    contacts = contactsList,
                    allMessages = allMessages,
                    selectedContact = null,
                    onContactSelected = { viewModel.selectedContact = it }
                )
            }
        }
    }
}

@Composable
fun ChatsLeftListPanel(
    contacts: List<Contact>,
    allMessages: List<ChatMessage>,
    selectedContact: Contact?,
    onContactSelected: (Contact) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhatsappTealSecondary)
                .padding(16.dp)
        ) {
            Text("Active Live Channels", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
        }

        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No open conversation channels", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(contacts) { contact ->
                    val lastMsg = allMessages.filter { it.sender == contact.phoneNumber || (it.sender == "YOU" && it.senderName == contact.name) }.lastOrNull()
                    val isSelected = contact.id == selectedContact?.id
                    val color = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color)
                            .clickable { onContactSelected(contact) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(WhatsappTealSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(contact.name.take(2).uppercase(), fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                if (lastMsg != null) {
                                    Text(
                                        text = android.text.format.DateFormat.format("hh:mm a", lastMsg.timestamp).toString(),
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lastMsg?.messageText ?: "Start chatting...",
                                fontSize = 13.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun ChatViewPanel(
    viewModel: ChatFlowViewModel,
    contact: Contact,
    messages: List<ChatMessage>,
    onBack: (() -> Unit)? = null
) {
    var textInput by remember { mutableStateOf("") }
    var simulateKeywordInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhatsappTealSecondary)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(contact.name.take(2).uppercase(), fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(contact.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                Text(
                    text = if (viewModel.isConnected) "Active Session Connection" else "Daemon Offline",
                    color = if (viewModel.isConnected) WhatsappGreen else Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Action Testing Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = simulateKeywordInput,
                    onValueChange = { simulateKeywordInput = it },
                    placeholder = { Text("Simulate inbound (e.g. price, demo)", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Button(
                    onClick = {
                        if (simulateKeywordInput.isNotBlank()) {
                            viewModel.receiveSimulatorMessage(contact, simulateKeywordInput)
                            simulateKeywordInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsappTealSecondary),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp)
                ) {
                    Text("Trigger WA", fontSize = 11.sp, color = Color.White)
                }
            }
        }

        // Messages Bubble body
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isSelf = !msg.isReceived
                val align = if (isSelf) Alignment.CenterEnd else Alignment.CenterStart
                val bubbleColor = if (isSelf) {
                    if (msg.sender == "AI_AUTO") WhatsappTealSecondary else WhatsappGreen
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
                val textColor = if (isSelf) Color.White else MaterialTheme.colorScheme.onSurface

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = align
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isSelf) 16.dp else 0.dp,
                            bottomEnd = if (isSelf) 0.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(containerColor = bubbleColor),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (msg.sender == "AI_AUTO") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, sizeChange = 12.dp, tint = NeonBlueAccent)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "GEMINI AI AUTO-REPLY",
                                        fontSize = 9.sp,
                                        color = NeonBlueAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                             Text(text = msg.messageText, color = textColor, fontSize = 14.sp)
                             
                             if (msg.translatedText != null) {
                                 Spacer(modifier = Modifier.height(6.dp))
                                 Column(
                                     modifier = Modifier
                                         .fillMaxWidth()
                                         .clip(RoundedCornerShape(8.dp))
                                         .background(if (isSelf) Color.Black.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.5f))
                                         .padding(8.dp)
                                 ) {
                                     Row(verticalAlignment = Alignment.CenterVertically) {
                                         Icon(
                                             imageVector = Icons.Default.Star,
                                             contentDescription = null,
                                             tint = if (isSelf) Color.White else NeonBlueAccent,
                                             modifier = Modifier.size(10.dp)
                                         )
                                         Spacer(modifier = Modifier.width(4.dp))
                                         Text(
                                             text = "Gemini AI Translation:",
                                             fontSize = 9.sp,
                                             fontWeight = FontWeight.Bold,
                                             color = if (isSelf) Color.White.copy(alpha = 0.8f) else Color.Gray
                                         )
                                     }
                                     Spacer(modifier = Modifier.height(2.dp))
                                     Text(
                                         text = msg.translatedText,
                                         fontSize = 13.sp,
                                         color = textColor
                                     )
                                 }
                             } else {
                                 Spacer(modifier = Modifier.height(4.dp))
                                 Row(
                                     modifier = Modifier.fillMaxWidth(),
                                     horizontalArrangement = Arrangement.Start
                                 ) {
                                     listOf("Spanish", "French", "German").forEach { lang ->
                                         Box(
                                             modifier = Modifier
                                                 .padding(end = 4.dp)
                                                 .clip(RoundedCornerShape(4.dp))
                                                 .background(if (isSelf) Color.Black.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f))
                                                 .clickable {
                                                     viewModel.translateMessageText(msg, lang)
                                                 }
                                                 .padding(horizontal = 6.dp, vertical = 2.dp)
                                         ) {
                                             Text(
                                                 text = "→ $lang",
                                                 fontSize = 8.sp,
                                                 fontWeight = FontWeight.Medium,
                                                 color = if (isSelf) Color.White.copy(alpha = 0.9f) else Color.DarkGray
                                             )
                                         }
                                     }
                                 }
                             }
                             Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (msg.sentiment != "Neutral") {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (msg.sentiment == "Positive") SentimentPositive.copy(alpha = 0.2f)
                                                else SentimentNegative.copy(alpha = 0.2f)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = msg.sentiment,
                                            color = if (msg.sentiment == "Positive") SentimentPositive else SentimentNegative,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                Text(
                                    text = android.text.format.DateFormat.format("hh:mm a", msg.timestamp).toString(),
                                    fontSize = 10.sp,
                                    color = if (isSelf) Color.White.copy(alpha = 0.6f) else Color.Gray,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }

        // Typing input
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Type reply to channel...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_text_input"),
                    shape = RoundedCornerShape(24.dp)
                )

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendChatMessage(contact, textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(WhatsappGreen)
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Send", tint = Color(0xFF111B21))
                }
            }
        }
    }
}

// ==========================================
// 6. AUTOMATION RULES SCREEN
// ==========================================
@Composable
fun AutomationRulesScreen(viewModel: ChatFlowViewModel) {
    val ruleList by viewModel.automationRules.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    var keywordInput by remember { mutableStateOf("") }
    var templateInput by remember { mutableStateOf("") }
    var isAiEnabled by remember { mutableStateOf(false) }
    var selectedTone by remember { mutableStateOf("Support") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Conversational Automations",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Configure keyword listener matches and active Gemini agents",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = WhatsappGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF111B21))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Rule", fontWeight = FontWeight.Bold, color = Color(0xFF111B21))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (ruleList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Edit, contentDescription = null, sizeChange = 64.dp, tint = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No triggers registered", fontWeight = FontWeight.Bold)
                    Text("Register automated triggers for user inquiries", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(ruleList) { rule ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(WhatsappTealSecondary.copy(alpha = 0.2f))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "/${rule.keyword.lowercase()}",
                                            fontWeight = FontWeight.Bold,
                                            color = WhatsappGreen,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    if (rule.isAiEnabled) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(NeonBlueAccent.copy(alpha = 0.15f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Star, contentDescription = null, sizeChange = 12.dp, tint = NeonBlueAccent)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Gemini Agent (${rule.promptType})", fontSize = 10.sp, color = NeonBlueAccent, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                IconButton(onClick = { viewModel.removeRule(rule) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Auto Reply Action Context:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = rule.replyTemplate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Create Automation Rule",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = keywordInput,
                        onValueChange = { keywordInput = it },
                        label = { Text("Trigger Keyword (Word Match)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = templateInput,
                        onValueChange = { templateInput = it },
                        label = { Text("Auto-Reply Body (or AI Prompt guidance)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Gemini LLM Assistant", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Formulates highly personalized replies", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = isAiEnabled,
                            onCheckedChange = { isAiEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = WhatsappGreen)
                        )
                    }

                    if (isAiEnabled) {
                        Text("Response Persona Tone:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val tonesList = listOf("Support", "Friendly", "Sarcastic", "Formal", "Translator")
                            tonesList.forEach { tone ->
                                val selected = selectedTone == tone
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedTone = tone },
                                    label = { Text(tone) }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (keywordInput.isNotBlank() && templateInput.isNotBlank()) {
                                    viewModel.addNewRule(keywordInput, templateInput, isAiEnabled, selectedTone)
                                    keywordInput = ""
                                    templateInput = ""
                                    isAiEnabled = false
                                    selectedTone = "Support"
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsappGreen)
                        ) {
                            Text("Save Target", color = Color(0xFF111B21), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. BULK MESSAGING & BROADCAST CAMPAIGNS
// ==========================================
@Composable
fun BulkMessagingScreen(viewModel: ChatFlowViewModel) {
    val campaignsList by viewModel.campaigns.collectAsStateWithLifecycle()
    var showCreateCampaignDialog by remember { mutableStateOf(false) }

    var titleInput by remember { mutableStateOf("") }
    var templateInput by remember { mutableStateOf("") }
    var targetsInput by remember { mutableStateOf("4") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Broadcast Campaigns",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Administer multi-customer webhook messaging instantly",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Button(
                onClick = { showCreateCampaignDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = WhatsappGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF111B21))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Schedule Campaign", fontWeight = FontWeight.Bold, color = Color(0xFF111B21))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (campaignsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, sizeChange = 64.dp, tint = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No Campaign logs", fontWeight = FontWeight.Bold)
                    Text("Launch automated broadcast lists securely", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(campaignsList) { camp ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = camp.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Target Pool Size: ${camp.totalCount} Contacts",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (camp.status) {
                                                    "Completed" -> WhatsappGreen.copy(alpha = 0.15f)
                                                    "Sending" -> NeonBlueAccent.copy(alpha = 0.15f)
                                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = camp.status,
                                            color = when (camp.status) {
                                                "Completed" -> WhatsappGreen
                                                "Sending" -> NeonBlueAccent
                                                else -> Color.Gray
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(onClick = { viewModel.deleteCampaign(camp) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Broadcast Message Template:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = camp.messageTemplate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val progress = if (camp.totalCount > 0) camp.sentCount.toFloat() / camp.totalCount.toFloat() else 0f
                            LinearProgressIndicator(
                                progress = { progress },
                                color = WhatsappGreen,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Progress Transacted",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "${camp.sentCount} / ${camp.totalCount} sent",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WhatsappGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateCampaignDialog) {
        Dialog(onDismissRequest = { showCreateCampaignDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Build Broadcast Campaign",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Campaign Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = templateInput,
                        onValueChange = { templateInput = it },
                        label = { Text("Broadcast Message Body") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4
                    )

                    OutlinedTextField(
                        value = targetsInput,
                        onValueChange = { targetsInput = it },
                        label = { Text("Targets Size count limit") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showCreateCampaignDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (titleInput.isNotBlank() && templateInput.isNotBlank()) {
                                    val count = targetsInput.toIntOrNull() ?: 4
                                    viewModel.createAndScheduleCampaign(
                                        name = titleInput.trim(),
                                        template = templateInput.trim(),
                                        targetsCount = count,
                                        scheduleOffsetMinutes = 0
                                    )
                                    titleInput = ""
                                    templateInput = ""
                                    targetsInput = "4"
                                    showCreateCampaignDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsappGreen)
                        ) {
                            Text("Launch Blast", color = Color(0xFF111B21), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. TELEMETRY ANALYTICS SCREEN
// ==========================================
@Composable
fun AnalyticsScreen(viewModel: ChatFlowViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Telemetry Analytics Logs",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Continuous queue monitoring, transacted logs, and sentiment pulse gauges",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Weekly Transacted Message Volume",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        val gridLines = 4
                        for (i in 0..gridLines) {
                            val y = height * i / gridLines
                            drawLine(
                                color = Color.White.copy(alpha = 0.05f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        val points = listOf(
                            Offset(width * 0.1f, height * 0.75f),
                            Offset(width * 0.3f, height * 0.45f),
                            Offset(width * 0.5f, height * 0.85f),
                            Offset(width * 0.7f, height * 0.30f),
                            Offset(width * 0.9f, height * 0.15f)
                        )

                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (p in 1 until points.size) {
                                lineTo(points[p].x, points[p].y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = WhatsappGreen,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        points.forEach { point ->
                            drawCircle(
                                color = WhatsappTealDark,
                                radius = 6.dp.toPx(),
                                center = point
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3.dp.toPx(),
                                center = point
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    XAxisLabel("Mon")
                    XAxisLabel("Tue")
                    XAxisLabel("Wed")
                    XAxisLabel("Thu")
                    XAxisLabel("Fri (Today)")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("ChatFlow Performance Analysis", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                TelemetryRow("AI Agent Auto Accuracy", "94.2%", "Higher match thresholds active", WhatsappGreen)
                TelemetryRow("Message Delivery success rate", "99.8%", "Active session connection is stable", WhatsappGreen)
                TelemetryRow("Average Server Response Latency", "124 ms", "Firebase Supabase real-time sync speed", WhatsappGreen)
                TelemetryRow("Active Client Subscription status", viewModel.currentUserPlan, "Billed client workspace license", NeonBlueAccent)
            }
        }
    }
}

@Composable
fun XAxisLabel(text: String) {
    Text(text, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
}

@Composable
fun TelemetryRow(label: String, value: String, description: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(description, fontSize = 11.sp, color = Color.Gray)
        }
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = valueColor)
    }
}

// ==========================================
// 9. CLIENT CONTACTS SCREEN
// ==========================================
@Composable
fun ContactsScreen(viewModel: ChatFlowViewModel) {
    val contactsList by viewModel.contacts.collectAsStateWithLifecycle()
    var showAddContactDialog by remember { mutableStateOf(false) }

    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Broadcast Contact List",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Manage recipient pools, customized tags, and VIP client indicators",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Button(
                onClick = { showAddContactDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = WhatsappGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF111B21))
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Contact", fontWeight = FontWeight.Bold, color = Color(0xFF111B21))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (contactsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Person, contentDescription = null, sizeChange = 64.dp, tint = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Contacts Pool Empty", fontWeight = FontWeight.Bold)
                    Text("Build a contact lists using the top action trigger", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(contactsList) { contact ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(WhatsappTealSecondary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = contact.name.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(contact.phoneNumber, fontSize = 13.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        contact.tags.split(",").forEach { tag ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(WhatsappTealSecondary.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(tag.trim(), fontSize = 9.sp, color = WhatsappGreen, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            IconButton(onClick = { viewModel.removeContact(contact) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddContactDialog) {
        Dialog(onDismissRequest = { showAddContactDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Register Recipient",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("WhatsApp Phone Number (with Country Code)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    OutlinedTextField(
                        value = tagsInput,
                        onValueChange = { tagsInput = it },
                        label = { Text("Custom Tags (comma separated. e.g. Lead, VIP)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddContactDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (nameInput.isNotBlank() && phoneInput.isNotBlank()) {
                                    viewModel.addNewContact(nameInput.trim(), phoneInput.trim(), tagsInput.trim())
                                    nameInput = ""
                                    phoneInput = ""
                                    tagsInput = ""
                                    showAddContactDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsappGreen)
                        ) {
                            Text("Save Entry", color = Color(0xFF111B21), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 10. SYSTEM SETTINGS & SAAS SUBSCRIPTIONS
// ==========================================
@Composable
fun SettingsScreen(viewModel: ChatFlowViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Console & System Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Licensing configurations, applet styling, and database memory clearing",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text("Active Workspace License Plans", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SaaSLicenseCard(
                planTitle = "SaaS Professional",
                price = "\$49/mo",
                features = "Uncapped auto replies\n4 connected numbers\nFull Gemini integrations",
                isActive = viewModel.currentUserPlan == "SaaS Professional",
                modifier = Modifier.weight(1f),
                onSelectPlan = { viewModel.currentUserPlan = "SaaS Professional" }
            )

            SaaSLicenseCard(
                planTitle = "Enterprise Global",
                price = "\$149/mo",
                features = "Dedicated cloud nodes\nUnlimited integrations\nSLA uptime guarantee",
                isActive = viewModel.currentUserPlan == "Enterprise Global",
                modifier = Modifier.weight(1f),
                onSelectPlan = { viewModel.currentUserPlan = "Enterprise Global" }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("General Tuning", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Low-Light Dark Visual Theme", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Highly optimized for long support shifts", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = viewModel.isDarkTheme,
                        onCheckedChange = { viewModel.isDarkTheme = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = WhatsappGreen)
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Flush Conversation memory cache", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Red.copy(alpha = 0.8f))
                        Text("Completely wipes room chats locally", fontSize = 11.sp, color = Color.Gray)
                    }
                    Button(
                        onClick = { viewModel.clearHistory() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Flush Room DB", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SaaSLicenseCard(planTitle: String, price: String, features: String, isActive: Boolean, modifier: Modifier, onSelectPlan: () -> Unit) {
    val borderColor = if (isActive) WhatsappGreen else MaterialTheme.colorScheme.surfaceVariant
    val containerColor = if (isActive) WhatsappTealSecondary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier.clickable { onSelectPlan() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(planTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isActive) WhatsappGreen else MaterialTheme.colorScheme.onSurface)
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(WhatsappGreen)
                            .size(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, sizeChange = 10.dp, tint = Color(0xFF111B21))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(price, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text(features, fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp)
        }
    }
}

// ==========================================
// 11. HIGH PRIVILEGE ADMIN PANEL
// ==========================================
@Composable
fun AdminPanelScreen(viewModel: ChatFlowViewModel) {
    var testPromptInput by remember { mutableStateOf("How much do you charge?") }
    var testConsoleOutput by remember { mutableStateOf("Output response results will display here.") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "SaaS Admin Control Deck",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "High-privilege database tuning, active server queue status limits, and LLM diagnostic prompts",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Simulated SaaS Multi-Tenant Cloud Services", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                TelemetryRow("Virtual Server Status", "ACTIVE • ONLINE", "NodeJS Express server daemon running", WhatsappGreen)
                TelemetryRow("Message Broadcast Queue backlog size", "0 pending", "Cron job execution is synchronized", WhatsappGreen)
                TelemetryRow("Memory usage load", "180 MB / 512 MB", "Supabase PostgreSQL pooling active", WhatsappGreen)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Gemini LLM Prompt Sandbox", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Direct test harness prompt query to evaluate response speed and context accuracy", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

                OutlinedTextField(
                    value = testPromptInput,
                    onValueChange = { testPromptInput = it },
                    label = { Text("Diagnostic prompt message") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        scope.launch {
                            testConsoleOutput = "Contacting Gemini API endpoint..."
                            testConsoleOutput = AiManager.generateAutoReply(
                                incomingMessage = testPromptInput,
                                ruleKeyword = "Diagnostic",
                                template = "Standard fallback baseline pricing: Developer trial plan is completely free.",
                                tone = "Support"
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsappGreen),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Execute Prompt Probe", fontWeight = FontWeight.Bold, color = Color(0xFF111B21))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Sandbox Response Output Console:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                ) {
                    Text(
                        text = testConsoleOutput,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
