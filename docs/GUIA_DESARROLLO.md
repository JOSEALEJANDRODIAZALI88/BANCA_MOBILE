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
.\gradlew.bat clean :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
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

El saldo, las facturas, los movimientos y las opciones de tarjeta se guardan en SQLite mediante Room. Para volver al estado inicial:

```powershell
adb shell pm clear com.nexo.banca.demo
```

Tambien puedes ir a **Ajustes > Aplicaciones > Nexo Demo > Almacenamiento > Borrar datos**. Al abrirla otra vez, la base de datos se crea de nuevo.

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
- Las reglas de credenciales, cuentas, transferencias y ahorro tienen pruebas unitarias.
- `lintDebug` comprueba compatibilidad desde Android API 24.
- Las capturas de pantalla y copias de seguridad permanecen desactivadas para la demostracion.
