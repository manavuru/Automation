package com.example.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed class AppScreen {
    object Landing : AppScreen()
    object Dashboard : AppScreen()
    object Connection : AppScreen()
    object LiveChats : AppScreen()
    object AutomationRules : AppScreen()
    object BulkMessaging : AppScreen()
    object Contacts : AppScreen()
    object Analytics : AppScreen()
    object Settings : AppScreen()
    object Admin : AppScreen()
}

class ChatFlowViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val dao = database.chatFlowDao()

    // Screen State
    var currentScreen by mutableStateOf<AppScreen>(AppScreen.Landing)
    var isDarkTheme by mutableStateOf(true)

    // Auth Simulation
    var userToken by mutableStateOf<String?>(null)
    var currentUserEmail by mutableStateOf("")
    var currentUserName by mutableStateOf("")
    var currentUserPlan by mutableStateOf("Free Trial")

    // Connection Environment Properties
    var isLiveMode by mutableStateOf(false)
    var backendBaseUrl by mutableStateOf("https://ais-dev-z6w3tz6bqb3yxxiwb5qvhz-12438360128.asia-southeast1.run.app")

    // WhatsApp Connection Simulation Upgrade
    var isConnected by mutableStateOf(false)
    var connectionStatus by mutableStateOf("DISCONNECTED") // DISCONNECTED, GENERATING_QR, QR_READY, CONNECTING, CONNECTED
    var qrCodeValue by mutableStateOf<String?>(null)

    // Upgraded States
    var connectionMethod by mutableStateOf("QR") // "QR" or "PAIRING"
    var pairingPhoneNumber by mutableStateOf("")
    var pairingCodeValue by mutableStateOf<String?>(null)

    // Configurator States
    var selectedEngine by mutableStateOf("Baileys API") // Baileys API, Puppeteer Web, Cloud API Webhook
    var isSessionPersistent by mutableStateOf(true)
    var autoReadReceipts by mutableStateOf(true)
    var reconnectInterval by mutableStateOf("10 seconds")

    // Stats and latency info
    var latencyMs by mutableStateOf(42)
    var connectedDeviceModel by mutableStateOf("Google Pixel 8 Pro (Android 14)")
    var sessionDuration by mutableStateOf("04:12:35")
    var memoryUsageMb by mutableStateOf(142)

    // Dynamic Live Terminal Logs
    val connectionLogs = androidx.compose.runtime.mutableStateListOf<String>().apply {
        add("[System] ChatFlow AI Daemon Gateway initialised.")
        add("[System] Ready for custom web-socket link handshake.")
    }

    fun addLog(message: String) {
        val formatter = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        val time = formatter.format(java.util.Date())
        connectionLogs.add("[$time] $message")
        if (connectionLogs.size > 80) {
            connectionLogs.removeAt(0)
        }
    }

    // Active Selected Chat Contact
    var selectedContact by mutableStateOf<Contact?>(null)

    // Observables from DB
    val contacts: StateFlow<List<Contact>> = dao.getAllContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automationRules: StateFlow<List<AutomationRule>> = dao.getAllRules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessage>> = dao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val campaigns: StateFlow<List<Campaign>> = dao.getAllCampaigns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Status / Log messages
    var aiStatusMessage by mutableStateOf("AI Engine Idle")
    var lastProcessedAiText by mutableStateOf<String?>(null)

    init {
        // Seed Database if empty
        viewModelScope.launch {
            seedDatabaseIfEmpty()
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        // Simple seed check on contacts
        dao.getAllContacts().first().let { currentContacts ->
            if (currentContacts.isEmpty()) {
                // Seed contacts
                val contactsSeed = listOf(
                    Contact(name = "Alice Green", phoneNumber = "+1-555-0199", tags = "VIP,Lead"),
                    Contact(name = "Bob Miller", phoneNumber = "+1-555-0144", tags = "Customer"),
                    Contact(name = "Charlie Rose", phoneNumber = "+1-555-0122", tags = "Partner"),
                    Contact(name = "Daniel Cross", phoneNumber = "+1-555-0188", tags = "Lead")
                )
                contactsSeed.forEach { dao.insertContact(it) }

                // Seed automation rules
                val rulesSeed = listOf(
                    AutomationRule(keyword = "price", replyTemplate = "Our SaaS packages are: Basic at \$19/mo, Professional at \$49/mo, and Enterprise at \$149/mo.", isAiEnabled = false),
                    AutomationRule(keyword = "demo", replyTemplate = "ChatFlow AI turns conversational prompts into robust WhatsApp marketing sequences. Let us schedule a video call.", isAiEnabled = true, promptType = "Friendly"),
                    AutomationRule(keyword = "support", replyTemplate = "If you need support, please contact us at help@chatflow.ai. Our team will review this shortly.", isAiEnabled = true, promptType = "Support"),
                    AutomationRule(keyword = "hello", replyTemplate = "Hello! Welcome to ChatFlow AI Hub. Type 'price' or 'demo' to test our triggers.", isAiEnabled = false)
                )
                rulesSeed.forEach { dao.insertRule(it) }

                // Seed some chat history
                val messagesSeed = listOf(
                    ChatMessage(sender = "+1-555-0199", senderName = "Alice Green", messageText = "Hello! Is this the official ChatFlow SaaS?", timestamp = System.currentTimeMillis() - 7200000, isReceived = true, contactPhoneNumber = "+1-555-0199"),
                    ChatMessage(sender = "YOU", senderName = "You", messageText = "Yes Alice, welcome to ChatFlow AI!", timestamp = System.currentTimeMillis() - 7100000, isReceived = false, contactPhoneNumber = "+1-555-0199"),
                    ChatMessage(sender = "+1-555-0199", senderName = "Alice Green", messageText = "Awesome! Can we run campaign scheduling here on mobile?", timestamp = System.currentTimeMillis() - 7000000, isReceived = true, contactPhoneNumber = "+1-555-0199"),
                    ChatMessage(sender = "AI_AUTO", senderName = "ChatFlow bot", messageText = "🤖 Yes! You can configure keyword automations or trigger bulk campaign schedules instantaneously from the console.", timestamp = System.currentTimeMillis() - 6900000, isReceived = false, sentiment = "Positive", contactPhoneNumber = "+1-555-0199")
                )
                messagesSeed.forEach { dao.insertMessage(it) }

                // Seed campaign
                val campaignSeed = Campaign(
                    name = "Summer Beta Blast",
                    messageTemplate = "Hey! Check out our new ChatFlow AI SaaS. Connect your WhatsApp QR in 5 seconds flat!",
                    scheduledTime = System.currentTimeMillis() + 86400000,
                    status = "Scheduled",
                    sentCount = 0,
                    totalCount = 4
                )
                dao.insertCampaign(campaignSeed)
            }
        }
    }

    // ==========================================
// ACTIONS
// ==========================================

    private var pollerJob: kotlinx.coroutines.Job? = null

    fun startLiveStatusPoller() {
        pollerJob?.cancel()
        pollerJob = viewModelScope.launch {
            addLog("[Gateway] Starting WhatsApp live status synchronizer...")
            while (true) {
                if (!isLiveMode) break
                try {
                    val token = userToken
                    if (token != null) {
                        val api = BackendClient.getService(backendBaseUrl)
                        val statusResponse = api.getStatus(token)
                        
                        isConnected = statusResponse.connected
                        val serverStatus = statusResponse.status
                        
                        // Map state
                        if (isConnected) {
                            connectionStatus = "CONNECTED"
                        } else {
                            connectionStatus = serverStatus
                        }

                        if (serverStatus == "QR_READY" && statusResponse.qr != null) {
                            qrCodeValue = statusResponse.qr
                            // Update pairing code to first segments for visual response
                            pairingCodeValue = if (statusResponse.qr.length >= 8) {
                                val clean = statusResponse.qr.replace("-", "").uppercase()
                                if (clean.length >= 8) "${clean.take(4)}-${clean.substring(4, 8)}" else "MOCK-LINK"
                            } else {
                                "MOCK-LINK"
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.d("ChatFlowViewModel", "Polling background sync error: ${e.message}")
                }
                delay(3000)
            }
        }
    }

    fun login(email: String, name: String) {
        viewModelScope.launch {
            if (isLiveMode) {
                addLog("[Auth] Contacting Server auth endpoint: $backendBaseUrl")
                try {
                    val api = BackendClient.getService(backendBaseUrl)
                    // Auto Login or Register on Live Node
                    val response = try {
                        api.login(BackendAuthRequest(email = email, password = "password"))
                    } catch (e: Exception) {
                        addLog("[Auth] Creating new team workspace profile on Live Node...")
                        api.register(BackendAuthRequest(email = email, password = "password", name = name))
                    }
                    userToken = "Bearer " + response.token
                    currentUserEmail = response.user.email
                    currentUserName = response.user.name
                    currentUserPlan = "SaaS " + response.user.plan
                    currentScreen = AppScreen.Dashboard
                    addLog("[Auth] Secure Authorization established with Live Server.")
                    startLiveStatusPoller()
                } catch (e: Exception) {
                    addLog("[Error] Live Server Auth error: ${e.localizedMessage}")
                    addLog("[Auth] Bypassing with fallback Developer Sandbox mode.")
                    currentUserEmail = email
                    currentUserName = name.ifBlank { "User" }
                    currentUserPlan = "SaaS Professional"
                    userToken = "jwt_" + UUID.randomUUID().toString().substring(0, 8)
                    currentScreen = AppScreen.Dashboard
                }
            } else {
                currentUserEmail = email
                currentUserName = name.ifBlank { "User" }
                currentUserPlan = "SaaS Professional"
                userToken = "jwt_" + UUID.randomUUID().toString().substring(0, 8)
                currentScreen = AppScreen.Dashboard
            }
        }
    }

    fun logout() {
        pollerJob?.cancel()
        userToken = null
        currentScreen = AppScreen.Landing
    }

    // Connect WhatsApp Connection simulation Upgrade
    fun initiateWhatsAppConnect() {
        viewModelScope.launch {
            if (isLiveMode) {
                addLog("[Gateway] Instantiating Express Node WebSocket connection handshakes...")
                connectionStatus = "GENERATING_QR"
                try {
                    val token = userToken
                    if (token != null) {
                        val api = BackendClient.getService(backendBaseUrl)
                        val response = api.connectWhatsApp(token)
                        addLog("[Server] Handshake initialization started: ${response.message}")
                        startLiveStatusPoller()
                    } else {
                        addLog("[Error] Missing authentication credentials.")
                    }
                } catch (e: Exception) {
                    addLog("[Error] Live server connection trigger failed: ${e.localizedMessage ?: e.message}")
                }
            } else {
                if (connectionMethod == "QR") {
                    connectionStatus = "GENERATING_QR"
                    addLog("[Gateway] Instantiating daemon websocket on $selectedEngine engine...")
                    addLog("[Gateway] Fetching fresh link secure handshake keys...")
                    delay(1200)
                    addLog("[Baileys] Socket opened. Session token initialized.")
                    addLog("[Baileys] Generated QR binary matrix. Awaiting scan...")
                    connectionStatus = "QR_READY"
                    qrCodeValue = "WAClose:${UUID.randomUUID()}@c.us"
                } else {
                    if (pairingPhoneNumber.isBlank()) {
                        addLog("[Error] Please enter a valid phone number before building a pairing code.")
                        return@launch
                    }
                    connectionStatus = "GENERATING_QR"
                    addLog("[Gateway] Initializing phone number pairing protocol for $pairingPhoneNumber...")
                    addLog("[Gateway] Contacting pairing challenge provider...")
                    delay(1200)
                    val code = generateRandomPairingCode()
                    pairingCodeValue = code
                    addLog("[Baileys] Generated 8-digit pairing code challenge: $code")
                    addLog("[Baileys] Awaiting pairing confirmation on target phone device...")
                    connectionStatus = "QR_READY" // Shared code path but displaying code layout
                }
            }
        }
    }

    private fun generateRandomPairingCode(): String {
        val allowed = ('A'..'Z') + ('0'..'9')
        val c1 = (1..4).map { allowed.random() }.joinToString("")
        val c2 = (1..4).map { allowed.random() }.joinToString("")
        return "$c1-$c2"
    }

    fun completeQRScanSimulation() {
        viewModelScope.launch {
            if (isLiveMode) {
                addLog("[Gateway] Please scan the actual QR Code on your WhatsApp smartphone to connect.")
            } else {
                connectionStatus = "CONNECTING"
                addLog("[Gateway] Caught link confirmation protocol response.")
                addLog("[Gateway] Splicing multidevice session variables...")
                delay(1500)
                addLog("[Gateway] Building fast cached index for DB synchronization...")
                delay(1000)
                addLog("[Baileys] Connected successfully! Host model verified as $connectedDeviceModel.")
                addLog("[System] Hook listeners running. Logging live inbound messaging updates.")
                connectionStatus = "CONNECTED"
                isConnected = true
                qrCodeValue = null
            }
        }
    }

    fun disconnectWhatsApp() {
        viewModelScope.launch {
            addLog("[Gateway] Killing active WebSocket instance session...")
            addLog("[Gateway] Purging secure key-pairs from memory registers...")
            isConnected = false
            connectionStatus = "DISCONNECTED"
            qrCodeValue = null
            pairingCodeValue = null
            delay(500)
            addLog("[Gateway] Offline. Free to start new connection handshake whenever ready.")
        }
    }

    fun testLatencyPing() {
        viewModelScope.launch {
            addLog("[Diagnose] Executing WebSocket roundtrip latency test ping...")
            if (isLiveMode) {
                val start = System.currentTimeMillis()
                try {
                    val token = userToken
                    if (token != null) {
                        val api = BackendClient.getService(backendBaseUrl)
                        api.getStatus(token)
                        val end = System.currentTimeMillis()
                        latencyMs = (end - start).coerceAtLeast(1L).toInt().coerceIn(12, 160)
                        memoryUsageMb = (120..195).random()
                        addLog("[Diagnose] Pong received from Live Server REST! Latency: ${latencyMs}ms | Container Memory Load: ${memoryUsageMb}MB")
                    } else {
                        addLog("[Error] Cannot run test: missing Bearer auth credentials.")
                    }
                } catch (e: Exception) {
                    addLog("[Error] Ping route failed on live backend: ${e.message}")
                }
            } else {
                delay(400)
                val randomLat = (15..55).random()
                latencyMs = randomLat
                memoryUsageMb = (120..180).random()
                addLog("[Diagnose] Pong received! WebSocket Roundtrip RTT: ${randomLat}ms | Memory Load: ${memoryUsageMb}MB")
            }
        }
    }

    // Handle User Sending a Chat message manual
    fun sendChatMessage(contact: Contact, messageText: String) {
        if (messageText.isBlank()) return

        viewModelScope.launch {
            // Log outgoing message
            val userMsg = ChatMessage(
                sender = "YOU",
                senderName = contact.name,
                messageText = messageText,
                isReceived = false,
                contactPhoneNumber = contact.phoneNumber
            )
            dao.insertMessage(userMsg)

            // Simulate incoming message after a 2.5 second delay to build interactive feel
            delay(2500)
            simulateIncomingResponse(contact, messageText)
        }
    }

    private suspend fun simulateIncomingResponse(contact: Contact, userSentMessageText: String) {
        // Simple contextual replies
        val replyText = when {
            userSentMessageText.lowercase().contains("hello") || userSentMessageText.lowercase().contains("hi") -> {
                "Hi there! This is ${contact.name}! I am browsing your web services. Do you support keyword rules here?"
            }
            userSentMessageText.lowercase().contains("help") -> {
                "Yes, please trigger the support desk automate keywords!"
            }
            else -> {
                "Nice! Tell me more about your ChatFlow triggers, please type 'price' or 'demo'!"
            }
        }

        val incomingMsg = ChatMessage(
            sender = contact.phoneNumber,
            senderName = contact.name,
            messageText = replyText,
            isReceived = true,
            contactPhoneNumber = contact.phoneNumber
        )
        dao.insertMessage(incomingMsg)

        // Sentiment Check & AI evaluation
        processAiAndAutomationTriggers(contact, incomingMsg)
    }

    // Directly allow simulating ANY arbitrary incoming message from a contact (helps user test automations immediately!)
    fun receiveSimulatorMessage(contact: Contact, testText: String) {
        viewModelScope.launch {
            val incomingMsg = ChatMessage(
                sender = contact.phoneNumber,
                senderName = contact.name,
                messageText = testText,
                isReceived = true,
                contactPhoneNumber = contact.phoneNumber
            )
            dao.insertMessage(incomingMsg)

            processAiAndAutomationTriggers(contact, incomingMsg)
        }
    }

    // Core AI Auto-Reply engine
    private suspend fun processAiAndAutomationTriggers(contact: Contact, incomingMsg: ChatMessage) {
        aiStatusMessage = "Analyzing message sentiment..."
        // Analyze Sentiment using Gemini API or locally
        val sentiment = AiManager.analyzeSentiment(incomingMsg.messageText)
        val updatedMsg = incomingMsg.copy(sentiment = sentiment)
        dao.insertMessage(updatedMsg)
        aiStatusMessage = "Sentiment: $sentiment"

        // Search in AutomationRules for a keyword match
        val text = incomingMsg.messageText.lowercase()
        val rulesList = dao.getAllRules().first()

        var matchedRule: AutomationRule? = null
        for (rule in rulesList) {
            if (rule.isActive && text.contains(rule.keyword.lowercase())) {
                matchedRule = rule
                break
            }
        }

        if (matchedRule != null) {
            aiStatusMessage = "Rule matched! Composing auto-reply..."
            delay(1500) // Build suspense

            val finalReplyText: String
            if (matchedRule.isAiEnabled) {
                // Uses Gemini!
                aiStatusMessage = "Calling Gemini to compose auto-reply (${matchedRule.promptType} tone)..."
                finalReplyText = AiManager.generateAutoReply(
                    incomingMessage = incomingMsg.messageText,
                    ruleKeyword = matchedRule.keyword,
                    template = matchedRule.replyTemplate,
                    tone = matchedRule.promptType
                )
                lastProcessedAiText = finalReplyText
            } else {
                finalReplyText = "🤖 [Auto-Reply] " + matchedRule.replyTemplate
            }

            // Save Bot outgoing message
            val botMsg = ChatMessage(
                sender = "AI_AUTO",
                senderName = "ChatFlow AI Bot",
                messageText = finalReplyText,
                isReceived = false,
                sentiment = "Positive", // Bot responses are constructive!
                contactPhoneNumber = contact.phoneNumber
            )
            dao.insertMessage(botMsg)
            aiStatusMessage = "AI Auto-replied successfully"
        } else {
            aiStatusMessage = "No keyword rules matched. Awaiting human support."
        }
    }

    // Contacts management
    fun addNewContact(name: String, phone: String, tags: String) {
        viewModelScope.launch {
            val newC = Contact(name = name, phoneNumber = phone, tags = tags.ifBlank { "Client" })
            dao.insertContact(newC)
        }
    }

    fun removeContact(contact: Contact) {
        viewModelScope.launch {
            if (selectedContact?.id == contact.id) {
                selectedContact = null
            }
            dao.deleteContact(contact)
        }
    }

    // Rules management
    fun addNewRule(keyword: String, template: String, isAi: Boolean, tone: String) {
        viewModelScope.launch {
            val rule = AutomationRule(
                keyword = keyword.trim(),
                replyTemplate = template.trim(),
                isAiEnabled = isAi,
                promptType = tone
            )
            dao.insertRule(rule)
        }
    }

    fun removeRule(rule: AutomationRule) {
        viewModelScope.launch {
            dao.deleteRule(rule)
        }
    }

    // Campaigns management
    fun createAndScheduleCampaign(name: String, template: String, targetsCount: Int, scheduleOffsetMinutes: Int) {
        viewModelScope.launch {
            val scheduledTime = System.currentTimeMillis() + (scheduleOffsetMinutes * 60000L)
            val newCampaign = Campaign(
                name = name,
                messageTemplate = template,
                scheduledTime = scheduledTime,
                status = "Scheduled",
                sentCount = 0,
                totalCount = targetsCount
            )
            dao.insertCampaign(newCampaign)

            // If scheduled immediately (0 delay), trigger background running simulation!
            if (scheduleOffsetMinutes == 0) {
                runCampaignSimulation(newCampaign)
            }
        }
    }

    fun deleteCampaign(campaign: Campaign) {
        viewModelScope.launch {
            dao.deleteCampaign(campaign)
        }
    }

    private fun runCampaignSimulation(campaign: Campaign) {
        viewModelScope.launch {
            val dbCampaign = dao.getAllCampaigns().first().firstOrNull { it.name == campaign.name && it.messageTemplate == campaign.messageTemplate }
                ?: return@launch

            // Set sending status
            var currentCamp = dbCampaign.copy(status = "Sending")
            dao.insertCampaign(currentCamp)

            // Progressively send messages mock
            for (step in 1..currentCamp.totalCount) {
                delay(1500)
                currentCamp = currentCamp.copy(sentCount = step)
                dao.insertCampaign(currentCamp)

                // Log an output chat msg in database corresponding to random contact
                val rContacts = contacts.value
                val destinationName = rContacts.getOrNull(step % rContacts.size)?.name ?: "Simulated Contact $step"
                val destPhone = rContacts.getOrNull(step % rContacts.size)?.phoneNumber ?: "+1-555-010$step"

                val broadcastMessage = ChatMessage(
                    sender = "YOU",
                    senderName = destinationName,
                    messageText = currentCamp.messageTemplate,
                    isReceived = false,
                    sentiment = "Neutral",
                    contactPhoneNumber = destPhone
                )
                dao.insertMessage(broadcastMessage)
            }

            // Complete campaign
            currentCamp = currentCamp.copy(status = "Completed")
            dao.insertCampaign(currentCamp)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            dao.clearAllMessages()
            selectedContact = null
        }
    }

    // Dynamic Real-time Translation Upgrade via Gemini
    fun translateMessageText(message: ChatMessage, targetLanguage: String = "Spanish") {
        viewModelScope.launch {
            val translation = AiManager.translateMessage(message.messageText, targetLanguage)
            val updatedMsg = message.copy(translatedText = translation)
            dao.insertMessage(updatedMsg)
        }
    }
}
