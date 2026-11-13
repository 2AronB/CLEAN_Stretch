"""Generate the app's voice clips with Kokoro-82M (voice af_heart), D-026.

Usage (on the PC, from the repo root):
    <kokoro venv>/Scripts/python.exe -I tools/voice/generate.py <model_dir>

<model_dir> holds kokoro-v1.0.onnx and voices-v1.0.bin (kokoro-onnx release files).
Reads tools/voice/phrases.txt and writes app/src/main/res/raw/<clip>.ogg (Ogg Vorbis, mono).
Clip names must match VoiceLines.clipName in the app; VoiceLinesTest checks this.
"""
import re
import sys
from pathlib import Path

import numpy as np
import soundfile as sf
from kokoro_onnx import Kokoro

VOICE = "af_heart"
SPEED = 0.95
ROOT = Path(__file__).resolve().parents[2]


def clip_name(text: str) -> str:
    return "voice_" + re.sub(r"[^a-z0-9]+", "_", text.lower()).strip("_")


def trim(audio: np.ndarray, sr: int, threshold: float = 0.01, pad_s: float = 0.08) -> np.ndarray:
    loud = np.where(np.abs(audio) > threshold)[0]
    if loud.size == 0:
        return audio
    pad = int(sr * pad_s)
    return audio[max(0, loud[0] - pad): loud[-1] + pad]


def main() -> None:
    model_dir = Path(sys.argv[1])
    kokoro = Kokoro(str(model_dir / "kokoro-v1.0.onnx"), str(model_dir / "voices-v1.0.bin"))
    out = ROOT / "app/src/main/res/raw"
    out.mkdir(parents=True, exist_ok=True)
    phrases = [p.strip() for p in (ROOT / "tools/voice/phrases.txt").read_text(encoding="utf-8").splitlines() if p.strip()]
    wanted = {clip_name(p) for p in phrases}
    for old in out.glob("voice_*.ogg"):
        if old.stem not in wanted:
            old.unlink()
            print("removed stale", old.name)
    # Existing clips are kept (they've been listened to); pass --all to remake every clip.
    remake = "--all" in sys.argv[2:]
    for text in phrases:
        path = out / f"{clip_name(text)}.ogg"
        if path.exists() and not remake:
            continue
        audio, sr = kokoro.create(text, voice=VOICE, speed=SPEED, lang="en-us")
        sf.write(path, trim(audio, sr), sr, format="OGG", subtype="VORBIS")
        print("wrote", path.name)


if __name__ == "__main__":
    main()
