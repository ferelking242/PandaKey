# Samsung Keyboard — Analyse UI & Workflow

> Référence visuelle pour la refonte UI de PandaKey (style Gboard/Samsung).
> Toutes les captures proviennent du clavier Samsung One UI en mode FR(AZERTY), thème sombre.

---

## Architecture des zones du clavier

```
┌─────────────────────────────────────────────────┐
│  SUGGESTION BAR  (mots suggestions / Coller)    │  ← zone contextuelle
├─────────────────────────────────────────────────┤
│  TOOLBAR ROW  (icônes: cursor, select, clip…)   │  ← 6 icônes + ⋯
├─────────────────────────────────────────────────┤
│  NUMBER ROW  (1 2 3 4 5 6 7 8 9 0)             │  ← optionnel
├─────────────────────────────────────────────────┤
│  KEYBOARD GRID  (AZERTY layout)                 │  ← zone principale
│  Row 1: A Z E R T Y U I O P                    │
│  Row 2: Q S D F G H J K L M                    │
│  Row 3: ⇧ W X C V B N ' ⌫                     │
├─────────────────────────────────────────────────┤
│  BOTTOM ROW  (!#1  🌐  ,  [Espace]  .  ⏎)     │  ← navigation + entrée
└─────────────────────────────────────────────────┘
```

---

## Workflow des screens

### 1. AZERTY Default — `01-azerty-default.png`
**État** : Clavier principal, mode minuscule, pas de Shift actif.

- **Suggestion bar** : vide (pas de texte tapé)
- **Toolbar** : 6 icônes grises (cursor ◇, text select ⌊⌋, clipboard ⎘, translate Ａ, keyboard ⌨, more ⋯)
- **Number row** : `1 2 3 4 5 6 7 8 9 0`
- **Lettres** : minuscules, fond `#2B2B2B`, text `#FFFFFF`
- **Shift** : flèche vers le haut, blanc outline
- **Espace** : label `Français (FR)` avec micro icon
- **Bottom row** : `!#1` (switch symbols), `🌐` (langue), `,`, espace, `.`, `⏎`

→ **Transition** : taper une lettre → passe à `06-suggestions-word.png`

---

### 2. AZERTY Uppercase (Shift) — `02-azerty-shifted.png`
**État** : Shift activé (tap sur ⇧), lettres majuscules.

- **Shift icon** : bleu foncé rempli (état actif)
- **Lettres** : `A Z E R T Y U I O P` majuscules
- **Suggestion bar** : montre suggestions de mots en majuscules
- **Coller suggestion** : `📎 Coller` si presse-papiers contient du contenu

→ **Transition** : taper une lettre → Shift se désactive automatiquement (shift single-tap)

---

### 3. AZERTY with Suggestions — `03-suggestions-expanded.png`
**État** : Lettre `F` tapée, suggestions affichées en grille 3 colonnes.

- **Suggestion bar** : `< F` (top row) avec `Fix`, `For`
- **Expanded grid** : `Fait`, `From`, `Faut`, `Faire`, `First`
- **Plus icon** `⋯` : pour plus de suggestions
- **Close icon** `<` : ferme les suggestions

→ **Transition** : sélectionner un mot → insère le mot, revient à état par défaut

---

### 4. Symbols Page 1/2 — `04-symbols-page1.png`
**État** : `!#1` pressé, première page de symboles.

- **Rangée 1** : `1 2 3 4 5 6 7 8 9 0`
- **Rangée 2** : `+ × ÷ = / _ < > [ ]`
- **Rangée 3** : `! @ # € % ^ & * ( )`
- **Rangée 4** : `1/2 - ' " : ; , ? ⌫` (bouton page)
- **Bottom** : `ABC` (retour lettres), `🌐`, `,`, `[Espace]`, `.`, `⏎`

→ **Transition** : tap `2/2` → passe à `05-symbols-page2.png`

---

### 5. Symbols Page 2/2 — `05-symbols-page2.png`
**État** : Deuxième page de symboles (currency, shapes, special).

