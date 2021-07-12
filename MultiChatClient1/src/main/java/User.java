import com.github.javafaker.Faker;

public class User {
    private String name;
    private String message;

    public User() {
        Faker faker = new Faker();
        name = faker.name().firstName();
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
