# Lingon 🌱

<div align="center">
  <img src="resources/images/icon.png" alt="Lingon Logo" width="200"/>

  <p><i>Interactive educational application for knowledge testing</i></p>

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![SQLite](https://img.shields.io/badge/SQLite-3-blue.svg)](https://www.sqlite.org/)
</div>

## 📝 Description

Lingon is an educational application created in Java using the Swing library for the graphical interface. It serves to test users' knowledge through quizzes from various categories. The project was developed as a semester work for the Informatics 2 course at FRI UNIZA, demonstrating OOP principles with special emphasis on polymorphism.

## ✨ Key Features

- 🧠 **Various Question Types:**
  - Multiple choice questions (VyberovaOtazka)
  - Text input questions (VpisovaciaOtazka)
  - Matching pair questions (ParovaciaOtazka)

- 🏆 **Point System:**
  - XP for correct answers
  - User level system
  - Statistics of correct and incorrect answers

- 🎮 **Modern Graphical Interface:**
  - Animated loading screen
  - Visually pleasing design
  - User profile with progress tracking

- 📊 **Test History:**
  - Saving test results
  - Overview of user's test history
  - Success rate statistics

## 💻 Technologies Used

- **Java Swing** for creating the user interface
- **SQLite** for the database of test history and user profiles
- **HTTP Client** for retrieving questions from online API
- **Object-oriented programming** with a focus on polymorphism
- **Design Patterns:**
  - Strategy (for answer checking strategies)
  - Template Method (in abstract classes)
  - Factory Method (for creating different types of questions)
  - Singleton (for database access)

## 🔍 Polymorphism Demonstration

The project demonstrates polymorphism in Java in the following ways:

1. **The `IZadanie` Interface** defines common behavior for all question types
2. **The `AbstractneZadanie` Abstract Class** provides basic functionality
3. **Concrete Implementations** (VyberovaOtazka, VpisovaciaOtazka, ParovaciaOtazka) override methods
4. **Custom Strategy System** through the `IOdpovedovaStrategia` interface for dynamic change of answer checking algorithm

Example of working with polymorphism in code:

```java
// Working with different types of questions through a unified interface
IZadanie zadanie = otazky.get(aktualnaOtazka);
zadanie.zobrazGrafiku(kontajner);
boolean jeSpravna = zadanie.skontrolujOdpoved(odpoved);

// Changing the answer checking strategy
VpisovaciaOtazka otazka = new VpisovaciaOtazka("How do you say 'Hello' in Slovak?", "Ahoj");
otazka.setStrategia(new ObsahujeStrategia());  // Dynamic strategy change
🚀 Running the Project
Requirements

JDK 11 or newer
Internet connection (for API questions)

Running the Application
