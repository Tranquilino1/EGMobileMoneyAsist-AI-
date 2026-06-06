# 📖 Documentación Técnica Oficial

## EG Mobile Money Assist

**Autor:** Tranquilino Mba Ncogo Andeme  
**Representación:** Universidad Afro-Americana de África Central (AAUCA)  
**Contexto de Desarrollo:** Hackathon de 24 Horas de FP Salesianos  
**Fecha:** Junio 2026  
**Versión:** 1.0.0  
**Licencia:** Apache 2.0  

---

## 1. Introducción y Propósito

**EG Mobile Money Assist** es una aplicación Android nativa diseñada para revolucionar la gestión de finanzas personales. Al integrar inteligencia artificial directamente con el control de presupuesto local, la aplicación permite a los usuarios registrar transacciones financieras de forma conversacional utilizando comandos de voz en lenguaje natural.

Este proyecto ha sido desarrollado en el marco del **Hackathon de 24 Horas organizado por FP Salesianos**, como proyecto representante de la **Universidad Afro-Americana de África Central (AAUCA)** por el participante **Tranquilino Mba Ncogo Andeme**.

### Objetivos Clave:
- **Interactividad Inteligente**: Permitir el registro de transacciones complejas en segundos sin necesidad de navegar por menús y formularios extensos.
- **Privacidad Absoluta (Offline-First)**: Almacenar toda la información financiera confidencial a nivel local (SQLite/Room), garantizando la soberanía de los datos del usuario.
- **Multi-moneda**: Soporte nativo para múltiples divisas en tiempo real para usuarios globales.

---

## 2. Arquitectura del Sistema

La aplicación está construida siguiendo los principios de **Clean Architecture** estructurada en capas y el patrón de presentación **MVVM (Model-View-ViewModel)**. Esto asegura que el código sea testeable, mantenible y scalable.

```
┌──────────────────────────────────────────────────────────┐
│                      Capa de UI                          │
│          (Jetpack Compose, ViewModels, States)           │
└──────────────────────────┬───────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────┐
│                   Capa de Dominio                        │
│          (Casos de Uso, Entidades, Interfaces)           │
└──────────────────────────┬───────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────┐
│                    Capa de Datos                         │
│       (Room DB, Repositorios, Gemini API Client)         │
└──────────────────────────────────────────────────────────┘
```

### Detalle de Capas:
1. **Capa de Presentación (UI)**: Desarrollada al 100% con **Jetpack Compose** para una UI reactiva y declarativa. Sigue las directrices de Material 3 y utiliza ViewModels para retener el estado de las vistas frente a cambios de configuración.
2. **Capa de Dominio**: Contiene las reglas de negocio puras, entidades independientes del framework (como `Transaction`, `Account`, `Category`) y los casos de uso (`GetTransactionsUseCase`, `SaveVoiceTransactionUseCase`).
3. **Capa de Datos**: Realiza la persistencia local con **Room DB** e implementa clientes de red usando **Ktor**. Aquí también se ubica el cliente de integración para el servicio de inteligencia artificial **Gemini 1.5 Flash**.

---

## 3. Estructura de Módulos

El proyecto está modularizado para optimizar los tiempos de compilación y aislar el ámbito de cada funcionalidad:

- **`app/`**: Inicializa la aplicación, gestiona el gráfico de navegación global e inyecta las dependencias del sistema.
- **`feature/`**: Contiene módulos específicos por funcionalidad:
  - `accounts/`: Gestión y creación de cuentas bancarias/efectivo.
  - `edit-transaction/`: Interfaz para agregar y editar transacciones, integrando el grabador de voz.
  - `reports/`: Gráficos interactivos y análisis de presupuesto mensual/semanal.
  - `exchange-rates/`: Lógica de conversión de divisas.
- **`shared/`**: Recursos compartidos reutilizables por múltiples módulos:
  - `data/`: Contiene la base de datos Room, esquemas y repositorios de datos persistentes.
  - `domain/`: Contiene el cliente SDK de Gemini y la lógica de negocio transversal.
  - `ui/`: Temas de colores, tipografía Material 3 y componentes reutilizables.
- **`widget/`**: Módulo del widget de escritorio de Android para visualizar balances rápidamente.

---

## 4. Motor de IA por Voz: Integración con Gemini 1.5 Flash

La característica distintiva de **EG Mobile Money Assist** es su motor de procesamiento de voz en lenguaje natural.

