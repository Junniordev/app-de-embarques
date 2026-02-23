package com.ksa.agendaembarques

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ksa.agendaembarques.data.Trip
import com.ksa.agendaembarques.data.TripRepository
import com.ksa.agendaembarques.notifications.NotificationScheduler
import com.ksa.agendaembarques.util.INSTAGRAM_URL
import com.ksa.agendaembarques.util.isoToBr
import com.ksa.agendaembarques.util.millisToIso
import com.ksa.agendaembarques.util.parseIso
import com.ksa.agendaembarques.util.today
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationScheduler.scheduleDaily(this)
        setContent { AgendaApp() }
    }
}

@Composable
fun AgendaApp() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(onOpenMain = { nav.navigate("main") }, onOpenPartnerships = { nav.navigate("partnerships") }) }
        composable("partnerships") { PlaceholderScreen(onBack = { nav.popBackStack() }) }
        composable("main") { MainTabsScreen() }
    }
}

@Composable
fun HomeScreen(onOpenMain: () -> Unit, onOpenPartnerships: () -> Unit) {
    val context = LocalContext.current
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_sunset_blur),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().blur(14.dp),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(Color(0xB3000000))
                    drawRect(Brush.radialGradient(listOf(Color.Transparent, Color(0x99000000))))
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ksa_logo),
                contentDescription = "Logo KSA",
                tint = Color.Unspecified,
                modifier = Modifier.size(120.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("VIAGENS E TURISMO", color = Color.White, fontWeight = FontWeight.Bold)
            Text("DESDE 2016", color = Color.White)
            Text(
                "REALIZANDO SONHOS, CELEBRANDO JORNADAS.",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )
            HomePillButton("ORÇAMENTOS", Icons.Default.AirplanemodeActive, onOpenMain)
            Spacer(Modifier.height(12.dp))
            HomePillButton("PARCERIAS", Icons.Default.Diamond, onOpenPartnerships)
            Spacer(Modifier.height(12.dp))
            HomePillButton("INSTAGRAM", Icons.Default.CameraAlt) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(INSTAGRAM_URL)))
            }
        }
    }
}

@Composable
private fun HomePillButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFB71C1C)),
        shape = RoundedCornerShape(50),
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text, fontWeight = FontWeight.Bold)
            Icon(icon, contentDescription = null)
        }
    }
}

@Composable
fun PlaceholderScreen(onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color(0xFF121212)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Parcerias em breve", color = Color.White)
            TextButton(onClick = onBack) { Text("Voltar") }
        }
    }
}

@Composable
fun MainTabsScreen() {
    val context = LocalContext.current
    val repo = remember { TripRepository(context) }
    val trips = remember { mutableStateListOf<Trip>().apply { addAll(repo.loadTrips()) } }
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    RequestNotificationPermissionIfNeeded(snackbar)

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                listOf("Cadastrar", "Próximos embarques").forEachIndexed { i, title ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title) })
                }
            }
            if (selectedTab == 0) {
                CadastroTab(
                    onSave = { trip ->
                        trips.add(trip)
                        repo.saveTrips(trips)
                        NotificationScheduler.scheduleDaily(context)
                        scope.launch { snackbar.showSnackbar("Cadastro salvo com sucesso") }
                    }
                )
            } else {
                ProximosEmbarquesTab(trips)
            }
        }
    }
}

