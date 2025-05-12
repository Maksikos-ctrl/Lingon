POLYMORFIZMUS V PROJEKTE LINGON
=============================

V mojom projekte Lingon demonštrujem polymorfizmus v jazyku Java nasledujúcimi spôsobmi:

1. ROZHRANIE IZADANIE
---------------------
Rozhranie IZadanie definuje spoločné správanie pre všetky typy otázok. Každá otázka, bez ohľadu na typ, musí implementovať tieto metódy:

- getText() - vráti text otázky
- zobrazGrafiku(UIKontajner kontajner) - zobrazí grafické rozhranie otázky
- skontrolujOdpoved(String vstup) - skontroluje odpoveď používateľa
- getTypOtazky() - vráti typ otázky

Toto rozhranie zabezpečuje, že s otázkami môžeme pracovať polymorfne, bez ohľadu na ich konkrétny typ.

2. ABSTRAKTNÁ TRIEDA ABSTRACTNEZADANIE
--------------------------------------
Abstraktná trieda AbstractneZadanie implementuje rozhranie IZadanie a poskytuje základnú funkcionalitu spoločnú pre všetky typy otázok:

- Spravuje text otázky
- Uchováva správnu odpoveď
- Poskytuje základnú implementáciu pre skontrolujOdpoved()
- Deklaruje abstraktnú metódu zobrazGrafiku(), ktorú musia implementovať podtriedy

Táto trieda zjednodušuje vytváranie nových typov otázok a zabezpečuje, že všetky otázky majú konzistentné správanie.

3. KONKRÉTNE IMPLEMENTÁCIE OTÁZOK
---------------------------------
V projekte mám tri rôzne typy otázok, každá s vlastnou implementáciou:

a) VyberovaOtazka - otázka s výberom z viacerých možností
- Zobrazuje radiobuttons pre výber odpovede
- Implementuje zobrazGrafiku() svojim špecifickým spôsobom

b) VpisovaciaOtazka - otázka s textovým vstupom
- Zobrazuje textové pole pre zadanie odpovede
- Implementuje zobrazGrafiku() svojím vlastným spôsobom

c) ParovaciaOtazka - otázka s párovaním pojmov
- Zobrazuje dropdown menu pre vytváranie párov
- Implementuje zobrazGrafiku() úplne odlišným spôsobom

Polymorfizmus sa prejavuje v tom, že všetky tieto otázky môžem používať cez jednotné rozhranie IZadanie, a pritom každá sa zobrazuje a správa svojím vlastným spôsobom. Napríklad:

```java
IZadanie zadanie = otazky.get(aktualnaOtazka);
zadanie.zobrazGrafiku(kontajner);
```

Toto je rovnaký kód pre všetky typy otázok, ale výsledné správanie sa líši podľa konkrétneho typu otázky.

4. ROZHRANIE IODPOVEDOVASTRATEGIA
---------------------------------
Implementujem návrhový vzor Strategy pomocou rozhrania IOdpovedovaStrategia, ktoré umožňuje dynamicky meniť spôsob kontroly odpovedí:

- PresnaZhodaStrategia - kontroluje presné zhody odpovedí
- ObsahujeStrategia - kontroluje, či odpoveď obsahuje očakávaný text

Toto je ďalší príklad polymorfizmu, kde rôzne implementácie stratégií poskytujú rôzne správanie, ale používajú sa rovnakým spôsobom:

```java
// Výberová otázka používa presnú zhodu
vyberovaOtazka.setStrategia(new PresnaZhodaStrategia());

// Vpisovacia otázka môže používať kontrolu obsahuje
vpisovaciaOtazka.setStrategia(new ObsahujeStrategia());

// Obidve otázky používajú rovnakú metódu na kontrolu
boolean jeSpravna1 = vyberovaOtazka.skontrolujOdpoved("odpoved");
boolean jeSpravna2 = vpisovaciaOtazka.skontrolujOdpoved("odpoved");
```

5. NACITACIA OBRAZOVKA A JEJ KOMPONENTY
---------------------------------------
V časti grafického rozhrania taktiež využívam polymorfizmus, pomocou delenia funkcionality do viacerých tried:

- NacitaciaObrazovka - hlavný komponent zobrazujúci načítaciu obrazovku
- NacitaciaAnimacie - trieda spravujúca animácie na obrazovke
- NacitaciaScreenManager - manager pre riadenie procesu načítania

Tieto triedy spolupracujú a využívajú polymorfizmus pre riadenie procesu načítania a zobrazovania animácií.

ZHRNUTIE
--------
Polymorfizmus v Môjem projekte Lingon mi umožňuje:

1. Pracovať s rôznymi typmi otázok cez jednotné rozhranie
2. Používať rôzne stratégie kontroly odpovedí
3. Jednoducho rozširovať aplikáciu o nové typy otázok a stratégie bez zmeny existujúceho kódu
4. Rozdeliť komplexnú funkcionalitu (ako načítacia obrazovka) do menších, špecializovaných tried
5. Zjednodušiť kód a zlepšiť jeho čitateľnosť a udržiavateľnosť

Toto všetko demonštruje výhody objektovo-orientovaného programovania a princíp "program to an interface, not an implementation".