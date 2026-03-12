# Package instructions

```pswhell
 New-Item -ItemType Directory -Force -Path "..\dist" | Out-Null
 Compress-Archive -Path ".\mediahub" -DestinationPath "..\dist\mediahub.zip"
```