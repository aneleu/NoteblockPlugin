package me.aneleu.noteblockplugin;

import me.aneleu.noteblockplugin.utils.NoteblockUtil;
import me.aneleu.noteblockplugin.utils.Pair;
import me.aneleu.noteblockplugin.utils.QuadFunction;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.UnaryOperator;

public class SheetMusic {

    static final Transformation VERTICAL_LINE_TRANSFORMATION = new Transformation(new Vector3f(-0.5F, 0.001F, -0.5F), new AxisAngle4f(), new Vector3f(0.02F, 0, 1), new AxisAngle4f());
    static final Transformation HORIZONTAL_LINE_TRANSFORMATION = new Transformation(new Vector3f(-0.5F, 0.001F, -0.5F), new AxisAngle4f(), new Vector3f(0.02F, 0, 0.98F), new AxisAngle4f());
    static final Transformation TEXT_TRANSFORMATION = new Transformation(new Vector3f(0.48F, -0.92F, 0.001F), new AxisAngle4f(), new Vector3f(1.5F, 1.5F, 1), new AxisAngle4f());
    static final Transformation SELECT_TRANSFORMATION = new Transformation(new Vector3f(0, 0.001F, 0), new AxisAngle4f(), new Vector3f(1, 0, 1), new AxisAngle4f());

    static final BlockData BLACK_CONCRETE_BLOCKDATA = Bukkit.createBlockData(Material.BLACK_CONCRETE);
    static final BlockData GRAY_CONCRETE_BLOCKDATA = Bukkit.createBlockData(Material.GRAY_CONCRETE);
    static final BlockData LIGHT_GRAY_CONCRETE_BLOCKDATA = Bukkit.createBlockData(Material.LIGHT_GRAY_CONCRETE);
    static final BlockData SELECT_BLOCKDATA = Bukkit.createBlockData(Material.LIGHT_BLUE_STAINED_GLASS);

    static final int INITIAL_LENGTH = 64;
    static final int INITIAL_LINE = 10;
    static final int LENGTH_EXTEND = 32;
    static final int LINE_EXTEND = 3;
    static final int LENGTH_EXTEND_TRIGGER = 32;
    static final int LINE_EXTEND_TRIGGER = 2;
    static final int LENGTH_REDUCE_TRIGGER = 64;
    static final int LINE_REDUCE_TRIGGER = 5;
    static final int LENGTH_REDUCE_LIMIT = 32;
    static final int LINE_REDUCE_LIMIT = 3;

    NoteblockPlugin plugin;

    String name;
    int x;
    int y;
    int z;
    int length;
    int line;

    World world;
    Location location;

    // undo / redo
    List<Pair<List<Pair<int[], NoteblockNote>>, List<Pair<int[], NoteblockNote>>>> record = new ArrayList<>();
    int recordIdx = -1; // undo / redo 현재 위치

    // copy / paste
    NoteblockNote[][] copiedNotes;

    // pos1 / pos2
    int[] pos1 = null;
    int[] pos2 = null;

    public SheetMusic(String name, int x, int y, int z) {
        this(name, x, y, z, 1, 1);
        createBox(0, 0);
        extendLength(INITIAL_LENGTH - 1);
        extendLine(INITIAL_LINE - 1);
    }

    public SheetMusic(String name, int x, int y, int z, int length, int line) {
        this.plugin = NoteblockPlugin.plugin;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.length = length;
        this.line = line;
        this.world = Bukkit.getWorld("world");
        this.location = new Location(world, 0, this.y, 0);

        unhighlight();

    }

    public String getName() {
        return name;
    }

    private void addEntityUUID(@NotNull Entity entity, String tag) {
        addEntityUUID(entity.getUniqueId().toString(), tag);
    }

    private void addEntityUUID(String uuid, String tag) {
        plugin.getConfig().set("sheet." + name + ".entity." + tag, uuid);
    }

    private void removeUniqueIdEntity(String uuid) {
        if (uuid == null) return;
        Entity entity = Bukkit.getEntity(UUID.fromString(uuid));
        if (entity == null) return;
        entity.remove();
    }

