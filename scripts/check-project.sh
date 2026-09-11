#!/usr/bin/env bash
set -e
for f in android/app/src/main/AndroidManifest.xml backend/server.js ai/demo_model.py counsellor-dashboard/index.html docs/architecture.md; do test -f "$f"; done
echo 'Repository structure OK'
