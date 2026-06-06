# Contributing to EG MobileMoney Asist

¡Gracias por tu interés en contribuir a **EG MobileMoney Asist**! 🎉

## 🚀 Cómo contribuir

### 1. Fork y clone

```bash
# Fork el repo desde GitHub y luego clona tu fork
git clone https://github.com/TU_USUARIO/EGMobileMoneyAsist-AI-.git
cd EGMobileMoneyAsist-AI-
```

### 2. Configura tu entorno

- **Java 17+** (recomendado: JDK 17)
- **Android Studio** Ladybug o superior
- **Android SDK** (API 34)

```bash
export JAVA_HOME="/path/to/jdk17"
export ANDROID_HOME="/path/to/android-sdk"
```

### 3. Crea una rama

```bash
git checkout -b feature/mi-nueva-feature
```

### 4. Haz tus cambios

- Sigue la arquitectura existente (Clean Architecture + MVVM)
- Escribe código en **Kotlin** con **Jetpack Compose**
- Añade tests si es posible

### 5. Compila y verifica

```bash
./gradlew assembleDebug
./gradlew test
```

### 6. Crea un Pull Request

- Describe claramente tus cambios
- Enlaza cualquier issue relacionado
- Incluye capturas de pantalla si hay cambios de UI

## 📋 Guidelines

- **Idioma del código**: Inglés
- **Commits**: Usa [Conventional Commits](https://www.conventionalcommits.org/)
- **Formato**: Sigue el estilo de código existente
- **UI**: Usa Jetpack Compose y Material3

## 🐛 Reportar bugs

Usa [GitHub Issues](https://github.com/Tranquilino1/EGMobileMoneyAsist-AI-/issues) con la plantilla de bug report.

## 📄 Licencia

Al contribuir, aceptas que tus contribuciones se licencien bajo **Apache License 2.0**.