@Composable
private fun RequestNotificationPermissionIfNeeded(snackbar: SnackbarHostState) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                scope.launch { snackbar.showSnackbar("Sem permissão de notificação. Você pode habilitar depois nas configurações.") }
            }
        }
        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroTab(onSave: (Trip) -> Unit) {
    var cliente by remember { mutableStateOf("") }
    var passageiro by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var idPassageiro by remember { mutableStateOf("") }
    var embarqueIso by remember { mutableStateOf<String?>(null) }
    var retornoIso by remember { mutableStateOf<String?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var showEmbarquePicker by remember { mutableStateOf(false) }
    var showRetornoPicker by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(value = cliente, onValueChange = { cliente = it }, label = { Text("Cliente responsável") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = passageiro, onValueChange = { passageiro = it }, label = { Text("Nome do passageiro principal") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = cpf, onValueChange = { cpf = it }, label = { Text("CPF") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = idPassageiro, onValueChange = { idPassageiro = it }, label = { Text("Número do ID do passageiro") }, modifier = Modifier.fillMaxWidth())

        DateField("Data de embarque", embarqueIso?.let(::isoToBr) ?: "Selecionar", onClick = { showEmbarquePicker = true })
        DateField("Data de retorno", retornoIso?.let(::isoToBr) ?: "Selecionar", onClick = { showRetornoPicker = true })

        erro?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        OutlinedButton(onClick = {
            erro = when {
                listOf(cliente, passageiro, cpf, idPassageiro).any { it.isBlank() } || embarqueIso == null || retornoIso == null -> "Preencha todos os campos obrigatórios."
                parseIso(retornoIso!!) < parseIso(embarqueIso!!) -> "A data de retorno não pode ser anterior ao embarque."
                else -> null
            }
            if (erro == null) {
                onSave(Trip(clienteResponsavel = cliente, passageiroPrincipal = passageiro, cpf = cpf, idPassageiro = idPassageiro, dataEmbarqueIso = embarqueIso!!, dataRetornoIso = retornoIso!!))
                cliente = ""; passageiro = ""; cpf = ""; idPassageiro = ""; embarqueIso = null; retornoIso = null
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Salvar cadastro")
        }
    }

    if (showEmbarquePicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(onDismissRequest = { showEmbarquePicker = false }, confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { embarqueIso = millisToIso(it) }
                showEmbarquePicker = false
            }) { Text("OK") }
        }, dismissButton = { TextButton(onClick = { showEmbarquePicker = false }) { Text("Cancelar") } }) {
            DatePicker(state = state)
        }
    }

    if (showRetornoPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(onDismissRequest = { showRetornoPicker = false }, confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { retornoIso = millisToIso(it) }
                showRetornoPicker = false
            }) { Text("OK") }
        }, dismissButton = { TextButton(onClick = { showRetornoPicker = false }) { Text("Cancelar") } }) {
            DatePicker(state = state)
        }
    }
}

@Composable
fun DateField(label: String, value: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, fontSize = 16.sp)
        }
    }
}

@Composable
fun ProximosEmbarquesTab(trips: List<Trip>) {
    val month = remember { YearMonth.now() }
    val days = remember(month) {
        val first = month.atDay(1)
        val leading = ((first.dayOfWeek.value - DayOfWeek.MONDAY.value) + 7) % 7
        buildList {
            repeat(leading) { add(null) }
            (1..month.lengthOfMonth()).forEach { add(month.atDay(it)) }
        }
    }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("${month.month.name.lowercase().replaceFirstChar { it.titlecase() }}/${month.year}", fontWeight = FontWeight.Bold)
        LazyVerticalGrid(columns = GridCells.Fixed(7), contentPadding = PaddingValues(2.dp), modifier = Modifier.height(280.dp)) {
            items(days) { date ->
                val tripDay = date?.let { d -> trips.filter { parseIso(it.dataEmbarqueIso) == d } } ?: emptyList()
                val bg = when {
                    date == null -> Color.Transparent
                    tripDay.isEmpty() -> Color(0xFFF1F1F1)
                    !date.isAfter(today().plusDays(5)) && !date.isBefore(today()) -> Color(0xFFB3261E)
                    else -> Color(0xFF2E7D32)
                }
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bg)
                        .clickable(enabled = date != null && tripDay.isNotEmpty()) { selectedDate = date }
                        .height(36.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) { Text(text = date?.dayOfMonth?.toString() ?: "", color = if (bg == Color(0xFFF1F1F1)) Color.Black else Color.White) }
            }
        }

        selectedDate?.let { d ->
            val selectedTrips = trips.filter { parseIso(it.dataEmbarqueIso) == d }
            selectedTrips.forEach {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("ID passageiro: ${it.idPassageiro}")
                        Text("Nome completo: ${it.passageiroPrincipal}")
                        Text("CPF: ${it.cpf}")
                        Text("Embarque: ${isoToBr(it.dataEmbarqueIso)}")
                        Text("Retorno: ${isoToBr(it.dataRetornoIso)}")
                    }
                }
            }
        }
    }
}
