# Agenda de Embarques (KSA)

Aplicativo Android em **Kotlin + Jetpack Compose + Material 3** para cadastrar passageiros/viagens e acompanhar embarques com alertas automáticos.

## Como rodar no Android Studio
1. Abra a pasta do projeto no Android Studio.
2. Aguarde o Gradle Sync.
3. Execute em um emulador/dispositivo Android (minSdk 26).
4. No Android 13+, conceda permissão de notificação quando solicitado.

> Observação: o projeto usa WorkManager para verificações diárias de embarques.

## HomeScreen
A tela inicial foi construída com:
- fundo escuro com efeito de blur e vinheta;
- logo KSA no topo e textos institucionais;
- três botões “pílula” com borda vermelha e texto branco:
  - **ORÇAMENTOS**: abre a área principal (Cadastro + Próximos embarques);
  - **PARCERIAS**: abre placeholder “em breve”;
  - **INSTAGRAM**: abre link externo definido em `INSTAGRAM_URL`.

## DatePicker BR (dd/MM/yyyy)
No cadastro:
- o usuário **não digita** datas manualmente;
- os campos abrem `DatePickerDialog` (Material 3);
- datas exibidas no padrão BR (`dd/MM/yyyy`);
- persistência em formato ISO (`yyyy-MM-dd`) para consistência e ordenação.

Validações implementadas:
- todos os campos obrigatórios;
- data de retorno não pode ser anterior ao embarque;
- erros exibidos de forma clara na tela.

## Notificações (WorkManager)
- `EmbarkNotificationWorker` verifica diariamente viagens com embarque em **exatos 5 dias**.
- `NotificationScheduler` agenda worker periódico com primeiro disparo alinhado para 9h.
- Se houver múltiplos embarques no mesmo dia, gera uma notificação única com quantidade e nomes.
- Criação de `NotificationChannel` para Android 8+.
- Em Android 13+, se a permissão for negada, o app mostra aviso amigável.

