# Safe Space Web App / PWA

Responsive HACKDAY browser experience with a working mood check-in, local private journal, breathing/grounding demo, explainable insights, Companion, support request flow, consent center and PWA install support.

Run locally with any static server, for example:

```
python -m http.server 8080 -d web-app
```

Then open http://localhost:8080.

For the full user-to-staff support demo, also run the existing backend on port 3000. The web app will sync a professional support request to the protected staff dashboard when the local API is available.

Production-only integrations such as verified provider directories, cloud AI, emergency dispatch and production authentication are intentionally not faked.