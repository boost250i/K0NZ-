# KONZ 0.2 — Android 10 touchscreen DJ prototype

An original red/blue sci-fi dark-theme dual-deck app, based on the user's KONZ design concept. Native Java Android project, API 29 minimum, no external runtime libraries. Includes load audio via Android picker, simultaneous dual playback, play/pause, seeking, saved cue, four-beat *approximate* loop, manual BPM, approximate tempo matching labeled SYNC, pitch +/- 0.5%, and equal-power crossfader. Landscape display; optimized as a first phone prototype.

## Important limitations
- **Not a full VirtualDJ equivalent.** No AI stems, stems prep, automatic BPM analysis, waveform analysis, real beat-grid alignment, keylock-quality time stretch, effects, library database, or headphone cue routing.
- SYNC only matches *manually entered BPM numbers*, not beat phase. Loop timing is based on those numbers, and is not sample accurate.
- Decorative waveform is synthesized, **not** audio analysis. MediaPlayer latency and independent clocks make this unsuitable for live performance until the audio engine is replaced.
- Speed and pitch preservation depend on Android MediaPlayer PlaybackParams; test on device.
- Does not bundle copyrighted Star Wars logos, characters, or imagery.

## Building
Upload the folder contents (including `.github`) to a GitHub repository. In Actions, run `Build KONZ APK`. Download the `KONZ-debug-apk` artifact, unzip and install on Android 10. Debug signing is for personal testing, not Play Store release. Not yet compiled or device-tested.

## Engineering roadmap
1. Replace MediaPlayer with one PCM-based shared low-latency engine (Oboe + decoder + time stretch) and accurate sample transport.
2. Add real waveform peak caching and BPM/beat-grid analysis, quantized cues and loops.
3. Add beat-phase sync, effects and 3-band EQ, track library and offline analysis/prep.
4. Investigate device-capable live/offline neural stems with a quantized model and thermal/memory benchmarks on Galaxy S9+.
5. USB MIDI and independent cue/master audio support, with DDJ-SB3 compatibility testing.
