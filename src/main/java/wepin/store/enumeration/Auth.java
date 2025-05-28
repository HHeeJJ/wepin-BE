package wepin.store.enumeration;

public enum Auth {
    ADMIN("ADMIN", "ADMIN"),
    USER("USER", "USER");

    String code;
    String name;

    Auth(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

}
