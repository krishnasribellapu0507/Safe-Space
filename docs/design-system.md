# Design System

Safe Space uses a calm, readable visual language rather than a clinical or government-portal aesthetic.

## Color roles

Primary: soft lavender/violet.
Secondary: muted blue.
Support: soft teal.
Light background: warm off-white / pale lavender.
Dark background: deep navy-charcoal.
Emergency: restrained red, reserved for urgent actions.

The current native UI uses these roles through ThemeManager, FinalScreenUi and screen-level tokens. Gradients are atmospheric accents, not a default treatment for every card.

## Type and hierarchy

Use rounded/readable system typography with clear hierarchy: hero, screen title, section heading, body, caption, button and metric. Avoid dense paragraphs inside mobile cards.

## Shape and spacing

Cards use approximately 18–28dp corners. Buttons are rounded without becoming toy-like. Shadows/elevation remain soft. Touch targets should be at least roughly 44–48dp. Screens use generous lateral padding and safe-area insets.

## Components in the current native build

FinalScreenUi helpers, feature cards, mood options, chart views, breathing orb, settings rows, support cards, modal dialogs and the new MotionSystem form the reusable layer. Future refactors should extract the repeated bottom-navigation code into a single component before adding more destinations.
