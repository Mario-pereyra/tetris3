# Tetris3 🎮

Un juego clásico de Tetris desarrollado en Android usando Kotlin con arquitectura MVVM.

## 📱 Características

### 🎯 Funcionalidades del Juego
- **Jugabilidad clásica de Tetris** con todas las piezas tradicionales (I, O, T, S, Z, J, L)
- **Sistema de puntuación** con progresión de niveles
- **Top 10 puntuaciones** guardadas localmente
- **Controles táctiles** intuitivos para móviles
- **Caída rápida** para jugadores avanzados
- **Reinicio de partida** en cualquier momento
- **Interfaz responsive** adaptada a diferentes tamaños de pantalla

### 🏗️ Características Técnicas
- **Arquitectura MVVM** (Model-View-ViewModel)
- **Base de datos Room** para persistencia local
- **View personalizada** para el tablero de juego
- **Gestión de estado** con LiveData y Observer pattern
- **Animaciones fluidas** y renderizado optimizado

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Kotlin
- **Plataforma:** Android (API 24+)
- **Arquitectura:** MVVM
- **Base de datos:** Room
- **UI:** View Binding
- **Threading:** Coroutines
- **Gradle:** 8.11.1

## 📋 Requisitos del Sistema

### Requisitos Mínimos
- **Android:** API 24 (Android 7.0) o superior
- **RAM:** 2GB mínimo
- **Almacenamiento:** 50MB disponibles

### Requisitos de Desarrollo
- **Android Studio:** Arctic Fox o superior
- **JDK:** 11 o superior
- **Gradle:** 8.0 o superior
- **Kotlin:** 1.9.0 o superior

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/Mario-pereyra/tetris3.git
cd tetris3
```

### 2. Configurar Android Studio
1. Abrir Android Studio
2. Seleccionar "Open an existing project"
3. Navegar a la carpeta clonada y seleccionarla
4. Esperar a que se sincronicen las dependencias

### 3. Configurar Emulador o Dispositivo
- **Emulador:** Crear un AVD con API 24 o superior
- **Dispositivo físico:** Habilitar modo desarrollador y depuración USB

### 4. Compilar y Ejecutar
```bash
# Compilar el proyecto
./gradlew build

# Instalar en dispositivo/emulador
./gradlew installDebug

# O usar Android Studio: Run > Run 'app'
```

## 🎮 Controles del Juego

### Controles Táctiles
- **←** Mover pieza a la izquierda
- **→** Mover pieza a la derecha
- **🔄** Rotar pieza en sentido horario
- **⬇** Caída rápida de la pieza
- **🔄 Reiniciar** Comenzar nueva partida
- **🏆 Top 10** Ver mejores puntuaciones

### Mecánicas de Juego
- Las piezas caen automáticamente según el nivel
- Completar líneas horizontales las elimina y otorga puntos
- Cada 100 puntos aumenta el nivel y la velocidad
- El juego termina cuando las piezas llegan al tope

## 🏗️ Arquitectura del Proyecto

### Estructura de Directorios
```
app/src/main/java/com/example/tetris/
├── MainActivity.kt                 # Actividad principal
├── db/                            # Capa de base de datos
│   ├── PuntuacionDao.kt          # Interfaz de acceso a datos
│   ├── PuntuacionEntity.kt       # Entidad de puntuación
│   └── TetrisDatabase.kt         # Configuración de Room
├── juego/                        # Lógica del juego
│   ├── MotorJuegoTetris.kt      # Motor principal del juego
│   ├── ControladorPiezas.kt     # Controlador de piezas
│   └── ManejadorTablero.kt      # Manejador del tablero
├── modelo/                       # Modelos de datos
│   ├── TetrominoModelo.kt       # Modelo de pieza Tetris
│   ├── FormasPieza.kt           # Definición de formas
│   ├── TipoPieza.kt             # Tipos de piezas
│   └── Punto.kt                 # Coordenadas
├── viewmodel/                    # Capa de ViewModel
│   ├── TetrisViewModel.kt       # ViewModel principal
│   └── TetrisViewModelFactory.kt # Factory del ViewModel
└── vista/                        # Vistas personalizadas
    └── TableroView.kt           # Vista del tablero de juego
```

### Patrones Implementados
- **MVVM:** Separación de responsabilidades
- **Repository Pattern:** Abstracción de acceso a datos
- **Observer Pattern:** Comunicación reactiva entre componentes
- **Factory Pattern:** Creación de ViewModels
- **Singleton:** Configuración de base de datos

## 🎯 Funcionalidades Detalladas

### Sistema de Puntuación
- **Línea simple:** 10 puntos
- **Progresión de nivel:** Cada 100 puntos
- **Velocidad:** Incrementa con cada nivel
- **Persistencia:** Top 10 guardado localmente

### Tipos de Piezas (Tetrominós)
| Pieza | Forma | Color |
|-------|--------|-------|
| I | ████ | Cian |
| O | ██<br>██ | Amarillo |
| T | ███<br>&nbsp;█ | Magenta |
| S | &nbsp;██<br>██ | Verde |
| Z | ██<br>&nbsp;██ | Rojo |
| J | █<br>███ | Azul |
| L | &nbsp;&nbsp;█<br>███ | Naranja |

### Base de Datos
```sql
CREATE TABLE puntuaciones (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    puntuacion INTEGER NOT NULL
);
```

## 🐛 Depuración y Desarrollo

### Logs Importantes
```kotlin
// Motor del juego
Log.d("MotorJuego", "Nueva pieza generada: ${pieza.tipo}")
Log.d("MotorJuego", "Líneas eliminadas: $lineasEliminadas")

// Base de datos
Log.d("Database", "Puntuación guardada: $nombre - $puntuacion")
```

### Herramientas de Desarrollo
- **Database Inspector:** Ver datos de Room en tiempo real
- **Layout Inspector:** Analizar jerarquía de vistas
- **Profiler:** Monitorear rendimiento

## 🧪 Testing

### Ejecutar Tests
```bash
# Tests unitarios
./gradlew test

# Tests de instrumentación
./gradlew connectedAndroidTest

# Tests con cobertura
./gradlew jacocoTestReport
```

### Estructura de Tests
```
app/src/test/                     # Tests unitarios
app/src/androidTest/              # Tests de instrumentación
```

## 📸 Screenshots

_Próximamente: Capturas de pantalla del juego en acción_

## 🤝 Contribuir

### Cómo Contribuir
1. Fork el proyecto
2. Crear rama para feature (`git checkout -b feature/AmazingFeature`)
3. Commit los cambios (`git commit -m 'Add: AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

### Estándares de Código
- **Kotlin Coding Conventions**
- **Nombres en español** para variables y métodos del dominio
- **Comentarios descriptivos** para lógica compleja
- **Tests unitarios** para nueva funcionalidad

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## 👨‍💻 Autor

**Mario Pereyra**
- GitHub: [@Mario-pereyra](https://github.com/Mario-pereyra)

## 🙏 Agradecimientos

- Inspirado en el clásico juego Tetris de Alexey Pajitnov
- Comunidad Android de desarrollo en Kotlin
- Documentación oficial de Android Developers

## 📞 Soporte

Si encuentras algún problema o tienes sugerencias:

1. **Issues:** [Crear nueva issue](https://github.com/Mario-pereyra/tetris3/issues)
2. **Discussions:** Usar la sección de discusiones del repositorio
3. **Email:** [Crear issue para contacto](https://github.com/Mario-pereyra/tetris3/issues)

---

⭐ ¡No olvides dar una estrella al proyecto si te gustó! ⭐