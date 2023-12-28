package me.aneleu.noteblockplugin;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Note implements ConfigurationSerializable {

    private String instrument;
    private int octave;
    private int note;
    private int volume;

    @Contract(pure = true)
    public Note(String instrument, int octave, int note, int volume) {
        this.instrument = instrument;
        this.octave = octave;
        this.note = note;
        this.volume = volume;
    }

    @SuppressWarnings("unused")
    public Note(Map<String, Object> map) {
        this.instrument = (String) map.get("instrument");
        this.octave = (int) map.get("octave");
        this.note = (int) map.get("note");
        this.volume = (int) map.get("volume");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("instrument", instrument);
        map.put("octave", octave);
        map.put("note", note);
        map.put("volume", volume);
        return map;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public int getOctave() {
        return octave;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getNoteSymbol() {
        return switch (note) {
            case 0 -> "C";
            case 1 -> "C#";
            case 2 -> "D";
            case 3 -> "D#";
            case 4 -> "E";
            case 5 -> "F";
            case 6 -> "F#";
            case 7 -> "G";
            case 8 -> "G#";
            case 9 -> "A";
            case 10 -> "A#";
            case 11 -> "B";
            default -> "ERROR";
        };
    }

    public float getPitch() {
        if (octave % 2 == 0) {
            return (float) Math.pow(2, ((note + 6) / 12.0)) / 2;
        } else {
            if (note < 6) {
                return (float) Math.pow(2, ((note + 18) / 12.0)) / 2;
            } else {
                return (float) Math.pow(2, ((note - 6) / 12.0)) / 2;
            }
        }
    }

    public Material getInstrumentBlock() {
        return switch (instrument) {
            case "piano" -> Material.GRASS_BLOCK;
            case "guitar" -> Material.WHITE_WOOL;
            case "double_bass" -> Material.OAK_PLANKS;
            case "flute" -> Material.CLAY;
            case "bell" -> Material.GOLD_BLOCK;
            case "snare_drum" -> Material.SANDSTONE;
            case "click" -> Material.GLASS;
            case "bass_drum" -> Material.STONE;
            case "chime_bell" -> Material.PACKED_ICE;
            case "xylophone" -> Material.BONE_BLOCK;
            case "iron_xylophone" -> Material.IRON_BLOCK;
            case "cow_bell" -> Material.SOUL_SAND;
            case "didgeridoo" -> Material.PUMPKIN;
            case "bit" -> Material.EMERALD_BLOCK;
            case "banjo" -> Material.HAY_BLOCK;
            case "pling" -> Material.GLOWSTONE;
            default -> Material.BLACK_CONCRETE;
        };
    }

    public String getInstrumentSound() {
        String instrumentSound = switch (instrument) {
            case "guitar" -> "block.note_block.guitar";
            case "double_bass" -> "block.note_block.bass";
            case "flute" -> "block.note_block.flute";
            case "bell" -> "block.note_block.bell";
            case "snare_drum" -> "block.note_block.snare";
            case "click" -> "block.note_block.click";
            case "bass_drum" -> "block.note_block.base";
            case "chime_bell" -> "block.note_block.chime";
            case "xylophone" -> "block.note_block.xylophone";
            case "iron_xylophone" -> "block.note_block.iron_xylophone";
            case "cow_bell" -> "block.note_block.cow_bell";
            case "didgeridoo" -> "block.note_block.didgeridoo";
            case "bit" -> "block.note_block.bit";
            case "banjo" -> "block.note_block.banjo";
            case "pling" -> "block.note_block.pling";
            default -> "block.note_block.harp";
        };

        if (octave < 3 || (octave == 3 && note < 6)) {
            instrumentSound += "_-1";
        } else if (octave > 5 || (octave == 5 && note >= 6)) {
            instrumentSound += "_1";
        }

        return instrumentSound;

    }

    public TextColor getOctaveColor() {
        return switch (octave) {
            case 1 -> TextColor.color(255, 0, 0);
            case 2 -> TextColor.color(255, 127, 0);
            case 3 -> TextColor.color(255, 255, 0);
            case 4 -> TextColor.color(0, 255, 0);
            case 5 -> TextColor.color(0, 255, 255);
            case 6 -> TextColor.color(0, 0, 255);
            case 7 -> TextColor.color(127, 0, 255);
            default -> TextColor.color(0, 0, 0);
        };
    }

    public TextColor getNoteColor() {
        return switch (note) {
            case 0 -> TextColor.color(255, 0, 0);
            case 1 -> TextColor.color(255, 127, 0);
            case 2 -> TextColor.color(255, 255, 0);
            case 3 -> TextColor.color(127, 255, 0);
            case 4 -> TextColor.color(0, 255, 0);
            case 5 -> TextColor.color(0, 255, 127);
            case 6 -> TextColor.color(0, 255, 255);
            case 7 -> TextColor.color(0, 127, 255);
            case 8 -> TextColor.color(0, 0, 255);
            case 9 -> TextColor.color(127, 0, 255);
            case 10 -> TextColor.color(255, 0, 255);
            case 11 -> TextColor.color(255, 0, 127);
            default -> null;
        };
    }

    public TextColor getVolumeColor() {
        return TextColor.color(0, 0, 0);
    }

    public Note getCopy() {
        return new Note(instrument, octave, note, volume);
    }

    public boolean equals(Note note) {
        if (note == null) return false;
        return instrument.equals(note.getInstrument()) && octave == note.getOctave() && this.note == note.getNote() && volume == note.getVolume();
    }

    public Note upNote() {
        if (note == 11 && octave < 7) {
            note = 0;
            upOctave();
        } else {
            if (octave == 7 && note == 6) {
                return this;
            }
            note++;
        }
        return this;
    }

    public Note upOctave() {
        if (octave < 7) {
            octave++;
        }
        return this;
    }

    public Note downNote() {
        if (note == 0 && octave > 1) {
            note = 11;
            downOctave();
        } else {
            if (octave == 1 && note == 6) {
                return this;
            }
            note--;
        }
        return this;
    }

    public Note downOctave() {
        if (octave > 1) {
            octave--;
        }
        return this;
    }

    public Note upVolume() {
        if (volume < 100) {
            volume++;
        }
        return this;
    }

    public Note downVolume() {
        if (volume > 0) {
            volume--;
        }
        return this;
    }

    public Note upVolume10() {
        if (volume < 90) {
            volume += 10;
        } else {
            volume = 100;
        }
        return this;
    }

    public Note downVolume10() {
        if (volume > 10) {
            volume -= 10;
        } else {
            volume = 0;
        }
        return this;
    }

    public void playSound(Player p) {
        @SuppressWarnings("PatternValidation")
        Sound sound = Sound.sound(Key.key(getInstrumentSound()), Sound.Source.MASTER, volume / 100f, getPitch());
        p.playSound(sound);
    }

}
