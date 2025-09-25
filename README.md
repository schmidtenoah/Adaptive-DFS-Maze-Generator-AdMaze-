# AdMaze – Adaptive DFS Maze Generator

AdMaze ist ein Java-Projekt zur Generierung von Labyrinthen, basierend auf dem klassischen DFS-Backtracker-Algorithmus.  
Es erweitert den Standardansatz um zwei zentrale Innovationen:

1. **Anti-Persistenz-Heuristik (`beta`, `k`)**  
   Verhindert monotone Korridore, indem kürzlich genutzte Richtungen weniger wahrscheinlich gewählt werden (Softmax-Gewichtung).

2. **Braiding (`pBraid`)**  
   Öffnet kontrolliert zusätzliche Verbindungen, um Sackgassen zu reduzieren und "Loops" im Maze zu erzeugen (– ohne 2×2-Räume).

## Features
- Parametrisierbare Maze-Größe (W, H)
- Start- und Zielmarkierung (S/E)
- Optionale Braiding-Funktionalität
- ASCII-Visualisierung im Terminal
- Laufzeitmessung (nanosecond precision)

## Roadmap
- Benchmarking: Laufzeit mit Algorithmen wie DFS, Prim, Kruskal... vergleichen
- Visualizer: Zeigen, wie das Maze entsteht
- Exports: PNGs des Mazes
- Algorithmus-Erweitern: Dead-End-Braiding implementieren
- Vollständige Theorie-Dokumentation schreiben / commiten
- Kommentieren
- Tests: Unit-Tests