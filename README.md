
# PCCCS495 – Term II Project

## Project Title - Code-Flow: An MVC-Based Engineering Text Editor

---

## Problem Statement (max 150 words)
###The primary technical challenge of this project is implementing a robust, memory-efficient state management system for a text editor from scratch. While many applications rely on heavy, full-document snapshots for undo operations, this project focuses on implementing the Command Design Pattern to manage granular edit history. This ensures the application remains responsive and memory-efficient by storing only specific delta changes (insertions/deletions), while maintaining a strict MVC separation to prevent logic leakage between the data layer and the Swing interface.
---

## Target User
###Engineering students and developers requiring a lightweight, distraction-free environment for local file manipulation and code drafting.
---

## Core Features
- File Management: Support for creating, opening, and saving .txt and .java files using JFileChooser.

-Efficient I/O: Reliable data handling via standard Java I/O streams (BufferedReader and BufferedWriter).

- Command-Based Undo/Redo: Infinite state reversal using the Command Design Pattern and Stacks.

- Live Metadata Analytics: A real-time status bar displaying word count, character count, and line/column position.

- UI Customization: A theme-switching engine for Light/Dark modes utilizing the Singleton Pattern.

 
---

## OOP Concepts Used

- Abstraction: Implementation of a Command interface to define standard behaviors for all editor actions.
- Inheritance:  Extending JFrame and JPanel to build custom Swing components.
- Polymorphism:  Using a list of Command objects to execute different logic (insertion/deletion) through a single method call.
- Exception Handling:  Robust try-catch blocks for IOException during file operations and NullPointerException for empty buffers.
- Collections / Threads:  Using java.util.Stack for undo/redo history and SwingWorker threads to keep the UI responsive during I/O and analytics updates.

---

## Proposed Architecture Description
###The project strictly follows the Model-View-Controller (MVC) architecture. The Model manages the text buffer and file persistence; the View handles the Java Swing GUI; and the Controller processes user events. The Singleton Pattern ensures a single point of access for application-wide theme settings, providing a synchronized UI state across all components.
---

## How to Run

---

## Git Discipline Notes
Minimum 10 meaningful commits required.
