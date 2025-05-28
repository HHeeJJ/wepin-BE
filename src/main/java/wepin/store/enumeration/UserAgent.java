package wepin.store.enumeration;

public enum UserAgent {
    WEB("WEB", "WEB"),
    ANDROID("ANDROID", "ANDROID"),
    IOS("IOS", "IOS");

    String code;
    String name;

    UserAgent(String code, String name) {
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
