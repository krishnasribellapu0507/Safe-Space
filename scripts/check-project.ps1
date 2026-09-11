$ErrorActionPreference='Stop'
Write-Host 'Checking Safe Space repository...'
$required=@('android/app/src/main/AndroidManifest.xml','backend/server.js','ai/demo_model.py','counsellor-dashboard/index.html','docs/architecture.md')
foreach($f in $required){if(-not(Test-Path $f)){throw "Missing $f"}}
Write-Host 'Repository structure OK'
