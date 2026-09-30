# Comtax

App Android de contabilidad personal por **sobres**: registra ingresos y gastos, reparte el dinero de cada cuenta en sobres y consulta cuánto queda en cada uno. Funciona 100 % en local, protegida con PIN y sin declarar permiso de Internet.

## Funcionalidades

- **Cuentas con color propio** y saldo por cuenta.
- **Sobres por cuenta** (activos o archivados), con saldo individual y posibilidad de **fusionar** sobres.
- **Ingresos y gastos** con flujo guiado: cuenta → formulario → confirmación. Los movimientos se pueden editar, anular y marcar como cuadrados.
- **Balance en pasos:** cuentas → sobres + historial de movimientos → detalle del movimiento.
- **Aviso de saldo negativo** y marca de *pendiente de cuadre* para movimientos por revisar.
- **Bloqueo por PIN:** el PIN se guarda con PBKDF2-HMAC-SHA256 (120 000 iteraciones y sal aleatoria) y la app se bloquea tras 2 minutos en segundo plano.
- **Exportación a CSV** (separador `;`, fechas `dd/MM/yyyy`) para abrir en una hoja de cálculo.

Los importes se almacenan en **céntimos** (`Long`) para evitar errores de coma flotante.

## Tecnologías

| Área | Stack |
|---|---|
| Lenguaje | Kotlin (JVM 17) |
| UI | Jetpack Compose, Material 3, Navigation Compose (rutas tipadas con `kotlinx.serialization`) |
| Persistencia | Room (esquema versión 2, migraciones y esquemas exportados en `app/schemas`) |
| Inyección de dependencias | Hilt (con KSP) |
| Seguridad | `androidx.security:security-crypto`, PBKDF2 |
| Asincronía | Coroutines y Flow |
| Tests | JUnit, Robolectric, `kotlinx-coroutines-test`, Room testing |
| Build | Gradle (Kotlin DSL) con version catalog · `minSdk 26` · `targetSdk 35` |

## Estructura del proyecto

```
comtax/
├── app/
│   ├── schemas/                       # Esquemas de Room exportados
│   └── src/
│       ├── main/java/com/comtax/app/
│       │   ├── data/
│       │   │   ├── local/             # Room: base de datos, DAOs, entidades y consultas
│       │   │   ├── repository/        # Repositorios (cuentas, sobres, movimientos, balance)
│       │   │   ├── security/          # PinHasher, PinManager
│       │   │   └── export/            # CsvExporter
│       │   ├── di/                    # Módulos de Hilt
│       │   ├── ui/                    # Pantallas Compose por funcionalidad
│       │   │   ├── lock/  home/  altamovimiento/  balance/  ajustes/
│       │   │   ├── common/            # Componentes reutilizables (tarjetas, botones, campos)
│       │   │   ├── navigation/        # NavHost y rutas
│       │   │   └── theme/             # Colores, tipografía y dimensiones
│       │   └── util/                  # Dinero, AppLockController
│       ├── main/res/
│       └── test/                      # Tests unitarios de DAOs, repositorios y ViewModels
├── tools/                             # Script para generar el icono (PowerShell)
├── gradle/                            # Wrapper y version catalog
└── build.gradle.kts
```

## Instalación y ejecución

Requisitos: [Android Studio](https://developer.android.com/studio) reciente, JDK 17 y un dispositivo o emulador con Android 8.0 (API 26) o superior.

```bash
git clone https://github.com/jortegadmr/comtax.git
cd comtax
```

**Desde Android Studio:** abre la carpeta, espera a la sincronización de Gradle y pulsa *Run*.

**Desde la terminal:**

```bash
./gradlew assembleDebug        # genera el APK de depuración
./gradlew installDebug         # lo instala en el dispositivo conectado
./gradlew test                 # ejecuta los tests unitarios
```

En Windows usa `gradlew.bat` en lugar de `./gradlew`.

### Build de release firmada

Crea en la raíz un `keystore.properties` (no se versiona) con estas claves y Gradle firmará el release automáticamente:

```properties
storeFile=ruta/al/keystore.jks
storePassword=...
keyAlias=...
keyPassword=...
```

```bash
./gradlew assembleRelease
```

## Estado

Versión `1.2` (`versionCode 3`). Proyecto personal.
