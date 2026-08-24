# PandaKey

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="70">](https://f-droid.org/packages/juloo.keyboard2/)
[![CI](https://github.com/ferelking242/PandaKey/actions/workflows/make-apk.yml/badge.svg)](https://github.com/ferelking242/PandaKey/actions/workflows/make-apk.yml)

Un clavier virtuel Android **léger, rapide et respectueux de la vie privée** —
basé sur [Unexpected Keyboard](https://github.com/Julow/Unexpected-Keyboard),
en cours de modernisation (**migration Kotlin** + interface inspirée de
Gboard / Samsung Keyboard).

## ✨ Pourquoi PandaKey ?

- **Swipe vers les coins** : chaque touche cache jusqu'à 8 caractères
  supplémentaires accessibles d'un glissement — le meilleur rapport
  caractères/cm² d'Android.
- **Pensé pour les développeurs** : toutes les touches Ctrl, Esc, Tab, flèches,
  F1–F12, accents combinants… parfait pour Termux, mais agréable au quotidien.
- **91 dispositions clavier** dans plus de 30 systèmes d'écriture (latin,
  cyrillique, arabe, devanagari, hangul, hébreu…), ou créez la vôtre en XML.
- **Modificateurs puissants** : Shift, Fn, Compose (séquences X11), accents
  morts — tout est personnalisable.
- **Zéro pub, zéro tracking, zéro permission réseau inutile.** Open source (GPLv3).

## 📱 Installation

| Source | Lien |
|---|---|
| F-Droid | [juloo.keyboard2](https://f-droid.org/packages/juloo.keyboard2/) |
| Google Play | [juloo.keyboard2](https://play.google.com/store/apps/details?id=juloo.keyboard2) |
| CI (APK debug) | [Artifacts GitHub Actions](https://github.com/ferelking242/PandaKey/actions/workflows/make-apk.yml) |

## 🛠️ Contribuer

- **Compiler** : voir [CONTRIBUTING.md](CONTRIBUTING.md) (JDK 17, SDK Android 36,
  `./gradlew assembleDebug` — environnement Nix prêt à l'emploi via `shell.nix`).
- **Ajouter un layout** : [doc/Custom-layouts.md](doc/Custom-layouts.md) +
  [l'éditeur web](https://unexpected-keyboard-layout-editor.lixquid.com/).
- **Traduire** : [Weblate](https://hosted.weblate.org/engage/unexpected-keyboard/).
- **Migration Kotlin** : le projet migre progressivement de Java vers Kotlin,
  vague par vague, sans réécriture massive (règle : tout nouveau fichier = Kotlin).

### Architecture rapide

```
srcs/
├── juloo.keyboard2/        # Code de l'app (Java → Kotlin progressif)
│   ├── Keyboard2.java      # Service IME principal
│   ├── Pointers.java       # Moteur multi-touch & gestes
│   ├── Keyboard2View.java  # Rendu custom
│   └── prefs/              # Préférences (migré en Kotlin ✅)
├── layouts/                # 91 layouts XML
└── compose/                # Séquences Shift/Fn/Compose (JSON → code généré)
```

## 🙏 Remerciements

- [Julow](https://github.com/Julow) et toute la communauté
  [Unexpected Keyboard](https://github.com/Julow/Unexpected-Keyboard) pour le
  projet d'origine.
- La [fondation NLnet](https://nlnet.nl/) qui a financé le correcteur
  orthographique.
