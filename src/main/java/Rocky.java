import java.util.Scanner;

public class Rocky {
    public static void main(String[] args) {
        String[] storage = new String[100];
        String banner =
                " ____   ___   ____ _  ______   __\n"
                        + "|  _ \\ / _ \\ / ___| |/ /\\ \\ / /\n"
                        + "| |_) | | | | |   | ' /  \\ V / \n"
                        + "|  _ <| |_| | |___| . \\   | |  \n"
                        + "|_| \\_\\\\___/ \\____|_|\\_\\  |_|  \n";

        String line = "    ____________________________________________________________";

        System.out.println(line);
        System.out.println(banner);
        System.out.println("Hello! I'm Rocky.");
        System.out.println("What can I do for you?");
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);
        int counter = 0;
        while (true) {
            String input = scanner.nextLine();
            System.out.println(line);

            if (input.equals("bye")) {
                System.out.println("     Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            } else if (input.equals("list")){
                for (String item : storage) {
                    if (item != null) {
                        System.out.println(item);
                    }
                }
            } else {
                storage[counter] = (counter + 1) + ". " + input;
                counter++;
                System.out.println("     " + "added: "+ input);
                System.out.println(line);
            }
        }

        scanner.close();
    }
}