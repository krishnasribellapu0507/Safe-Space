# Motion System

Safe Space motion communicates continuity and emotional softness. It should never compete with content or delay urgent actions.

## Timing

- Press feedback: 120–180 ms.
- Standard screen/content entrance: about 280–320 ms.
- Chart/interpolation updates: about 480 ms.
- Ambient breathing/atmospheric motion may be longer and looping.
- Emergency/support actions should use direct transitions without decorative delay.

## Native implementation

MotionSystem centralizes reduced-motion awareness, press scale feedback and content entrance animation. Existing screen transitions use a decelerated horizontal continuity transition. RelaxView disables its continuous breathing-orb scale animation when reduced motion is enabled.

## Principles

Use opacity plus small translation or scale changes. Avoid aggressive bounce, flashing and celebratory confetti in sensitive flows. Cancel looping animation when views detach. Animations must never consume the only tap target or block an urgent support action.

## Accessibility

The Settings > Accessibility control lets the demo user enable reduced motion. New motion should check MotionSystem.reducedMotion(context). Reduced motion should prefer static state changes or simple fades.
