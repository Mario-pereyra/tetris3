# Tetris Android 🎮

Un juego clásico de Tetris desarrollado en Kotlin para Android, implementando una arquitectura MVVM moderna y persistencia de puntuaciones con Room.

## 📱 Características

### Funcionalidades del Juego
- **Jugabilidad clásica de Tetris** con las 7 piezas tradicionales (I, O, T, S, Z, J, L)
- **Sistema de puntuación** progresivo
- **Sistema de niveles** con incremento de dificultad
- **Caída rápida** de piezas para jugadores avanzados
- **Detección de colisiones** precisa
- **Eliminación de líneas completas** con animación
- **Game Over** con diálogo de finalización

### Características Técnicas
- **Persistencia de puntuaciones** con base de datos Room
- **Top 10 de mejores puntuaciones** con nombres de jugadores
- **Interfaz responsive** adaptada a diferentes tamaños de pantalla
- **Controles táctiles** optimizados para móviles
- **Arquitectura MVVM** para separación de responsabilidades
- **Custom Views** para renderizado optimizado del tablero

## 🏗️ Arquitectura

### Patrón MVVM
El proyecto implementa el patrón Model-View-ViewModel para una separación clara de responsabilidades:

```
📁 com.example.tetris/
├── 📁 vista/           # Views personalizadas
│   └── TableroView.kt  # Renderizado del tablero de juego
├── 📁 viewmodel/       # ViewModels y Factory
│   ├── TetrisViewModel.kt
│   └── TetrisViewModelFactory.kt
├── 📁 modelo/          # Modelos de datos del juego
│   ├── TetrominoModelo.kt    # Modelo de las piezas
│   ├── TipoPieza.kt          # Enum de tipos de piezas
│   ├── Punto.kt              # Coordenadas
│   └── FormasPieza.kt        # Definiciones de formas
├── 📁 juego/           # Lógica principal del juego
│   ├── MotorJuegoTetris.kt   # Motor principal del juego
│   ├── ManejadorTablero.kt   # Gestión del tablero
│   └── ControladorPiezas.kt  # Control de piezas
├── 📁 db/              # Persistencia de datos
│   ├── TetrisDatabase.kt     # Base de datos Room
│   ├── PuntuacionDao.kt      # DAO para puntuaciones
│   └── PuntuacionEntity.kt   # Entidad de puntuación
└── MainActivity.kt     # Actividad principal
```

### Componentes Principales

#### 🎮 Motor de Juego (`MotorJuegoTetris`)
- Controla el bucle principal del juego
- Gestiona la velocidad de caída de las piezas
- Maneja la progresión de niveles
- Calcula puntuaciones

#### 🎯 Manejo del Tablero (`ManejadorTablero`)
- Detección de colisiones
- Eliminación de líneas completas
- Asentamiento de piezas
- Estado del tablero

#### 🧩 Control de Piezas (`ControladorPiezas`)
- Generación aleatoria de piezas
- Movimientos (izquierda, derecha, rotación)
- Caída rápida
- Validación de movimientos

#### 📊 Vista del Tablero (`TableroView`)
- Renderizado custom del tablero
- Dibujo de piezas y celdas ocupadas
- Visualización de Game Over
- Optimización de performance

## 🚀 Instalación y Compilación

### Requisitos Previos
- **Android Studio** Hedgehog | 2023.1.1 o superior
- **Android SDK** API 24 (Android 7.0) o superior
- **Kotlin** 1.9.0 o superior  
- **Gradle** 8.0 o superior
- **JDK** 11 o superior

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/Mario-pereyra/tetris3.git
cd tetris3
```

2. **Abrir en Android Studio**
- Abrir Android Studio
- Seleccionar "Open an existing project"
- Navegar a la carpeta del proyecto

3. **Sincronizar dependencias**
```bash
# Dar permisos de ejecución al script de Gradle
chmod +x gradlew

# Construir el proyecto
./gradlew build
```

> **Nota**: Si encuentras problemas de compatibilidad de versiones con Android Gradle Plugin, verifica que tengas las versiones correctas de Android Studio y SDK instaladas.

4. **Ejecutar en dispositivo/emulador**
- Conectar dispositivo Android con depuración USB habilitada, o
- Iniciar emulador Android
- Hacer clic en "Run" (▶️) en Android Studio

### Configuración del Proyecto
- **Application ID**: `com.example.tetris`
- **App Name**: tetris
- **Compile SDK**: 35
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35
- **Version Code**: 1
- **Version Name**: "1.0"

### Dependencias Principales
```kotlin
// Core Android
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.appcompat)
implementation(libs.material)
implementation(libs.androidx.activity)
implementation(libs.androidx.constraintlayout)

// ViewModel y Lifecycle
implementation(libs.androidx.lifecycle.viewmodel.ktx)

// Room Database
implementation(libs.androidx.room.runtime)
ksp(libs.androidx.room.compiler)
```

## 🎯 Cómo Jugar

### Controles
- **⬅️ Izquierda**: Mover pieza hacia la izquierda
- **➡️ Derecha**: Mover pieza hacia la derecha  
- **🔄 Rotar**: Rotar pieza en sentido horario
- **⬇️ Abajo (Caída Rápida)**: Hacer caer la pieza inmediatamente
- **🔄 Reiniciar**: Reiniciar el juego actual
- **🏆 Top 10**: Ver las mejores puntuaciones

### Objetivo
- Completar líneas horizontales para eliminarlas y ganar puntos
- Evitar que las piezas lleguen hasta arriba del tablero
- Alcanzar la mayor puntuación posible

### Sistema de Puntuación
- **Línea simple**: 10 puntos base
- **Múltiples líneas**: Bonificación por combo
- **Progresión de nivel**: Cada 100 puntos por nivel
- **Velocidad**: Aumenta con cada nivel

### Tipos de Piezas (Tetrominós)
- **I** (Cyan): Línea recta de 4 bloques
- **O** (Amarillo): Cuadrado de 2x2
- **T** (Magenta): Forma de T
- **S** (Verde): Forma de S
- **Z** (Rojo): Forma de Z
- **J** (Azul): Forma de J
- **L** (Naranja): Forma de L

## 🛠️ Desarrollo

### Estructura de Código
El proyecto sigue principios de **Clean Architecture** y **SOLID**:

- **Separación de responsabilidades**: Cada clase tiene una responsabilidad específica
- **Inyección de dependencias**: ViewModelFactory para ViewModels
- **Observadores**: LiveData para comunicación reactiva
- **Persistencia**: Room database para datos locales

### Patrones Implementados
- **MVVM**: Separación Vista-Lógica-Datos
- **Observer**: LiveData para actualizaciones de UI
- **Factory**: Para creación de ViewModels
- **DAO**: Para acceso a base de datos

### Testing
```bash
# Tests unitarios
./gradlew test

# Tests instrumentados
./gradlew connectedAndroidTest
```

### Configuración de Build
- **Compile SDK**: 35
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35
- **Version Code**: 1
- **Version Name**: "1.0"

## 🐛 Problemas Conocidos

- **Compatibilidad de Gradle**: El proyecto requiere versiones específicas de Android Gradle Plugin. Si tienes problemas de compilación, asegúrate de tener Android Studio actualizado.

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

### Convenciones de Código
- Seguir las convenciones de Kotlin
- Usar nombres descriptivos en español para variables y métodos
- Documentar funciones complejas
- Mantener clases pequeñas y enfocadas

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

## 👨‍💻 Autor

**Mario Pereyra**
- GitHub: [@Mario-pereyra](https://github.com/Mario-pereyra)

---

¡Disfruta jugando Tetris! 🎮✨
