# Guia de desarrollo y ejecucion de Nexo

Esta guia explica como preparar, ejecutar, verificar y diagnosticar la aplicacion Android. Nexo es una demostracion local: no se conecta a bancos ni procesa dinero real.

## 1. Ubicacion correcta del proyecto

La carpeta que Android Studio debe abrir es:

```text
banca mobile/banca_movile/android-app
```

No abras solamente `app` ni la carpeta exterior `banca mobile`, porque Android Studio no encontrara correctamente `settings.gradle.kts` y el Gradle Wrapper.

## 2. Requisitos

- Android Studio actualizado.
- Android SDK Platform 37.
- Android SDK Build-Tools instaladas desde SDK Manager.
- JDK 17 o superior; es preferible el JDK integrado de Android Studio.
- Android 7.0 (API 24) o superior para ejecutar la aplicacion.
- Conexion a Internet durante la primera sincronizacion para descargar dependencias.

En Android Studio revisa **Settings > Build, Execution, Deployment > Build Tools > Gradle** y selecciona el JDK integrado.

## 3. Comprobar el entorno

Abre PowerShell en `banca_movile/android-app` y ejecuta:

```powershell
java -version
.\gradlew.bat --version
.\gradlew.bat projects
```

El ultimo comando debe mostrar el proyecto raiz `NexoBanca` y el modulo `:app`.

## 4. Ejecutar desde Android Studio

1. Abre `banca_movile/android-app`.
2. Espera a que desaparezca el mensaje de sincronizacion de Gradle.
3. Si falta el SDK, abre **Tools > SDK Manager**, instala Android API 37 y sincroniza nuevamente.
4. Crea un emulador desde **Tools > Device Manager** o conecta un telefono con depuracion USB.
5. Selecciona el dispositivo y la configuracion `app`.
6. Pulsa **Run**.

Credenciales de demostracion:

```text
Usuario: demo@nexo.com
Clave:   Nexo2026
```

Si el emulador o dispositivo tiene biometria registrada, el dialogo oficial de Android aparece automaticamente al abrir el acceso. Solo aparece una vez por entrada; al cancelarlo permanecen disponibles **Entrar con biometria** y las credenciales demo. Se desactiva desde **Mas > Config. > Solicitar al iniciar**. Este mecanismo solo desbloquea la demostracion local; no sustituye autenticacion bancaria ni cifra la base Room.

Desde el engranaje superior derecho de Inicio o **Mas > Config.** puedes elegir el tema **Sistema**, **Claro** u **Oscuro** y decidir si el saldo debe mostrarse al entrar a Inicio. Estas preferencias se guardan en DataStore y no requieren una migracion Room. Configuracion tambien enlaza Perfil y Tarjetas, muestra la version desde BuildConfig y permite restablecer la demo con confirmacion.

Los botones **Servicios**, **Creditos** y **Calculadora** de Mas abren solo su contenido, sin repetir las tres pestañas. **Pagos** mantiene las tres como vista general. El acceso Servicios de Inicio tambien es directo. El historial de navegacion permite regresar desde Tarjetas a Perfil y despues a Configuracion.

## 5. Compilar y probar desde PowerShell

Compilacion de depuracion:

```powershell
.\gradlew.bat :app:assembleDebug
```

Pruebas unitarias:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Analisis estatico:

```powershell
.\gradlew.bat :app:lintDebug
```

Verificacion completa recomendada antes de entregar:

```powershell
.\gradlew.bat clean :app:testDebugUnitTest :app:lintDebug :app:generarApkDemo
```

## 6. Generar el APK de entrega

Ejecuta:

```powershell
.\gradlew.bat :app:generarApkDemo
```

El script compila el APK de depuracion y lo copia como:

```text
banca_movile/distribucion/Nexo-Demo.apk
```

Este APK usa una firma de depuracion. Sirve para clases y demostraciones, pero no debe publicarse como una aplicacion bancaria de produccion.

### Regenerar los manuales PDF

Las fuentes editables estan en `docs/src`. Desde la raiz del repositorio ejecuta:

```powershell
.\banca_movile\scripts\generar-documentos.ps1
```

El script usa Microsoft Edge en modo headless y reemplaza los cuatro PDF de `docs`. Revisa los PDF generados antes de entregarlos.

