<<<<<<< HEAD
# Shed Client

A modular Minecraft 1.8.9 client modification featuring a comprehensive suite of gameplay enhancements, built with Java and MCP (Minecraft Codebase Project).

![Java](https://img.shields.io/badge/Java-8-ED8B00?logo=java)
![Minecraft](https://img.shields.io/badge/Minecraft-1.8.9-62B47A)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apache-maven)

## 🎯 Overview

Shed Client is a feature-rich Minecraft modification that demonstrates advanced Java programming concepts through game enhancement development. Built on the MCP framework, it showcases clean architecture, event-driven design, and modular code organization.

## ✨ Features

### Module System
- **Event-Driven Architecture**: Custom event bus implementation using Alpine
- **Modular Design**: Clean separation of concerns with extensible module framework
- **Category Organization**: Modules organized into Combat, Movement, Player, and Render categories
- **Hot-Swappable**: Runtime module toggling with keybind support

### Implemented Modules

**Combat**
- AutoClicker - Automated clicking with configurable CPS
- KillAura - Advanced entity targeting system
- Reach - Extended interaction distance

**Movement**
- BHop - Bunny hop movement optimization
- Flight - Creative-style flight mechanics
- Scaffold - Automated block placement
- Sprint - Automatic sprint management
- Eagle - Edge detection for building
- InventoryMove - Movement while in inventory
- NoSlowDown - Remove item slowdown effects

**Player**
- NoFall - Fall damage prevention

**Render**
- HUD - Heads-up display with module list
- TargetHUD - Entity information overlay
- KeyStrokes - Visual keystroke indicator
- Charms - Custom visual effects

### User Interface
- **ClickGUI**: Intuitive graphical module configuration interface
- **Custom Main Menu**: Branded client interface
- **Notification System**: Real-time module state feedback
- **Settings Framework**: Per-module configuration system

## 🛠️ Technical Stack

- **Language**: Java 8
- **Build Tool**: Maven
- **Framework**: MCP (Minecraft Codebase Project) 1.8.9
- **Event System**: Alpine Event Bus
- **Dependency Injection**: Project Lombok
- **Additional Libraries**:
  - LWJGL 2.9.4 (graphics/input)
  - Netty (networking)
  - Gson (JSON serialization)
  - Reflections (runtime class scanning)

## 🏗️ Architecture

### Core Components

```
Shed/
├── Modules/          # Module system and implementations
│   ├── Module.java          # Base module class
│   ├── ModuleManager.java   # Module lifecycle management
│   ├── Category.java        # Module categorization
│   └── impl/               # Concrete module implementations
├── event/           # Custom event system
├── UI/             # User interface components
│   ├── ClickGUI/          # Configuration interface
│   └── MainMenu/          # Custom main menu
├── Setting/        # Module settings framework
├── Notification/   # Notification system
└── util/          # Utility classes
```

### Design Patterns

**Observer Pattern**: Event-driven module communication via Alpine event bus
**Singleton Pattern**: Central client instance management
**Strategy Pattern**: Modular feature implementations with shared base class
**Annotation-Based Configuration**: Module metadata via `@ModuleInfo` annotations

## 📋 Prerequisites

- Java 8 JDK
- Maven 3.6+
- Minecraft 1.8.9

## 🔧 Building

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/ShedClient.git
   cd ShedClient/MavenMCP-1.8.9-master
   ```

2. **Build with Maven**
   ```bash
   mvn clean package
   ```
   
   Alternatively, use the Maven sidebar in your IDE (IntelliJ IDEA recommended).

3. **Locate artifact**
   
   The compiled JAR will be in `/target` directory:
   ```
   target/MCP-1.8.9-jar-with-dependencies.jar
   ```

4. **Installation**
   
   Copy the JAR to your Minecraft versions folder:
   ```
   %appdata%/.minecraft/versions/
   ```

## 🚀 Development

### Setting Up Workspace

1. Import the Maven project into your IDE
2. Set project SDK to **Java 8**
3. Allow Maven to index and download dependencies
4. Configure run configuration with working directory: `./test_run/`

### Running in IDE

Execute `Start.java` with the following VM options:
```
-Djava.library.path=./test_natives/
```

The client will launch with Minecraft directory set to `./test_run/`.

### Creating New Modules

```java
@ModuleInfo(
    name = "MyModule",
    description = "Module description",
    category = Category.PLAYER,
    enabled = false
)
public class MyModule extends Module {
    @Override
    public void onEnable() {
        // Module enable logic
    }
    
    @Override
    public void onUpdate() {
        // Per-tick update logic
    }
}
```

## 🎮 Usage

### In-Game Controls

- **Right Shift**: Open ClickGUI
- **Module Keybinds**: Configurable per-module toggle keys
- **ClickGUI Navigation**: Mouse-driven interface for module configuration

### Module Management

1. Open ClickGUI (Right Shift by default)
2. Navigate to desired category
3. Click to toggle modules on/off
4. Configure module settings via expanded panels

## 🔑 Key Technical Implementations

### Event System
Custom event bus implementation allowing modules to subscribe to game events:
- Update events (tick-based)
- Render events (2D overlay)
- Input events (keyboard/mouse)
- Network events (packet handling)

### Module Lifecycle
Automatic module discovery and registration using reflection, with clean subscription/unsubscription to event bus on toggle.

### Settings Framework
Type-safe settings system supporting boolean, numeric, and enum configurations with automatic GUI generation.

## 🎓 Learning Outcomes

This project demonstrates proficiency in:
- Large-scale Java application architecture
- Event-driven programming patterns
- Game modification and reverse engineering concepts
- Maven build system and dependency management
- GUI development with custom rendering
- Real-time graphics programming with LWJGL
- Network protocol interaction
- Reflection and runtime class manipulation

## 📝 Disclaimer

This project is for **educational purposes only**, demonstrating software engineering concepts through game modification. Use responsibly and in accordance with server rules and Minecraft's Terms of Service.

## 🤝 Acknowledgments

- MavenMCP framework by [marCloud](https://marcloud.net)
- Alpine event bus by ZeroMemes
- Minecraft by Mojang Studios

## 📧 Contact

Erik Abolins - [GitHub Profile](https://github.com/erikabolins)

Project Link: [https://github.com/erikabolins/ShedClient](https://github.com/erikabolins/ShedClient)

---

*Built as a demonstration of advanced Java development and software architecture concepts.*
=======
