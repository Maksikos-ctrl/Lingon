# Lingon 🌱

<div align="center">
  <img src="assets/lingon_logo.png" alt="Lingon Logo" width="200"/>
  
  <p><i>An interactive language learning application inspired by Duolingo</i></p>

  [![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
  [![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
</div>

## 📝 Description

Lingon is an educational language learning application developed as a semester project for computer science. The application demonstrates the application of OOP principles, especially polymorphism, and provides an interactive platform with different types of questions, a point system, and progress statistics.

## ✨ Key Features

- 🧠 **Various Question Types:**
  - Multiple choice questions
  - Text input questions
  - Matching pairs questions
  
- 🏆 **Point System:**
  - XP for correct answers
  - Different XP amounts based on question difficulty
  - Statistics for correct and incorrect answers

- 🎮 **Modern UI:**
  - Animated loading screen
  - Visually appealing design
  - User profile with progress tracking

## 💻 Technologies

- **Java Swing** for creating the user interface
- **Object-Oriented Programming** with a focus on polymorphism
- **Design Patterns:**
  - Strategy (for answer validation)
  - Delegate (for result handling)
  - Abstract Factory (for creating different question types)

## 🔍 Polymorphism Demonstration

The project serves as an excellent example of polymorphism in Java:

1. **`IZadanie` Interface** defines the contract for all question types
2. **`AbstractneZadanie` Abstract Class** provides base functionality
3. **Concrete Implementations** (VyberovaOtazka, VpisovaciaOtazka, ParovaciaOtazka) override behavior
4. **`IOdpovedovaStrategia` Interface** allows dynamically changing answer validation algorithms

Polymorphism enables handling different question types in a unified way:

```java
// Working with different question types through a single interface
IZadanie zadanie = otazky.get(aktualnaOtazka);
zadanie.zobrazGrafiku(kontajner);
```

## 🚀 Running the Project

### Prerequisites

- JDK 17 or higher
- Maven (optional)

### Clone the Repository

```bash
git clone https://github.com/your-username/lingon.git
cd lingon
```

### Build and Run

```bash
# Using Maven
mvn clean compile
mvn exec:java -Dexec.mainClass="sk.uniza.fri.lingon.Main"

# Or directly with Java
javac -d out src/main/java/sk/uniza/fri/lingon/Main.java
java -cp out sk.uniza.fri.lingon.Main
```

## 📸 Screenshots

<div align="center">
  <img src="assets/screenshot_main.png" alt="Main Screen" width="400"/>
  <img src="assets/screenshot_question.png" alt="Question Screen" width="400"/>
</div>

## 🏗️ Project Structure

```
Lingon/
├── src/
│   └── main/
│       └── java/
│           └── sk/
│               └── uniza/
│                   └── fri/
│                       └── lingon/
│                           ├── core/         # Core classes and interfaces
│                           ├── db/           # Data handling
│                           ├── grafika/      # UI components
│                           ├── GUI/          # UI interfaces
│                           ├── pouzivatel/   # User handling
│                           │   └── lekcia/   # Lessons and questions
│                           └── Main.java     # Entry point
```

## 📝 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

© 2025 [Max Chernikov] - Computer Science Semester Project
