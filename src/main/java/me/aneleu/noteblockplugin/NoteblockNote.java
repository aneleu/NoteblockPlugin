package me.aneleu.noteblockplugin;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class NoteblockNote implements ConfigurationSerializable {

    private String instrument;
    private int octave;
    private int note;
    private int volume;

    public NoteblockNote(String instrument, int octave, int note, int volume) {
        this.instrument = instrument;
        this.octave = octave;
        this.note = note;
        this.volume = volume;
    }

    public NoteblockNote(Map<String, Object> map) {
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

    public int getOctave() {
        return octave;
    }

    public int getNote() {
        return note;
    }

    public int getVolume() {
        return volume;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public void setNote(int note) {
        this.note = note;
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

    public NoteblockNote getCopy() {
        return new NoteblockNote(instrument, octave, note, volume);
    }

    public boolean equals(NoteblockNote note) {
        if (note == null) return false;
        return instrument.equals(note.getInstrument()) && octave == note.getOctave() && this.note == note.getNote() && volume == note.getVolume();
    }

    public void upNote() {
        if (note == 11) {
            note = 0;
            upOctave();
        } else {
            note++;
        }
    }

    public void upOctave() {
        if (octave < 7) {
            octave++;
        }
    }

    public void downNote() {
        if (note == 0) {
            note = 11;
            downOctave();
        } else {
            note--;
        }
    }

    public void downOctave() {
        if (octave > 1) {
            octave--;
        }
    }

}
