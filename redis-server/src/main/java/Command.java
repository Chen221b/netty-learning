public class Command {

    private String action;
    private String key;
    private String value;

    public Command(String[] command) {
        this.action = command[0];
        this.key = command[1];
        this.value = command[2];
    }


}
