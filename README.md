<div align="center">

# 💸 EG Mobile Money Assist

### Tu asistente de finanzas personales potenciado por Inteligencia Artificial

[![Release](https://img.shields.io/badge/Release-v1.0.0-00E676?style=for-the-badge&logo=android&logoColor=white)](https://github.com/Tranquilino1/EGMobileMoneyAsist-AI-/releases)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue?style=for-the-badge)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Gemini AI](https://img.shields.io/badge/Gemini_1.5-AI_Powered-FF6F00?style=for-the-badge&logo=google&logoColor=white)](https://ai.google.dev/)

<br/>

**EG Mobile Money Assist** es una app Android de código abierto para la gestión de finanzas personales, potenciada por **inteligencia artificial (Gemini 1.5 Flash)** que permite registrar transacciones por voz, analizar patrones de gasto y tomar el control total de tu dinero.

<br/>

[📲 Descargar APK](https://github.com/Tranquilino1/EGMobileMoneyAsist-AI-/releases/latest) · [🌐 Landing Page](https://tranquilino1.github.io/EGMobileMoneyAsist-AI-/) · [📊 PPT Interactiva](docs/presentation/index.html) · [📖 Doc. Técnica](docs/TECHNICAL_DOCUMENTATION.md) · [🐛 Reportar Bug](https://github.com/Tranquilino1/EGMobileMoneyAsist-AI-/issues)

</div>

---

## ✨ Características Principales

| Característica | Descripción |
|:--|:--|
| 🎙️ **Registro por Voz con IA** | Habla naturalmente: _"gasté 30 dólares en comida"_ y la IA registra la transacción automáticamente |
| 🧠 **Gemini 1.5 Flash** | Análisis inteligente de patrones de gasto con categorización automática |
| 💰 **Multi-moneda** | Gestiona cuentas en diferentes monedas con tasas de cambio en tiempo real |
| 📊 **Informes Detallados** | Gráficos interactivos, categorías personalizables y exportación CSV |
| 🔒 **100% Privado** | Datos almacenados localmente. Sin servidores externos, sin tracking, sin anuncios |
| 🏗️ **Arquitectura Moderna** | Clean Architecture + MVVM con Jetpack Compose y Kotlin Coroutines |
| 📎 **Adjuntar Comprobantes** | Captura y adjunta fotos de recibos a cada transacción |
| ⚡ **Ultra Rápida** | Construida con Jetpack Compose para una experiencia fluida |

---

## 🚀 Inicio Rápido

### Requisitos
- **Android 7.0+** (API 24)
- **Java 17+** (para compilar)
- **Android Studio** Ladybug o superior

### Instalación desde APK

1. Descarga el APK desde [**Releases**](https://github.com/Tranquilino1/EGMobileMoneyAsist-AI-/releases/latest)
2. En tu dispositivo Android: **Ajustes → Seguridad → Fuentes desconocidas** (activar)
3. Abre el APK descargado e instala
4. ¡Listo! 🎉

### Compilar desde el código fuente

```bash
# 1. Clonar el repositorio
git clone https://github.com/Tranquilino1/EGMobileMoneyAsist-AI-.git
cd EGMobileMoneyAsist-AI-

# 2. Configurar variables de entorno
export JAVA_HOME="/path/to/jdk17"
export ANDROID_HOME="/path/to/android-sdk"

# 3. Compilar APK debug
./gradlew assembleDebug

# 4. El APK estará en: app/build/outputs/apk/debug/app-debug.apk
```

---

## 🏗️ Arquitectura del Proyecto

```
EGMobileMoneyAsist-AI-/
├── app/                      # Módulo principal de la aplicación
├── feature/                  # Feature modules (Clean Architecture)
│   ├── accounts/             # Gestión de cuentas
│   ├── budgets/              # Presupuestos
│   ├── categories/           # Categorías personalizables
│   ├── edit-transaction/     # Edición de transacciones + IA por voz
│   ├── exchange-rates/       # Tasas de cambio multi-moneda
│   ├── home/                 # Pantalla principal / Dashboard
│   ├── loans/                # Préstamos
│   ├── onboarding/           # Onboarding flow
│   ├── planned-payments/     # Pagos programados
│   ├── reports/              # Informes y gráficos
│   ├── search/               # Búsqueda de transacciones
│   ├── settings/             # Configuración
│   └── transactions/         # Lista de transacciones
├── shared/                   # Módulos compartidos
│   ├── base/                 # Utilidades base + Audio Recorder
│   ├── data/                 # Capa de datos (Room DB, repos)
│   ├── domain/               # Lógica de negocio + Gemini Agent
│   └── ui/                   # Componentes UI reutilizables
├── widget/                   # Widget de balance para home screen
├── docs/                     # Documentación
│   └── landing/              # Landing page (GitHub Pages)
└── gradle/                   # Configuración de Gradle
```

---

## 🛠️ Stack Tecnológico

### Core
| Tecnología | Uso |
|:--|:--|
| [Kotlin](https://kotlinlang.org/) | 100% del código |
| [Jetpack Compose](https://developer.android.com/jetpack/compose) | UI declarativa moderna |
| [Material3](https://m3.material.io/) | Componentes de diseño |
| [Kotlin Coroutines + Flow](https://kotlinlang.org/docs/coroutines-overview.html) | Concurrencia y datos reactivos |
| [Hilt](https://dagger.dev/hilt/) | Inyección de dependencias |
| [ArrowKt](https://arrow-kt.io/) | Programación funcional |

### Inteligencia Artificial
| Tecnología | Uso |
|:--|:--|
| [Gemini 1.5 Flash](https://ai.google.dev/) | Procesamiento de voz natural y categorización |
| Android AudioRecord API | Captura de audio para comandos de voz |

### Persistencia & Networking
| Tecnología | Uso |
|:--|:--|
| [Room DB](https://developer.android.com/training/data-storage/room) | Base de datos SQLite ORM |
| [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) | Key-value storage |
| [Ktor Client](https://ktor.io/) | Cliente HTTP |
| [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) | Serialización JSON |

### Testing & Build
| Tecnología | Uso |
|:--|:--|
| [JUnit4](https://junit.org/) | Framework de pruebas |
| [Kotest](https://kotest.io/) | Assertions para unit tests |
| [Gradle KTS](https://docs.gradle.org/) | Build system con Kotlin DSL |
| [Gradle Convention Plugins](https://docs.gradle.org/) | Lógica de build compartida |

---

## 🎙️ Cómo funciona la IA de Voz

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Usuario    │────▶│   Grabar     │────▶│  Gemini 1.5  │────▶│  Registrar   │
│   habla      │     │   audio      │     │  Flash API   │     │  transacción │
│              │     │  (Android)   │     │  (análisis)  │     │  (Room DB)   │
└──────────────┘     └──────────────┘     └──────────────┘     └──────────────┘
                                                │
                                                ▼
                                    ┌──────────────────────┐
                                    │  Extrae:             │
                                    │  • Monto             │
                                    │  • Categoría         │
                                    │  • Descripción       │
                                    │  • Tipo (gasto/ingr) │
                                    │  • Cuenta            │
                                    └──────────────────────┘
```

**Ejemplo de uso:**
> 🎙️ _"Gasté cuarenta y cinco dólares en el supermercado"_
>
> ✅ La IA registra: **-$45.00** · Categoría: **Alimentación** · Cuenta: **Principal**

---

## 📄 Licencia

```
Copyright 2026 EG Mobile Money Assist

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

<div align="center">
 
### ⭐ ¡Dale una estrella si te gusta el proyecto! ⭐
 
Proyecto desarrollado en el **Hackathon de 24 Horas de FP Salesianos**  
Participante representante de la **AAUCA**: **Tranquilino Mba Ncogo Andeme** ([Tranquilino1](https://github.com/Tranquilino1))
 
[📲 Descargar](https://github.com/Tranquilino1/EGMobileMoneyAsist-AI-/releases/latest) · [🌐 Web](https://tranquilino1.github.io/EGMobileMoneyAsist-AI-/) · [🐛 Issues](https://github.com/Tranquilino1/EGMobileMoneyAsist-AI-/issues)
 
</div>