El documento [Adopcion de apertura](ADOPCION_DE_APERTURA.pdf) integra el acta de apertura y el plan de adopcion de todo el proyecto: objetivos, alcance, arquitectura, roles por asignar, entregables, puesta en marcha y criterios de aceptacion. Su fuente editable es `docs/src/ADOPCION_DE_APERTURA.html`. Las fechas, responsables y aprobaciones deben completarse por el equipo; el documento no declara una aprobacion ni una validacion ya realizada.

## 7. Instalar con ADB

Comprueba primero que Android reconoce el dispositivo:

```powershell
adb devices
```

Instala o actualiza la aplicacion:

```powershell
adb install -r ".\app\build\outputs\apk\debug\app-debug.apk"
```

Abrela desde la terminal:

```powershell
adb shell am start -n com.nexo.banca.demo/com.nexo.banca.MainActivity
```

## 8. Restablecer datos ficticios

El saldo, las facturas, los movimientos y las opciones de tarjeta se guardan en SQLite mediante Room. La biometria inicial, el tema y la visibilidad inicial del saldo se guardan por separado mediante DataStore. Para volver todo al estado inicial:

Usa **Configuracion > Restablecer demostracion** y confirma. El repositorio elimina y vuelve a sembrar los datos bancarios en una transaccion Room; despues DataStore recupera sus valores predeterminados. Tambien puedes borrar los datos desde Android:

```powershell
adb shell pm clear com.nexo.banca.demo
```

Tambien puedes ir a **Ajustes > Aplicaciones > Nexo Demo > Almacenamiento > Borrar datos**. Al abrirla otra vez, la base de datos se crea de nuevo.

Haz este restablecimiento despues de cambiar `domain/DemoData.kt` o los datos sembrados; Room conserva instalaciones anteriores y no reemplaza una cuenta existente.

## 9. Solucion de problemas

### `SDK location not found`

Abre el proyecto con Android Studio para que genere `local.properties`, o crea ese archivo dentro de `android-app` con la ruta local del SDK. No publiques `local.properties` porque contiene una ruta especifica de tu equipo.

### `Android resource linking failed` o falta una plataforma

Instala Android SDK Platform 37 desde SDK Manager, sincroniza Gradle y ejecuta:

```powershell
.\gradlew.bat clean :app:assembleDebug
```

### Version de Java incompatible

Selecciona el JDK integrado de Android Studio. Comprueba la seleccion con `.\gradlew.bat --version`; no basta con revisar solamente `java -version` si Android Studio usa otro JDK.

### Gradle no descarga dependencias

Verifica Internet y desactiva temporalmente el modo **Offline work** de Gradle. Si existe un proxy institucional, configuralo en Android Studio sin guardar claves dentro del repositorio.

### El dispositivo no aparece

Activa opciones de desarrollador y depuracion USB, acepta la clave RSA en el telefono y vuelve a ejecutar `adb devices`. En Windows puede ser necesario instalar el controlador USB del fabricante.

### La camara no abre para leer QR

Concede el permiso de camara a Nexo Demo. La app tambien permite escribir manualmente el destinatario, la cuenta y el valor.

### La instalacion falla por una firma diferente

Desinstala la version anterior y vuelve a instalar. Esto elimina los datos locales:

```powershell
adb uninstall com.nexo.banca.demo
adb install ".\app\build\outputs\apk\debug\app-debug.apk"
```

## 10. Controles de calidad incorporados

- Las operaciones de saldo se realizan dentro de transacciones Room.
- El boton de transferencia se bloquea mientras una operacion esta en curso para evitar duplicados.
- Las cuentas QR con caracteres no numericos se rechazan.
- Las reglas de credenciales, cuentas, transferencias, ahorro, tipos de pago y calculo de prestamos tienen pruebas unitarias.
- Pagos permite alternar entre servicios y creditos; ambos se guardan como movimientos atomicos.
- El prompt biometrico usa AndroidX Biometric, espera a que DataStore cargue y se abre una sola vez por entrada cuando la preferencia esta activa.
- Los temas Sistema, Claro y Oscuro usan colores semanticos para mantener contraste en toda la interfaz glass.
- Los accesos especificos de pagos no muestran el selector general. Revisa Servicios, Creditos y Calculadora desde Mas y el regreso Configuracion > Perfil > Tarjetas en un dispositivo.
- Restablecer requiere confirmacion; comprueba que Cancelar conserve las operaciones y que confirmar restaure datos bancarios y preferencias.
- `lintDebug` comprueba compatibilidad desde Android API 24.
- Las capturas de pantalla y copias de seguridad permanecen desactivadas para la demostracion.
