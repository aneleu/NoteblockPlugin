package me.aneleu.noteblockplugin;

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


    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("instrument", instrument);
        map.put("octave", octave);
        map.put("note", note);
        map.put("volume", volume);
        return map;
    }

}
