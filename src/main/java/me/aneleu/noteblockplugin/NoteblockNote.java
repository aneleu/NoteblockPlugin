package me.aneleu.noteblockplugin;

import net.kyori.adventure.text.format.TextColor;
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

    // TODO 옥타브 / 음 / 소리크기 별 색깔 반환 만들기
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

    public TextColor getOctaveColor() {
        return null;
    }

    public TextColor getNoteColor() {
        return null;
    }

    public TextColor getVolumeColor() {
        return null;
    }

}
