public class Rocky {
    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = "duke.txt";
    private static final Storage storage = new Storage(DATA_DIR, DATA_FILE);
    private static final TaskList tasks = new TaskList(storage.load());
    private static final Ui ui = new Ui();

    public static void main(String[] args) {
        ui.showWelcome();

        while (true) {
            String input = ui.readCommand();
            String commandWord = Parser.getCommandWord(input);
            ui.showLine();

            try {
                if (input.equals("bye")) {
                    ui.showGoodbye();
                    ui.showLine();
                    break;
                } else if (input.equals("list")) {
                    printList();
                } else if (commandWord.equals("mark")) {
                    setDone(input, true);
                } else if (commandWord.equals("unmark")) {
                    setDone(input, false);
                } else if (commandWord.equals("delete")) {
                    deleteTask(input);
                } else {
                    TaskType type = TaskType.fromKeyword(commandWord);
                    if (type != null) {
                        addTask(type, input);
                    } else {
                        throw new RockyException(
                                "Hmm, \"" + commandWord + "\" isn't a command I know.");
                    }
                }
            } catch (RockyException e) {
                ui.showError(e.getMessage());
            }

            ui.showLine();
        }

        ui.close();
    }

    private static void printList() {
        ui.showTaskList(tasks);
    }

    private static void addTask(TaskType type, String input) throws RockyException {
        Task task = Parser.parseNewTask(type, input);
        if (task == null) {
            return;
        }

        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private static void deleteTask(String input) throws RockyException {
        int index = Parser.parseIndex(input, tasks.size());
        Task removed = tasks.delete(index);
        storage.save(tasks);
        ui.showTaskRemoved(removed, tasks.size());
    }

    private static void setDone(String input, boolean done) throws RockyException {
        int index = Parser.parseIndex(input, tasks.size());
        Task task = tasks.get(index);

        if (done) {
            task.mark();
        } else {
            task.unmark();
        }
        storage.save(tasks);

        if (done) {
            ui.showTaskMarked(task);
        } else {
            ui.showTaskUnmarked(task);
        }
    }

}