```
┌──────────────┐      ┌────────────────┐      ┌─────────────────┐      ┌─────────────┐
│  Grabación   │      │  Envío de      │      │ Procesamiento   │      │ Persistencia│
│   de Audio   ├─────▶│  Audio a       ├─────▶│ Gemini 1.5      ├─────▶│ y Registro  │
│ (AudioRecord)│      │  Gemini API    │      │ (Generación JSON│      │ (Room DB)   │
└──────────────┘      └────────────────┘      └────────┬────────┘      └─────────────┘
                                                       │
                                                       ▼
                                              ┌────────────────┐
                                              │ Extracción de: │
                                              │ • Monto        │
                                              │ • Categoría    │
                                              │ • Descripción  │
                                              │ • Tipo (Gasto) │
                                              └────────────────┘
```

### Flujo de Ejecución Detallado:
1. **Captura**: La API `AudioRecord` de Android captura la voz del usuario en formato PCM de alta fidelidad, convirtiéndolo a un búfer optimizado temporalmente.
2. **Networking**: El fragmento de audio se procesa y se envía mediante el cliente Ktor a la API de **Gemini 1.5 Flash**, configurado con un prompt del sistema especializado.
3. **Inferencia y Análisis**: Gemini procesa el audio, extrae la intención semántica y genera un objeto **JSON estructurado** bajo un esquema riguroso:
   ```json
   {
     "amount": 45.00,
     "currency": "USD",
     "category": "Alimentación",
     "type": "EXPENSE",
     "description": "Supermercado"
   }
   ```
4. **Mapeo Automático**: La aplicación recibe el JSON, lo parsea utilizando `kotlinx.serialization` y ejecuta la lógica para encontrar la cuenta del usuario más adecuada y la categoría preexistente en la base de datos.
5. **Confirmación**: El usuario visualiza una tarjeta interactiva con los datos extraídos para confirmar o ajustar los detalles antes de persistirlos.

---

## 5. Base de Datos y Almacenamiento Local

La privacidad es una prioridad. Todos los datos sensibles se gestionan en local.

### Esquema de Datos Principal (Room DB):

#### Entidad: `Transaction` (`transaction_table`)
- `id` (UUID, Primary Key)
- `amount` (BigDecimal)
- `currency` (String)
- `description` (String, nullable)
- `timestamp` (Long)
- `type` (Enum: INCOME, EXPENSE, TRANSFER)
- `categoryId` (UUID, Foreign Key → Category)
- `accountId` (UUID, Foreign Key → Account)

#### Entidad: `Account` (`account_table`)
- `id` (UUID, Primary Key)
- `name` (String)
- `balance` (BigDecimal)
- `currency` (String)
- `colorHex` (String)

#### Entidad: `Category` (`category_table`)
- `id` (UUID, Primary Key)
- `name` (String)
- `iconKey` (String)
- `colorHex` (String)

### Configuración (Preferences DataStore):
Se utiliza Jetpack DataStore para configuraciones clave-valor que no requieren una base de datos relacional (por ejemplo: la clave API de Gemini del usuario, moneda principal por defecto, estado del onboarding, y tema de la app).

---

## 6. Seguridad y Buenas Prácticas

1. **Clave API Descentralizada**: La aplicación no almacena claves API del servidor en el código. El usuario introduce su propia clave de Google AI Studio, la cual se encripta y se guarda localmente en el almacenamiento privado de Android mediante `EncryptedSharedPreferences` / `DataStore`.
2. **Sin Tráfico de Datos de Terceros**: Las grabaciones de voz se envían directamente a los endpoints de Google Gemini API sin pasar por ningún servidor intermediario. No hay servicios de analíticas invasivos.
3. **Copia de Seguridad Local**: La aplicación cuenta con funcionalidad para exportar y restaurar la base de datos Room en formato cifrado y en archivos planos CSV.

---

## 7. Compilación del Proyecto

Para compilar y generar la APK de EG Mobile Money Assist localmente:

### Requisitos Previos:
- JDK 17 o superior.
- Android SDK (API 34 instalado).
- Variables de entorno:
  ```powershell
  $env:ANDROID_HOME = "C:\Users\RYESA\AppData\Local\Android\Sdk"
  ```

### Comandos de Compilación:
Compilar el paquete en modo Debug:
```bash
./gradlew assembleDebug
```

Para generar la APK de lanzamiento (Release) optimizada:
```bash
./gradlew assembleRelease
```
El archivo APK resultante se ubica en:
`app/build/outputs/apk/debug/app-debug.apk` o `app/build/outputs/apk/release/app-release.apk`.
