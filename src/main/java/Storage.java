import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Deals with loading tasks from, and saving tasks to, the save file on disk.
 * Knows the on-disk text format so the rest of the app only ever has to
 * think in terms of {@link Task} objects.
 */
public class Storage {
    private final File file;

    /** Creates a Storage that reads/writes {@code folderName/fileName}. */
    public Storage(String folderName, String fileName) {
        this.file = new File(folderName, fileName);
    }

    /**
     * Loads previously saved tasks from disk. If the save file doesn't exist
     * yet (e.g. first run), an empty list is returned. A line that can't be
     * parsed is skipped with a warning, without stopping the rest of the
     * file from loading.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!file.exists()) {
            return tasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    try {
                        tasks.add(lineToTask(line));
                    } catch (RockyException e) {
                        System.out.println("     Warning: skipped an unreadable saved task.");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("     Warning: couldn't read saved tasks.");
        }

        return tasks;
    }

    /** Saves the given tasks to disk, overwriting whatever was there before. */
    public void save(ArrayList<Task> tasks) {
        try {
            ensureFileExists();
            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : tasks) {
                    writer.write(taskToLine(task) + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("     Warning: couldn't save tasks (" + e.getMessage() + ").");
        }
    }

    private void ensureFileExists() throws IOException {
        File folder = file.getParentFile();
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    private String taskToLine(Task task) {
        String done = task.isDone() ? "1" : "0";
        String line = " | " + done + " | " + task.getDescription();

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D" + line + " | " + deadline.getDate();
        } else if (task instanceof Event) {
            Event event = (Event) task;
            return "E" + line + " | " + event.getStartDate() + " | " + event.getEndDate();
        } else {
            return "T" + line;
        }
    }

    private Task lineToTask(String line) throws RockyException {
        String[] parts = line.split("\\|");
        String type = parts[0].trim();
        boolean isDone = parts[1].trim().equals("1");
        String description = parts[2].trim();

        Task task;
        switch (type) {
            case "D":
                task = new Deadline(description, parts[3].trim());
                break;
            case "E":
                task = new Event(description, parts[3].trim(), parts[4].trim());
                break;
            default:
                task = new ToDo(description);
                break;
        }

        if (isDone) {
            task.mark();
        }
        return task;
    }
}
