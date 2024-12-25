# Semestrální práce na předmět PAA

Aplikace vykresluje LaTeXové vzorce na základě zadaného vstupu, umožňuje také uživateli ukládat starší dotazy a zobrazovat je.

Aplikace ve stylu Flashcards, kde uživatel zadá vzorec a aplikace mu ho vykreslí. Uživatel si může vzorec uložit a zobrazit si ho později.

Aplikace jse sestavená a otestovaná na virtuálním stroji **Nexus S API 26**.

## Pro užiatele

## Jak funguje aplikace?

Aplikace ukládá vzorce do souborů v interní paměti zařízení. Na každý vzorec se vytvoří jeden `.json` soubor, který obsahuje název vzorce a jeho latex zápis, a jeden `.svg` soubor s vyrenderovaným matematickým zápisem.

Do json souboru se serializuje následující struktura:

```Kotlin
data class EquationData(
    val equation: String,
    val label: String,
    val description: String,
    val scale: Int,
    val svgFileName: String,
    val thisFileName: String,
)
```