- **Rangée 1** : `` ` ~ \ | { } $ £ ¥ ¢ ``
- **Rangée 2** : `` • · ○ ● □ ■ ♠ ♥ ♦ ♣ ``
- **Rangée 3** : `2/2 ☆ ■ ⌜ « » ¡ ¿ ⌫`
- **Bottom** : `ABC`, `🌐`, `,`, `[Espace]`, `.`, `⏎`

→ **Transition** : tap `ABC` → retourne à `01-azerty-default.png`

---

### 6. Suggestions + Presse-papiers — `06-paste-suggestion.png`
**État** : Keyboard avec suggestion "Coller" (paste) au-dessus des touches.

- **Barre** : `📎 Coller` + `✕` pour fermer
- **Layout** : identique à `01-azerty-default.png`

→ **Transition** : tap Coller → colle le contenu du presse-papiers

---

### 7. Toolbar Overflow Menu — `07-overflow-menu.png`
**État** : Icône `⋯` (more) pressée, menu déroulant.

- **Top row** : mêmes 6 icônes que toolbar (cursor, select, clipboard, translate, keyboard)
- **Bottom grid** : Paramètres ⚙, Clavier à une main 📱, Emojis 😊, Taille du clavier 🔲, Samsung Pass 🔑, **+** (ajouter)

→ **Transition** : tap une option → active la fonction correspondante

---

### 8. Toolbar Customization — `08-toolbar-customize.png`
**État** : Mode édition de la toolbar (long-press ou depuis overflow → éditer).

- **Icônes** : badges `-` rouges sur chaque icône (pour supprimer)
- **Bottom sheet** : "Touches disponibles" — liste des touches qu'on peut ajouter
- **Boutons** : `Réinitialiser` (reset) | `OK` (confirmer)

→ **Transition** : OK → sauvegarde la configuration toolbar

---

### 9. Presse-papiers (Clipboard) Panel — `09-clipboard-panel.png`
**État** : Panneau presse-papiers ouvert, remplace le clavier.

- **Header** : `⊞ Presse-papiers` + 📌 (pin) + 🗑 (supprimer)
- **Grid** : 3 colonnes de clips copiés (texte, images, URLs)
- **Mini keyboard preview** : en haut à gauche
- **Floating button** : ✕ pour fermer

→ **Transition** : tap sur un clip → insère le texte, ferme le panneau

---

## Design Tokens extraits du Samsung Keyboard

| Propriété | Valeur |
|---|---|
| Background clavier | `#2B2B2B` (dark grey) |
| Background touche | `#3D3D3D` (lighter grey) |
| Text touche | `#FFFFFF` (white) |
| Shift actif | `#4A90D9` (bleu Samsung) |
| Border radius touche | ~6dp (légèrement arrondi) |
| Espace entre touches | ~3dp |
| Toolbar icônes | `#8E8E93` (grey) |
| Suggestion bar bg | `#1C1C1E` (plus sombre) |
| Bottom row bg | `#1C1C1E` |
| Number row | `#2B2B2B` (même fond que clavier) |
| Font key | Roboto Regular ~18sp |
| Font suggestion | Roboto Regular ~16sp |
| Height row | uniforme (~46dp) |
| Toolbar height | ~44dp |
| Suggestion bar height | ~44dp |

---

## Screens index

| # | Fichier | Description |
|---|---|---|
| 01 | `01-azerty-default.png` | AZERTY minuscule, état par défaut |
| 02 | `02-azerty-shifted.png` | AZERTY majuscule (Shift actif) |
| 03 | `03-suggestions-expanded.png` | Grille de suggestions après tap |
| 04 | `04-symbols-page1.png` | Symboles page 1/2 |
| 05 | `05-symbols-page2.png` | Symboles page 2/2 |
| 06 | `06-paste-suggestion.png` | Barre "Coller" au-dessus du clavier |
| 07 | `07-overflow-menu.png` | Menu overflow (⋯) |
| 08 | `08-toolbar-customize.png` | Édition toolbar (badges rouge −) |
| 09 | `09-clipboard-panel.png` | Panneau presse-papiers complet |

---

## Notes pour l'implémentation PandaKey

1. **Rangée de chiffres** : Samsung la place toujours au-dessus des lettres, même en mode default. PandaKey devrait l'ajouter comme option activée par défaut.
2. **Toolbar** : 6 icônes max + overflow. Chaque icône = une fonction rapide (cursor, select, clipboard, translate, keyboard switch, more).
3. **Suggestions** : grille 3 colonnes au lieu d'une simple ligne. S'étend en hauteur si nécessaire.
4. **Bottom row** : `!#1` pour symbols, `🌐` pour langue, micro dans l'espace, `⏎` entrée.
5. **Thème sombre** : fond `#2B2B2B`, touches `#3D3D3D`, texte blanc. Le shift actif = bleu.
6. **PRESSE-PAPIERS** : panneau complet avec grille de clips, header avec actions (pin, delete).
