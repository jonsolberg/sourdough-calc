# Sourdough Calculator (Android)

A fully offline bread calculator with baker's percentages, modelled on sourdough-calculator.com.
No internet permission – everything is bundled in `app/src/main/assets/index.html`.

- Styles: Sourdough, Focaccia (olive oil), Pizza (olive oil), Enriched (butter, eggs, sugar)
- Target dough weight or total flour weight, number of loaves
- Hydration, starter %, starter hydration, salt, oil – presets, sliders and ± buttons
- Flour blends for dough and starter (7 flour types)
- Ingredients to add, totals and baker's percentages; grams or ounces
- Notes, recipe steps (with template), saved recipes, share as text
- Kitchen mode with tick-off list and step timers (uses the phone's clock app)
- Dark/light theme, keep-screen-on, English and Swedish (follows the phone language)

## Building
Push to GitHub – the workflow builds the APK and publishes `Sourdough.apk` on the `apk` branch.
Or open the folder in Android Studio and choose *Build → Build APK(s)*.