    private void createBox(int a, int b) {
        location.set(a + x + 0.5, y, b + z + 0.5);

        world.getBlockAt(location).setType(Material.WHITE_CONCRETE);

        Interaction interaction = (Interaction) world.spawnEntity(location, EntityType.INTERACTION);
        String interactionUUID = interaction.getUniqueId().toString();
        plugin.getConfig().set("sheet." + name + ".interaction." + interactionUUID, List.of(a, b));

        addEntityUUID(interactionUUID, a + "|" + b + "|interaction");

        location.add(0, 1, 0);
        for (int i = 0; i < 2; i++) {
            BlockDisplay outline = (BlockDisplay) world.spawnEntity(location, EntityType.BLOCK_DISPLAY);
            Location outline_loc = outline.getLocation();
            outline_loc.setYaw(90 * i);
            outline.teleport(outline_loc);
            if (i == 0) {
                if (a % 16 == 0) {
                    outline.setBlock(BLACK_CONCRETE_BLOCKDATA);
                } else if (a % 4 == 0) {
                    outline.setBlock(GRAY_CONCRETE_BLOCKDATA);
                } else {
                    outline.setBlock(LIGHT_GRAY_CONCRETE_BLOCKDATA);
                }
                outline.setTransformation(VERTICAL_LINE_TRANSFORMATION);
            } else {
                outline.setBlock(LIGHT_GRAY_CONCRETE_BLOCKDATA);
                outline.setTransformation(HORIZONTAL_LINE_TRANSFORMATION);
            }

            addEntityUUID(outline, a + "|" + b + "|outline" + i);
        }

    }


