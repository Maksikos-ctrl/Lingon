# Lingon Quiz - Desktop 🌱

<div align="center">
  <img src="resources/images/icon.png" alt="Lingon Logo" width="200"/>
  ![icon](https://github.com/user-attachments/assets/01e0c8bf-b2a1-49ed-bc71-3b0240d6f8ce)


  <p><i>Interactive educational desktop application with cloud synchronization</i></p>

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Firebase](https://img.shields.io/badge/Firebase-Realtime%20DB-yellow.svg)](https://firebase.google.com/)
[![H2 Database](https://img.shields.io/badge/H2-Database-blue.svg)](https://www.h2database.com/)
[![Swing](https://img.shields.io/badge/Java-Swing%20UI-blue.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
</div>

## 📝 Description

Lingon Quiz is a modern Java desktop educational application with **Firebase cloud synchronization**. Built with Java Swing, it provides an interactive quiz experience with persistent user data storage both locally (H2 database) and in the cloud (Firebase). The application demonstrates advanced software architecture patterns and real-time data synchronization capabilities. Originally created as a semester project for Informatics 2 course at FRI UNIZA, it has evolved into a comprehensive quiz platform with modern backend integration.

## 🌟 Key Features

### 🔥 **Cloud Synchronization**
- **Firebase Realtime Database** integration for cloud data storage
- **Real-time user data sync** with other devices/instances
- **Automatic backup** of user profiles and test history
- **Cross-device consistency** - same account accessible everywhere

### 🧠 **Various Question Types**
- **Multiple choice questions** (VyberovaOtazka)
- **Text input questions** (VpisovaciaOtazka)
- **Matching pair questions** (ParovaciaOtazka)

### 🏆 **Advanced Point & Level System**
- **XP rewards** for correct answers (10 XP per correct answer)
- **Progressive user levels** (Začiatočník → Pokročilý → Expert)
- **Detailed statistics** tracking with success rate calculation
- **Level-based category unlocking** system

### 💻 **Modern Desktop Interface**
- **Java Swing** with modern UI design and animations
- **User profile management** with avatar display
- **Firebase connection indicators** and sync status
- **Responsive design** with smooth transitions
- **Category-based quiz organization** with progress tracking

### 📊 **Comprehensive Data Management**
- **Dual storage system**: Local H2 + Cloud Firebase
- **Offline capability** with automatic sync when online
- **Test history tracking** with detailed analytics
- **User profile persistence** across sessions
- **Data consistency** with Firebase priority system

## 💻 Technologies & Architecture

### **Core Technologies**
- **Java 11+** with Swing for desktop UI
- **H2 Database** for local data storage and offline capability
- **Firebase Admin SDK** for cloud synchronization
- **HikariCP** for efficient database connection pooling
- **Gson** for JSON data serialization

### **Architecture Patterns**
- **MVC Pattern** (Model-View-Controller separation)
- **Strategy Pattern** (IOdpovedovaStrategia interface)
- **Template Method** (AbstractneZadanie class)
- **Factory Method** (Question type creation)
- **Singleton** (DatabaseManager, FirebaseManager)
- **Observer Pattern** (Real-time Firebase listeners)

### **Database Design**
- **Local H2**: Fast access, offline capability, caching
- **Firebase**: Cloud storage, real-time sync, cross-device access
- **Hybrid approach**: Firebase priority with H2 fallback

## 🔍 Polymorphism Demonstration

The project showcases advanced OOP concepts and polymorphism:

```java
// Unified interface for different question types
IZadanie zadanie = otazky.get(aktualnaOtazka);
zadanie.zobrazGrafiku(kontajner);
boolean jeSpravna = zadanie.skontrolujOdpoved(odpoved);

// Dynamic strategy switching for answer checking
VpisovaciaOtazka otazka = new VpisovaciaOtazka("How do you say 'Hello' in Slovak?", "Ahoj");
otazka.setStrategia(new ObsahujeStrategia());  // Runtime strategy change

// Firebase data handling with type safety
FirebaseManager.getInstance().syncUser(pouzivatel);
```

## 🚀 Getting Started

### **Requirements**
- **JDK 11** or newer
- **Internet connection** for Firebase synchronization
- **firebase-service-account.json** file (for Firebase integration)

### **Firebase Setup**
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
2. Enable **Realtime Database**
3. Go to **Project Settings** → **Service accounts**
4. Click **"Generate new private key"** and download JSON file
5. Rename to `firebase-service-account.json` and place in project root
6. Configure database rules:

```json
{
  "rules": {
    ".read": true,
    ".write": true,
    "users": {
      ".indexOn": ["email", "totalXP"]
    },
    "testHistory": {
      ".indexOn": ["timestamp", "userEmail"]
    }
  }
}
```

### **Running the Application**

```bash
# Clone the repository
git clone https://github.com/your-username/lingon-quiz-desktop.git
cd lingon-quiz-desktop

# Place firebase-service-account.json in project root
# (Optional - app works offline without Firebase)

# Run with Gradle
./gradlew run

# Or build and run JAR
./gradlew build
java -jar build/libs/lingon-desktop.jar
```

## 🔥 Firebase Integration Features

### **User Management**
- **Cloud user profiles** with automatic synchronization
- **Create account once, access everywhere**
- **Real-time XP and statistics updates**
- **Persistent login** across application restarts

### **Data Synchronization**
- **Automatic cloud backup** of all user data
- **Real-time sync** when online
- **Offline mode** with local H2 database
- **Conflict resolution** with Firebase data priority

### **Test History**
- **Cloud storage** of all test results
- **Cross-device access** to complete history
- **Real-time updates** when tests completed
- **Detailed analytics** and progress tracking

## 📊 Firebase Database Structure

```json
{
  "users": {
    "user_email_com": {
      "name": "User Name",
      "email": "user@email.com", 
      "totalXP": 150,
      "correctAnswers": 25,
      "incorrectAnswers": 8,
      "level": "Pokročilý",
      "lastUpdated": 1751822221406
    }
  },
  "testHistory": {
    "user_email_com": {
      "test_id": {
        "categoryName": "General Knowledge",
        "categoryId": "general-knowledge",
        "totalQuestions": 10,
        "correctAnswers": 7,
        "incorrectAnswers": 3, 
        "successRate": 70.0,
        "completedAt": "07.01.2025 14:30",
        "timestamp": 1751822221406
      }
    }
  }
}
```

## 🏗️ Project Structure

```
src/
├── sk/uniza/fri/lingon/
│   ├── core/                    # Core quiz logic and models
│   │   ├── AbstractneZadanie.java
│   │   ├── VysledokTestu.java
│   │   └── UIKontajner.java
│   │
│   ├── db/                      # Database and Firebase management
│   │   ├── DatabaseManager.java    # H2 database operations
│   │   └── FirebaseManager.java    # Firebase cloud sync
│   │
│   ├── grafika/                 # UI components and controllers
│   │   ├── hlavny/              # Main application controllers
│   │   ├── obrazovky/           # UI screens and dialogs
│   │   └── spravcovia/          # Business logic managers
│   │
│   ├── pouzivatel/              # User management
│   │   └── Pouzivatel.java
│   │
│   └── Main.java                # Application entry point
│
├── resources/
│   └── images/
│       └── icon.png
│
├── firebase-service-account.json   # Firebase credentials (not in repo)
└── build.gradle                    # Project dependencies
```

## ⚙️ Configuration

### **Offline Mode**
The application works fully offline if Firebase is not configured:
- Local H2 database stores all data
- All features available except cloud sync
- Automatic sync when Firebase is later configured

### **Firebase Optional**
- **With Firebase**: Cloud sync, cross-device access, real-time updates
- **Without Firebase**: Full offline functionality with local storage
- **Hybrid mode**: Automatic fallback to offline when connection unavailable

## 🎯 Key Learning Outcomes

This project demonstrates:
- **Modern Java desktop development** with Swing
- **Cloud integration** with Firebase Realtime Database
- **Hybrid data storage** strategies (local + cloud)
- **Real-time synchronization** implementation
- **Clean architecture** with separation of concerns
- **Advanced OOP concepts** and design patterns
- **Error handling** and graceful degradation

## 🤝 Contributing

Contributions are welcome! Areas for improvement:
- Additional question types and categories
- Enhanced UI/UX with modern design patterns
- Advanced Firebase security rules
- Performance optimizations for large datasets
- Accessibility features and internationalization

## 📄 License

This project was created for educational purposes as part of coursework at FRI UNIZA. It demonstrates modern desktop application development with cloud integration and advanced software engineering principles.

---

<div align="center">
  <p><strong>Modern desktop quiz application with cloud-powered synchronization! 💻☁️</strong></p>
  <p><i>Take your learning anywhere - your progress is always saved in the cloud!</i></p>
</div>
