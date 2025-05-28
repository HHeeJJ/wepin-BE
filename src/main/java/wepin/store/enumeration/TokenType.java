package wepin.store.enumeration;

public enum TokenType {
    REFRESH("REFRESH", "REFRESH"),
    ACCESS("ACCESS", "ACCESS"),
    TYPE("TYPE", "TYPE");

    String code;
    String name;

    TokenType(String code, String name) {
        this.code    = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

}