    private void extendLength(int n) {
        int start = length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < line; j++) {
                createBox(start + i, j);
            }
        }
        length += n;
        plugin.getConfig().set("sheet." + name + ".length", length);
    }

    private void extendLine(int n) {
        int start = line;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < length; j++) {
                createBox(j, start + i);
            }
        }
        line += n;
        plugin.getConfig().set("sheet." + name + ".line", line);
    }

    private void removeBox(int a, int b) {
        world.getBlockAt(x + a, y, z + b).setType(Material.AIR);
        String interactionPath = "sheet." + name + ".entity." + a + "|" + b + "|interaction";
        String outline0Path = "sheet." + name + ".entity." + a + "|" + b + "|outline0";
        String outline1Path = "sheet." + name + ".entity." + a + "|" + b + "|outline1";
        String interactionUUID = plugin.getConfig().getString(interactionPath);
        String outline0UUID = plugin.getConfig().getString(outline0Path);
        String outline1UUID = plugin.getConfig().getString(outline1Path);
        plugin.getConfig().set("sheet." + name + ".interaction." + interactionUUID, null);
        plugin.getConfig().set(interactionPath, null);
        plugin.getConfig().set(outline0Path, null);
        plugin.getConfig().set(outline1Path, null);
        removeUniqueIdEntity(interactionUUID);
        removeUniqueIdEntity(outline0UUID);
        removeUniqueIdEntity(outline1UUID);
    }

    private void reduceLength(int n) {
        for (int i = length - n; i < length; i++) {
            for (int j = 0; j < line; j++) {
                removeBox(i, j);
            }
        }
        length -= n;
        plugin.getConfig().set("sheet." + name + ".length", length);
    }

    private void reduceLine(int n) {
        for (int j = line - n; j < line; j++) {
            for (int i = 0; i < length; i++) {
                removeBox(i, j);
            }
        }
        line -= n;
        plugin.getConfig().set("sheet." + name + ".line", line);
    }

    private void reduce() {

        int maxLength = 0;
        int maxLine = 0;

        ConfigurationSection textDisplaySection1 = plugin.getConfig().getConfigurationSection("sheet." + name + ".note");
        if (textDisplaySection1 == null) {
            return;
        }
        for (String i : textDisplaySection1.getKeys(false)) {
            ConfigurationSection textDisplaySection2 = textDisplaySection1.getConfigurationSection(i);
            if (textDisplaySection2 == null) {
                continue;
            }
            Set<String> keys = textDisplaySection2.getKeys(false);
            if (keys.isEmpty()) {
                continue;
            }
            int integerI = Integer.parseInt(i);
            if (integerI > maxLength) {
                maxLength = integerI;
            }
            for (String j : textDisplaySection2.getKeys(false)) {
                int integerJ = Integer.parseInt(j);
                if (integerJ > maxLine) {
                    maxLine = integerJ;
                }
            }
        }

        if (length - maxLength > LENGTH_REDUCE_TRIGGER) {
            if (maxLength + 1 + LENGTH_REDUCE_LIMIT >= INITIAL_LENGTH) {
                reduceLength(length - maxLength - 1 - LENGTH_REDUCE_LIMIT);
            } else {
                reduceLength(length - INITIAL_LENGTH);
            }
        }

        if (line - maxLine > LINE_REDUCE_TRIGGER) {
            if (maxLine + 1 + LINE_REDUCE_LIMIT >= INITIAL_LINE) {
                reduceLine(line - maxLine - 1 - LINE_REDUCE_LIMIT);
            } else {
                reduceLine(line - INITIAL_LINE);
            }

        }

    }

    public void setNote(int a, int b, @NotNull NoteblockNote note, boolean rec) {

        NoteblockNote previousNote = plugin.getConfig().getSerializable("sheet." + name + ".note." + a + "." + b + ".note", NoteblockNote.class);

        // 이미 해당 위치에 note와 같은 노트가 있는 경우 실행 취소
        if (note.equals(previousNote)) {
            return;
        }

        // undo / redo 를 위한 이전 상태 / 이후 상태 저장
        if (rec) {
            if (record.size() != recordIdx + 1) {
                record.subList(recordIdx + 1, record.size()).clear();
            }

            int[] coordinate = {a, b};
            List<Pair<int[], NoteblockNote>> previousData = List.of(new Pair<>(coordinate, previousNote));
            List<Pair<int[], NoteblockNote>> modifiedData = List.of(new Pair<>(coordinate, note));
            record.add(new Pair<>(previousData, modifiedData));
            recordIdx++;
        }

        // 에디터 연장
        if (a >= length - LENGTH_EXTEND_TRIGGER) {
            extendLength(LENGTH_EXTEND);
        }
        if (b >= line - LINE_EXTEND_TRIGGER) {
            extendLine(LINE_EXTEND);
        }

        world.getBlockAt(x + a, y, z + b).setType(note.getInstrumentBlock());

        String displayUUID = plugin.getConfig().getString("sheet." + name + ".note." + a + "." + b + ".display");
        if (displayUUID != null) {
            Entity entity = Bukkit.getEntity(UUID.fromString(displayUUID));
            if (entity != null) {
                entity.remove();
            }
        }

        location.set(x + a, y + 1, z + b);
        TextDisplay textDisplay = (TextDisplay) world.spawnEntity(location, EntityType.TEXT_DISPLAY);
        Component component = Component.text(note.getOctave() + " ", note.getOctaveColor())
                .append(Component.text(note.getNoteSymbol() + "\n", note.getNoteColor()))
                .append(Component.text(note.getVolume(), note.getVolumeColor()));
        textDisplay.text(component);
        textDisplay.setRotation(0, -90);
        textDisplay.setTransformation(TEXT_TRANSFORMATION);
        textDisplay.setDefaultBackground(false);
        textDisplay.setBackgroundColor(Color.fromARGB(0));

        plugin.getConfig().set("sheet." + name + ".note." + a + "." + b + ".display", textDisplay.getUniqueId().toString());
        plugin.getConfig().set("sheet." + name + ".note." + a + "." + b + ".note", note);

    }


    public void deleteNote(int a, int b, boolean reduce, boolean rec) { // reduce: [에디터를 축소 할수 있다면 축소 시키는 작업]을 수행할 것인지.
        String displayUUID = plugin.getConfig().getString("sheet." + name + ".note." + a + "." + b + ".display");
        if (displayUUID != null) {
            Entity entity = Bukkit.getEntity(UUID.fromString(displayUUID));
            if (entity != null) {
                entity.remove();
            }

            // undo / redo 를 위한 이전 상태 / 이후 상태 저장
            if (rec) {
                if (record.size() != recordIdx + 1) {
                    record.subList(recordIdx + 1, record.size()).clear();
                }

                int[] coordinate = {a, b};
                NoteblockNote previousNote = plugin.getConfig().getSerializable("sheet." + name + ".note." + a + "." + b + ".note", NoteblockNote.class);
                List<Pair<int[], NoteblockNote>> previousData = List.of(new Pair<>(coordinate, previousNote));
                List<Pair<int[], NoteblockNote>> modifiedData = List.of(new Pair<>(coordinate, null));

                record.add(new Pair<>(previousData, modifiedData));
                recordIdx++;
            }

            plugin.getConfig().set("sheet." + name + ".note." + a + "." + b, null);
            world.getBlockAt(x + a, y, z + b).setType(Material.WHITE_CONCRETE);

            if (reduce) reduce();
        }

    }

    public void undo() {
        if (recordIdx >= 0) {
            for (Pair<int[], NoteblockNote> data : record.get(recordIdx).first()) {
                int[] coordinate = data.first();
                NoteblockNote note = data.second();

                if (note == null) {
                    deleteNote(coordinate[0], coordinate[1], false, false);
                } else {
                    setNote(coordinate[0], coordinate[1], note, false);
                }

            }

            recordIdx--;
            reduce();

        }
    }

    public void redo() {
        if (recordIdx + 1 < record.size()) {
            recordIdx++;
            for (Pair<int[], NoteblockNote> data : record.get(recordIdx).second()) {
                int[] coordinate = data.first();
                NoteblockNote note = data.second();

                if (note == null) {
                    deleteNote(coordinate[0], coordinate[1], false, false);
                } else {
                    setNote(coordinate[0], coordinate[1], note, false);
                }

            }

            reduce();

        }

    }

    public void setPos1(int x, int y) {
        pos1 = new int[]{x, y};
        if (pos2 == null) pos2 = new int[]{x, y};
        highlight();
    }

    public void setPos2(int x, int y) {
        pos2 = new int[]{x, y};
        if (pos1 == null) pos1 = new int[]{x, y};
        highlight();
    }

    public void setPos(int x1, int y1, int x2, int y2) {
        pos1 = new int[]{x1, y1};
        pos2 = new int[]{x2, y2};
        highlight();
    }

    public void resetPos() {
        pos1 = null;
        pos2 = null;
        unhighlight();
    }

    private void highlight() {
        unhighlight();

        int startX = Math.min(pos1[0], pos2[0]);
        int endX = Math.max(pos1[0], pos2[0]);
        int startY = Math.min(pos1[1], pos2[1]);
        int endY = Math.max(pos1[1], pos2[1]);

        List<String> displayUUIDs = new ArrayList<>();

        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {

                location.set(x + i, y + 1, z + j);
                BlockDisplay selectDisplay = (BlockDisplay) world.spawnEntity(location, EntityType.BLOCK_DISPLAY);
                selectDisplay.setBlock(SELECT_BLOCKDATA);
                selectDisplay.setTransformation(SELECT_TRANSFORMATION);
                displayUUIDs.add(selectDisplay.getUniqueId().toString());


            }
        }

        plugin.getConfig().set("sheet." + name + ".select", displayUUIDs);

    }

    private void unhighlight() {
        plugin.getConfig().getStringList("sheet." + name + ".select").forEach(uuid -> {
            Entity entity = Bukkit.getEntity(UUID.fromString(uuid));
            if (entity != null) {
                entity.remove();
            }
        });
        plugin.getConfig().set("sheet." + name + ".select", null);
    }

    private void modifyNoteWithPos(QuadFunction function) {
        if (pos1 == null || pos2 == null) {
            return;
        }
        function.apply(pos1[0], pos1[1], pos2[0], pos2[1]);

    }

    public void copy(int x1, int y1, int x2, int y2) {
        int startX = Math.min(x1, x2);
        int endX = Math.max(x1, x2);
        int startY = Math.min(y1, y2);
        int endY = Math.max(y1, y2);

        int length = endX - startX + 1;
        int line = endY - startY + 1;

        copiedNotes = new NoteblockNote[length][line];

        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                copiedNotes[i - startX][j - startY] = plugin.getConfig().getSerializable("sheet." + name + ".note." + i + "." + j + ".note", NoteblockNote.class);
            }
        }

    }

    public void copy() {
        modifyNoteWithPos(this::copy);
    }

    public void paste(int x, int y) {
        if (copiedNotes == null) {
            return;
        }

        if (record.size() != recordIdx + 1) {
            record.subList(recordIdx + 1, record.size()).clear();
        }

        int length = copiedNotes.length;
        int line = copiedNotes[0].length;

        List<Pair<int[], NoteblockNote>> previousData = new ArrayList<>();
        List<Pair<int[], NoteblockNote>> modifiedData = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < line; j++) {
                if (copiedNotes[i][j] != null) {

                    int a = x + i;
                    int b = y + j;

                    NoteblockNote previousNote = plugin.getConfig().getSerializable("sheet." + name + ".note." + a + "." + b + ".note", NoteblockNote.class);

                    if (copiedNotes[i][j].equals(previousNote)) {
                        continue;
                    }

                    int[] coordinate = {a, b};

                    previousData.add(new Pair<>(coordinate, previousNote));
                    modifiedData.add(new Pair<>(coordinate, copiedNotes[i][j]));

                    setNote(a, b, copiedNotes[i][j], false);
                }
            }
        }

        if (!previousData.isEmpty()) {
            record.add(new Pair<>(previousData, modifiedData));
            recordIdx++;
        }

    }

    public void delete(int x1, int y1, int x2, int y2) {

        if (record.size() != recordIdx + 1) {
            record.subList(recordIdx + 1, record.size()).clear();
        }

        int startX = Math.min(x1, x2);
        int endX = Math.max(x1, x2);
        int startY = Math.min(y1, y2);
        int endY = Math.max(y1, y2);

        List<Pair<int[], NoteblockNote>> previousData = new ArrayList<>();
        List<Pair<int[], NoteblockNote>> modifiedData = new ArrayList<>();

        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                NoteblockNote previousNote = plugin.getConfig().getSerializable("sheet." + name + ".note." + i + "." + j + ".note", NoteblockNote.class);

                if (previousNote == null) {
                    continue;
                }

                int[] coordinate = {i, j};

                previousData.add(new Pair<>(coordinate, previousNote));
                modifiedData.add(new Pair<>(coordinate, null));

                deleteNote(i, j, false, false);
            }
        }

        if (!previousData.isEmpty()) {
            record.add(new Pair<>(previousData, modifiedData));
            recordIdx++;
        }

        reduce();

    }

    public void delete() {
        modifyNoteWithPos(this::delete);
    }

    public void cut(int x1, int y1, int x2, int y2) {
        copy(x1, y1, x2, y2);
        delete(x1, y1, x2, y2);
    }

    public void cut() {
        modifyNoteWithPos(this::cut);
    }

    public void saveClipboard(String clipboardName, int x1, int y1, int x2, int y2) {

        copy(x1, y1, x2, y2);
        for (int i = 0; i < copiedNotes.length; i++) {
            for (int j = 0; j < copiedNotes[i].length; j++) {
                if (copiedNotes[i][j] != null) {
                    plugin.getConfig().set("sheet." + name + ".clipboard." + clipboardName + "." + i + "." + j, copiedNotes[i][j]);
                }
            }
        }

    }

    public void saveClipboard(String clipboardName) {

        if (pos1 == null || pos2 == null) {
            return;
        }

        saveClipboard(clipboardName, pos1[0], pos1[1], pos2[0], pos2[1]);

    }

    public void loadClipboard(String clipboardName) {

        ConfigurationSection clipboardSection = plugin.getConfig().getConfigurationSection("sheet." + name + ".clipboard." + clipboardName);
        if (clipboardSection == null) {
            return;
        }

        copiedNotes = new NoteblockNote[clipboardSection.getKeys(false).size()][clipboardSection.getKeys(false).size()];

        for (String i : clipboardSection.getKeys(false)) {
            ConfigurationSection clipboardSection2 = clipboardSection.getConfigurationSection(i);
            if (clipboardSection2 == null) {
                continue;
            }
            for (String j : clipboardSection2.getKeys(false)) {
                copiedNotes[Integer.parseInt(i)][Integer.parseInt(j)] = clipboardSection2.getSerializable(j, NoteblockNote.class);
            }
        }

    }

    public void deleteClipboard(String clipboardName) {

        plugin.getConfig().set("sheet." + name + ".clipboard." + clipboardName, null);

    }

    public List<String> getClipboardList() {

        ConfigurationSection clipboardSection = plugin.getConfig().getConfigurationSection("sheet." + name + ".clipboard");
        if (clipboardSection == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(clipboardSection.getKeys(false));

    }


    public void expand(int x1, int y1, int x2, int y2) {

        if (record.size() != recordIdx + 1) {
            record.subList(recordIdx + 1, record.size()).clear();
        }

        int startX = Math.min(x1, x2);
        int endX = Math.max(x1, x2);
        int startY = Math.min(y1, y2);
        int endY = Math.max(y1, y2);

        List<Pair<int[], NoteblockNote>> previousData = new ArrayList<>();
        List<Pair<int[], NoteblockNote>> modifiedData = new ArrayList<>();

        for (int i = endX; i > startX; i--) {
            for (int j = endY; j >= startY; j--) {
                NoteblockNote note = plugin.getConfig().getSerializable("sheet." + name + ".note." + i + "." + j + ".note", NoteblockNote.class);

                if (note == null) {
                    continue;
                }

                int[] coordinate = {i, j};
                int newCoordinateX = startX + (i - startX) * 2;
                int[] newCoordinate = {newCoordinateX, j};

                previousData.add(0, new Pair<>(coordinate, note));
                previousData.add(0, new Pair<>(newCoordinate, null));
                modifiedData.add(new Pair<>(coordinate, null));
                modifiedData.add(new Pair<>(newCoordinate, note));

                deleteNote(i, j, false, false);
                setNote(startX + (i - startX) * 2, j, note, false);
            }
        }

        if (!previousData.isEmpty()) {
            record.add(new Pair<>(previousData, modifiedData));
            recordIdx++;
        }
    }

    public void expand() {
        modifyNoteWithPos(this::expand);
    }

    public void collapse(int x1, int y1, int x2, int y2) {

        if (record.size() != recordIdx + 1) {
            record.subList(recordIdx + 1, record.size()).clear();
        }

        int startX = Math.min(x1, x2);
        int endX = Math.max(x1, x2);
        int startY = Math.min(y1, y2);
        int endY = Math.max(y1, y2);

        List<Pair<int[], NoteblockNote>> previousData = new ArrayList<>();
        List<Pair<int[], NoteblockNote>> modifiedData = new ArrayList<>();

        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                NoteblockNote note = plugin.getConfig().getSerializable("sheet." + name + ".note." + i + "." + j + ".note", NoteblockNote.class);

                if (note == null) {
                    continue;
                }

                int[] coordinate = {i, j};
                int newCoordinateX = startX + (i - startX) / 2;
                int[] newCoordinate = {newCoordinateX, j};

                previousData.add(0, new Pair<>(coordinate, note));
                previousData.add(0, new Pair<>(newCoordinate, null));
                modifiedData.add(new Pair<>(coordinate, null));
                modifiedData.add(new Pair<>(newCoordinate, note));

                deleteNote(i, j, false, false);
                setNote(startX + (i - startX) / 2, j, note, false);
            }
        }

        if (!previousData.isEmpty()) {
            record.add(new Pair<>(previousData, modifiedData));
            recordIdx++;
        }
    }

    public void collapse() {
        modifyNoteWithPos(this::collapse);
    }

    private void noteModify(int x, int y, UnaryOperator<NoteblockNote> function) {
        NoteblockNote note = plugin.getConfig().getSerializable("sheet." + name + ".note." + x + "." + y + ".note", NoteblockNote.class);
        if (note == null) {
            return;
        }
        note = NoteblockUtil.copyNote(note);
        note = function.apply(note);
        setNote(x, y, note, true);
    }

    public void upNote(int x, int y) {
        noteModify(x, y, NoteblockNote::upNote);
    }

    public void downNote(int x, int y) {
        noteModify(x, y, NoteblockNote::downNote);
    }

    public void upOctave(int x, int y) {
        noteModify(x, y, NoteblockNote::upOctave);
    }

    public void downOctave(int x, int y) {
        noteModify(x, y, NoteblockNote::downOctave);
    }

    public void upVolume(int x, int y) {
        noteModify(x, y, NoteblockNote::upVolume);
    }

    public void downVolume(int x, int y) {
        noteModify(x, y, NoteblockNote::downVolume);
    }

    public void upVolume10(int x, int y) {
        noteModify(x, y, NoteblockNote::upVolume10);
    }

    public void downVolume10(int x, int y) {
        noteModify(x, y, NoteblockNote::downVolume10);
    }

    public void changeInstrument(int x, int y, String instrument) {
        noteModify(x, y, note -> {
            note.setInstrument(instrument);
            return note;
        });
    }

    private void modifyNoteAll(int x1, int y1, int x2, int y2, UnaryOperator<NoteblockNote> function) {
        if (record.size() != recordIdx + 1) {
            record.subList(recordIdx + 1, record.size()).clear();
        }

        int startX = Math.min(x1, x2);
        int endX = Math.max(x1, x2);
        int startY = Math.min(y1, y2);
        int endY = Math.max(y1, y2);

        List<Pair<int[], NoteblockNote>> previousData = new ArrayList<>();
        List<Pair<int[], NoteblockNote>> modifiedData = new ArrayList<>();

        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                NoteblockNote previousNote = plugin.getConfig().getSerializable("sheet." + name + ".note." + i + "." + j + ".note", NoteblockNote.class);

                if (previousNote == null) {
                    continue;
                }

                int[] coordinate = {i, j};

                NoteblockNote modifiedNote = function.apply(NoteblockUtil.copyNote(previousNote));

                previousData.add(new Pair<>(coordinate, previousNote));
                modifiedData.add(new Pair<>(coordinate, modifiedNote));

                setNote(i, j, modifiedNote, false);
            }
        }

        if (!previousData.isEmpty()) {
            record.add(new Pair<>(previousData, modifiedData));
            recordIdx++;
        }
    }

    public void upNoteAll(int x1, int y1, int x2, int y2) {
        modifyNoteAll(x1, y1, x2, y2, NoteblockNote::upNote);
    }

    public void upNoteAll() {
        modifyNoteWithPos(this::upNoteAll);
    }

    public void upOctaveAll(int x1, int y1, int x2, int y2) {
        modifyNoteAll(x1, y1, x2, y2, NoteblockNote::upOctave);
    }

    public void upOctaveAll() {
        modifyNoteWithPos(this::upOctaveAll);
    }

    public void downNoteAll(int x1, int y1, int x2, int y2) {
        modifyNoteAll(x1, y1, x2, y2, NoteblockNote::downNote);
    }

    public void downNoteAll() {
        modifyNoteWithPos(this::downNoteAll);
    }

    public void downOctaveAll(int x1, int y1, int x2, int y2) {
        modifyNoteAll(x1, y1, x2, y2, NoteblockNote::downOctave);
    }

    public void downOctaveAll() {
        modifyNoteWithPos(this::downOctaveAll);
    }

    public void upVolumeAll(int x1, int y1, int x2, int y2) {
        modifyNoteAll(x1, y1, x2, y2, NoteblockNote::upVolume);
    }

    public void upVolumeAll() {
        modifyNoteWithPos(this::upVolumeAll);
    }

    public void downVolumeAll(int x1, int y1, int x2, int y2) {
        modifyNoteAll(x1, y1, x2, y2, NoteblockNote::downVolume);
    }

    public void downVolumeAll() {
        modifyNoteWithPos(this::downVolumeAll);
    }

    public void upVolume10All(int x1, int y1, int x2, int y2) {
        modifyNoteAll(x1, y1, x2, y2, NoteblockNote::upVolume10);
    }

    public void upVolume10All() {
        modifyNoteWithPos(this::upVolume10All);
    }

    public void downVolume10All(int x1, int y1, int x2, int y2) {
        modifyNoteAll(x1, y1, x2, y2, NoteblockNote::downVolume10);
    }

    public void downVolume10All() {
        modifyNoteWithPos(this::downVolume10All);
    }

    public void changeInstrumentAll(int x1, int y1, int x2, int y2, String instrument) {
        modifyNoteAll(x1, y1, x2, y2, note -> {
            note.setInstrument(instrument);
            return note;
        });
    }


    public void remove() {

        unhighlight();

        ConfigurationSection entitySection = plugin.getConfig().getConfigurationSection("sheet." + name + ".entity");
        if (entitySection != null) {
            for (String i : entitySection.getKeys(false)) {
                Entity entity = Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(entitySection.getString(i))));
                if (entity != null) {
                    entity.remove();
                }
            }
        }

        removeText:
        {
            ConfigurationSection textDisplaySection1 = plugin.getConfig().getConfigurationSection("sheet." + name + ".note");
            if (textDisplaySection1 == null) {
                break removeText;
            }
            for (String i : textDisplaySection1.getKeys(false)) {
                ConfigurationSection textDisplaySection2 = textDisplaySection1.getConfigurationSection(i);
                if (textDisplaySection2 == null) {
                    continue;
                }
                for (String j : textDisplaySection2.getKeys(false)) {
                    String displayUUID = textDisplaySection2.getString(j + ".display");
                    if (displayUUID == null) {
                        continue;
                    }
                    Entity entity = Bukkit.getEntity(UUID.fromString(displayUUID));
                    if (entity != null) {
                        entity.remove();
                    }
                }
            }
        }

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < line; j++) {
                location.set(x + i, y, z + j);
                world.getBlockAt(location).setType(Material.AIR);
            }
        }

        plugin.getConfig().set("sheet." + name, null);

    }

}
