# WordCounter Viber 📊

Android programėlė, skirta analizuoti tekstą ir realiu laiku apskaičiuoti sakinių, žodžių, rašybos ženklų bei skaičių kiekius su skaičiavimų istorija ir pavyzdžių generatoriumi.

---

## 📋 Turinys

- [Apie projektą](#-apie-projektą)
- [Funkcionalumas](#-funkcionalumas)
- [Skaičiavimo taisyklės ir formulė](#-skaičiavimo-taisyklės-ir-formulė)
- [Pavyzdžiai](#-pavyzdžiai)
- [Projekto architektūra](#-projekto-architektūra)
- [Technologijų rinkinys](#-technologijų-rinkinys)
- [Reikalavimai ir paleidimas](#-reikalavimai-ir-paleidimas)
  - [Reikalavimai](#reikalavimai)
  - [Kompiliavimas ir testavimas](#kompiliavimas-ir-testavimas)
  - [Paleidimas įrenginyje / emuliatoriuje](#paleidimas-įrenginyje--emuliatoriuje)
- [Lokalizacija (i18n)](#-lokalizacija-i18n)

---

## 📱 Apie projektą

**WordCounter Viber** – tai šiuolaikiška Android programėlė, sukurta naudojant „Material Design 3“ principus ir grynosios Java (POJO) architektūros tekstų analizatoriaus variklį. Programėlė leidžia vartotojui įvesti norimą tekstą arba sugeneruoti testinį pavyzdį, atlikti analizę, matyti detalias metrikas atskirose kortelėse bei peržiūrėti atliktų skaičiavimų istoriją.

---

## ✨ Funkcionalumas

- **4 pagrindinių parametrų analizė**:
  - Sakinių skaičius
  - Žodžių skaičius (atmetant skaičius)
  - Rašybos ženklų skaičius (įskaitant tarpus ir skyrybą)
  - Skaičių kiekis (sveikieji, dešimtainiai ir neigiami skaičiai)
- **Santraukos formulė**:
  - Rezultatų atvaizdavimas formatu: `„Tekstas“ => Sakiniai, Žodžiai, Rašybos ženklai, Skaičiai`.
- **Testinių sakinių generatorius**:
  - Mygtukas „Generuoti pavyzdį“ paeiliui pateikia paruoštus testinius sakinius su įvairiomis skyrybos ir skaitinėmis kombinacijomis.
- **Skaičiavimų istorija**:
  - Kiekvienas atliktas skaičiavimas išsaugomas interaktyviame sąraše (`RecyclerView`) kartu su laiko žyma.
  - Galimybė vienu paspaudimu išvalyti istoriją („Valyti istoriją“).
- **Įvesties validacija**:
  - Tikrinama tuščia įvestis ir pranešama vartotojui per `TextInputLayout` klaidą bei `Toast` pranešimą.
- **Greitas išvalymas**:
  - Mygtukas „Išvalyti“ ištrina įvesties tekstą ir atstato rezultatų skaitiklius į nulį.
- **Daugiakalbystė**:
  - Pilnas lietuvių (numatytoji) ir anglų kalbų palaikymas.

---

## 📐 Skaičiavimo taisyklės ir formulė

Metrikų skaičiavimas atliekamas nepriklausomoje `TextMetricsCalculator` klasėje:

1. **Sakiniai**:
   - Tekstas skaidomas pagal skyrybos ženklus `.` (taškas), `!` (šauktukas), `?` (klaustukas), naudojant reguliarųjį reiškinį `[.!?]+(?![0-9])`.
   - Vengiama klaidingo sakinio skaidymo dešimtainiuose skaičiuose (pvz., `3.14`).
   - Skaičiuojami tik tie segmentai, kuriuose yra raidžių arba skaitmenų.
2. **Žodžiai**:
   - Tekstas skaidomas tarpais (`\s+`).
   - Iš žetono pašalinami kraštiniai skyrybos ženklai.
   - Žodžiu laikomas žetonas, turintis bent vieną raidę (palaikomos lietuviškos Unicode raidės `ą, č, ę, ė, į, š, ų, ū, ž`).
   - Skaitiniai žetonai prie žodžių **nepridedami**.
3. **Rašybos ženklai**:
   - Visi simboliai, esantys tekste (simbolių ilgis `text.length()`, įskaitant tarpus, skyrybos ženklus ir spec. simbolius).
4. **Skaičiai**:
   - Atpažįstami sveikieji, dešimtainiai (tiek su kableliu `10,5`, tiek su tašku `3.14`) bei neigiami skaičiai (pvz., `-5`).

---

## 🔍 Pavyzdžiai

| Įvesties tekstas | Sakiniai | Žodžiai | Rašybos ženklai | Skaičiai | Formulės santrauka |
| :--- | :---: | :---: | :---: | :---: | :--- |
| `Labas rytas` | 1 | 2 | 11 | 0 | `„Labas rytas“ => 1, 2, 11, 0` |
| `Rugsėjo 1 prasidėjo karas. Liūdna` | 2 | 4 | 33 | 1 | `„Rugsėjo 1 prasidėjo karas. Liūdna“ => 2, 4, 33, 1` |
| `Pi reikšmė yra 3.14. Šiandien šalna: -5 laipsniai!` | 2 | 6 | 49 | 2 | `„Pi reikšmė yra 3.14...“ => 2, 6, 49, 2` |

---

## 🏗️ Projekto architektūra

Projektas struktūrizuotas pagal švarios architektūros principus, atskiriant verslo logiką nuo Android karkaso:

```
app/src/
├── main/
│   ├── java/com/example/wordcounter/
│   │   ├── MainActivity.java                # Pagrindinis UI langas, valdikliai ir įvykių klausytojai
│   │   ├── analyzer/
│   │   │   ├── TextMetrics.java             # Nekintamas (immutable) metrikų duomenų modelis
│   │   │   ├── TextMetricsCalculator.java   # Grynosios logikos analizatorius (be Android priklausomybių)
│   │   │   └── TestSentenceGenerator.java   # Pavyzdinių sakinių generatorius
│   │   ├── model/
│   │   │   └── CalculationRecord.java       # Istorijos įrašo modelis (tekstas, metrikos, laikas)
│   │   └── ui/
│   │       └── HistoryAdapter.java          # RecyclerView adapteris istorijos atvaizdavimui
│   └── res/
│       ├── layout/
│       │   ├── activity_main.xml            # Pagrindinis ekranas (kortelės, mygtukai, tinklelis)
│       │   └── item_calculation_history.xml# Istorijos elemento maketas
│       ├── values/                          # Lietuviški resursai, spalvos, temos
│       └── values-en/                       # Angliški tekstiniai resursai
└── test/
    └── java/com/example/wordcounter/
        └── TextMetricsCalculatorTest.java   # JUnit 4 vienetų testai analizei tikrinti
```

---

## 🛠️ Technologijų rinkinys

- **Programavimo kalba**: Java (Java 11)
- **Minimali palaikoma Android versija**: Android 7.0 (API 24 - Nougat)
- **Tikslinė / Kompiliavimo versija**: Android API 37
- **UI komponentai**:
  - Google Material Components (`com.google.android.material:material:1.10.0`)
  - AndroidX ConstraintLayout (`2.1.4`)
  - AndroidX RecyclerView (`1.3.2`)
  - AndroidX CoordinatorLayout & NestedScrollView
- **Testavimas**:
  - JUnit 4 (`junit:junit:4.13.2`)
  - Espresso Core (`androidx.test.espresso:espresso-core:3.5.1`)
- **Gradle**: Gradle 9+ su Gradle Kotlin DSL (`build.gradle.kts`) ir Version Catalog (`libs.versions.toml`)

---

## 🚀 Reikalavimai ir paleidimas

### Reikalavimai
- **JDK**: Java Development Kit 17 arba naujesnė versija
- **Android Studio**: Android Studio Ladybug (arba naujesnė)
- **Android SDK**: API Platform 24–37

### Kompiliavimas ir testavimas

Projektą galima kompiliuoti ir testuoti per komandinę eilutę arba Android Studio aplinką.

#### Vienetų testų vykdymas (Unit Tests)
```bash
# Windows PowerShell
.\gradlew.bat test

# macOS / Linux
./gradlew test
```

#### Debug APK kūrimas
```bash
# Windows PowerShell
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```
Sukurtas `.apk` failas bus patalpintas kataloge:
`app/build/outputs/apk/debug/app-debug.apk`

### Paleidimas įrenginyje / emuliatoriuje

1. Prijunkite Android įrenginį per USB su įjungtu *USB Debugging* arba paleiskite Android Virtual Device (AVD).
2. Įdiekite ir paleiskite:
   ```bash
   .\gradlew.bat installDebug
   ```
   Arba tiesiog paspauskite **Run (Shift + F10)** Android Studio aplinkoje.

---

## 🌐 Lokalizacija (i18n)

Programėlė automatiškai prisitaiko prie įrenginio kalbos:
- **Lietuvių kalba (`res/values/strings.xml`)** – numatytoji sistemos kalba.
- **Anglų kalba (`res/values-en/strings.xml`)** – taikoma, kai įrenginio kalba nustatyta į anglų ar kitą palaikomą kalbą.
