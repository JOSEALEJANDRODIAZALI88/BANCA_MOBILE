# Nexo Banca Demo

Aplicacion educativa de banca movil para Android con Kotlin, Jetpack Compose, SQLite/Room y transferencias mediante QR.

> No se conecta a un banco ni mueve dinero real. Todos los datos y comprobantes son ficticios.

## Distribucion

```text
banca mobile/
|-- banca_movile/
|   `-- android-app/           Proyecto que se abre en Android Studio
|       |-- app/               Codigo y recursos Android
|       |-- gradle/            Configuracion de compilacion
|       `-- gradlew.bat        Generador del APK en Windows
|-- docs/                      Manuales finales en PDF
`-- README.md
```

Abre **`banca_movile/android-app`** en Android Studio.

## Requisitos

- Android Studio con Android SDK 37 instalado.
- JDK 17 o superior. Se recomienda usar el JDK incluido con Android Studio.
- Windows 10/11 para ejecutar los comandos de esta guia. El proyecto tambien puede compilarse en macOS o Linux mediante `./gradlew`.
- Un emulador o dispositivo con Android 7.0 (API 24) o superior.

No es necesario instalar Gradle globalmente: el proyecto incluye Gradle Wrapper.

## Inicio rapido

1. Abre Android Studio.
2. Selecciona **Open** y abre `banca_movile/android-app`.
3. Espera a que finalice la sincronizacion de Gradle.
4. Instala el SDK 37 si Android Studio lo solicita.
5. Selecciona un emulador o conecta un dispositivo con depuracion USB.
6. Ejecuta la configuracion `app`.

Consulta `docs/GUIA_DESARROLLO.md` para instrucciones detalladas y solucion de problemas.

## Manuales

- `docs/GUIA_INSTALACION_APK.pdf`: instalar el APK sin conocimientos de programacion.
- `docs/MANUAL_USUARIO_NEXO.pdf`: utilizar la aplicacion, SQLite y los codigos QR.
- `docs/MANUAL_APK_EJECUCION_GITHUB.pdf`: ejecutar el proyecto, generar el APK y publicarlo en GitHub.
- `docs/GUIA_DESARROLLO.md`: preparar el entorno, compilar, probar, instalar y diagnosticar errores.

## Generar el APK rapidamente

Desde PowerShell:

```powershell
cd "banca_movile\android-app"
.\gradlew.bat :app:generarApkDemo
```

Resultado:

```text
banca_movile/distribucion/Nexo-Demo.apk
```

Entrega `Nexo-Demo.apk` junto con `docs/GUIA_INSTALACION_APK.pdf`.

## Verificar el proyecto

Desde `banca_movile/android-app` ejecuta:

```powershell
.\gradlew.bat clean :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

La compilacion es correcta cuando termina con `BUILD SUCCESSFUL`. El APK normal queda en `app/build/outputs/apk/debug/app-debug.apk`.

Para instalarlo con ADB:

```powershell
adb devices
adb install -r ".\app\build\outputs\apk\debug\app-debug.apk"
adb shell am start -n com.nexo.banca.demo/com.nexo.banca.MainActivity
```

## Credenciales

```text
Usuario: demo@nexo.com
Clave:   Nexo2026
```

## Funciones

- Base SQLite local para saldo, movimientos, facturas, ahorro y tarjeta.
- Transferencias y pagos simulados con actualizacion atomica.
- Generacion, lectura y validacion de QR Nexo.
- Persistencia despues de cerrar la aplicacion.
- Interfaz de alto contraste en verde petroleo, menta y coral.
- Capturas y copias de seguridad desactivadas.

## APK

El APK de depuracion es para demostraciones y clases. Una aplicacion bancaria real requiere backend protegido, autenticacion multifactor, cifrado, auditoria, antifraude y cumplimiento normativo.

## Restablecer la demostracion

Para recuperar el saldo y movimientos iniciales, elimina los datos de **Nexo Demo** desde los ajustes de Android o ejecuta:

```powershell
adb shell pm clear com.nexo.banca.demo
```

Al volver a abrir la aplicacion, Room creara nuevamente los datos ficticios.
